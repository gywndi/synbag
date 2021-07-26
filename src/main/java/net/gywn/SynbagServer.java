package net.gywn;

import java.sql.Connection;

import javax.sql.DataSource;

import net.gywn.common.SynbagConfig;

public class SynbagServer {
    public static void main(String[] argv) throws Exception{
        SynbagConfig synbagConfig = SynbagConfig.loadAndConfigSynbag("synbag-config.yml");
        System.out.println(synbagConfig);

        DataSource ds = synbagConfig.getShardingDatasource();
        Connection conn = ds.getConnection();
        conn.close();
    }
}
