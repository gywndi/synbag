package net.gywn;

import static spark.Spark.*;

import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import com.google.gson.Gson;
import net.gywn.common.Req;
import net.gywn.common.ReqType;
import net.gywn.common.Res;
import net.gywn.common.SynbagConfig;

public class SynbagServer {
    public static DataSource DS;
    public static Gson gson = new Gson();

    public static void main(String[] argv) throws Exception {

        SynbagConfig synbagConfig = SynbagConfig.loadAndConfigSynbag("synbag-config.yml");
        DS = synbagConfig.getShardingDatasource();

        // Port
        port(80);

        // ==========================
        // Routing each tables
        // ==========================
        List<String> routeTables = new ArrayList<String>();

        // Sharding tables to route
        for (String tb : synbagConfig.getShardingTables().keySet()) {
            routeTables.add(tb);
        }

        // Broadcast tables to route
        routeTables.addAll(synbagConfig.getBroadcastTables());

        // ==========================
        // Routing query request
        // ==========================
        post("/q", (request, response) -> {
            System.out.println("/q for query request");
            String query = request.queryParams("query");
            Req req = new Req(query);
            Res res = req.getType().getRes(null, req);
            return gson.toJson(res);
        });
    }
}
