package net.gywn;

import static spark.Spark.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.sql.DataSource;
import com.google.gson.Gson;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.gywn.common.Req;
import net.gywn.common.Res;
import net.gywn.common.SynbagConfig;

import picocli.CommandLine;
import picocli.CommandLine.Option;

public class SynbagServer implements Callable<Integer> {
    private static final Logger logger = LoggerFactory.getLogger(SynbagServer.class);
    static {
		try {
			String loggingConfigFile = System.getProperty("java.util.logging.config.file");
			if (loggingConfigFile == null) {
				loggingConfigFile = "log4j.properties";
			}
			PropertyConfigurator.configure(loggingConfigFile);
		} catch (Exception e) {
		}
	}
    
    public static Gson gson = new Gson();

    @Option(names = { "--config-file",
            "-c" }, description = "Config file", defaultValue = "synbag-config.yml", required = false)
    private String configFile;

    @Option(names = { "--port", "-p" }, description = "API server port", defaultValue = "5280", required = false)
    private Integer port;

    @Option(names = { "--route", "-r" }, description = "Route home", defaultValue = "", required = false)
    private String route;

    public static void main(String[] args) throws Exception {
        Integer exitCode = new CommandLine(new SynbagServer()).execute(args);
        if (exitCode != 0) {
            logger.error("exit code: {}", exitCode);
        }
    }

    @Override
    public Integer call() throws Exception {
        SynbagConfig synbagConfig = SynbagConfig.loadAndConfigSynbag(configFile);
        DataSource ds = synbagConfig.getShardingDatasource();

        // Port
        port(port);

        // ==========================
        // Routing for each tables
        // ==========================
        List<String> routeTables = new ArrayList<String>();

        // Sharding tables to route
        for (String tb : synbagConfig.getShardingTables().keySet()) {
            routeTables.add(tb);
        }

        // Broadcast tables to route
        routeTables.addAll(synbagConfig.getBroadcastTables());

        // Routing
        for (String tb : routeTables) {
            String routingPath = String.format("%s/t/%s", route, tb);
            logger.info("0.0.0.0:{}{}", port, routingPath);
            post(routingPath, (request, response) -> {
                Req req = gson.fromJson(request.body(), Req.class);
                Res res = req.getType().getRes(ds, tb, req);
                return gson.toJson(res);
            });
        }

        // ==========================
        // Routing query request
        // ==========================
        String routingPath = String.format("%s/q", route);
        logger.info("0.0.0.0:{}{}", port, routingPath);
        post("/q", (request, response) -> {
            String query = request.queryParams("query");
            Req req = new Req(query);
            Res res = req.getType().getRes(ds, null, req);
            return gson.toJson(res);
        });

        return 0;
    }
}
