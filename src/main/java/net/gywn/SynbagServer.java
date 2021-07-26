package net.gywn;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.sql.DataSource;

import net.gywn.common.SynbagConfig;

public class SynbagServer {
    public static void main(String[] argv) throws Exception{
        SynbagConfig synbagConfig = SynbagConfig.loadAndConfigSynbag("synbag-config.yml");
        System.out.println(synbagConfig);

        DataSource ds = synbagConfig.getShardingDatasource();
        Connection conn = ds.getConnection();

		PreparedStatement pstmt = null;
        pstmt = conn.prepareStatement("delete from user");
        int rows = pstmt.executeUpdate();
        // System.out.println(rows+" deleted");
        pstmt.close();

        pstmt = conn.prepareStatement("insert ignore into user(userid, name, dttm)values (?,?, now(6))");
		for (int i = 0; i < 100; i++) {
			pstmt.setString(1, "abcde" + i);
			pstmt.setString(2, "abcde-name" + i);
			pstmt.executeUpdate();
			pstmt.clearParameters();
		}
		pstmt.close();

		pstmt = conn.prepareStatement("insert ignore into config (ver, info) values (?,?)");
		for (int i = 0; i < 10; i++) {
			pstmt.setString(1, "0." + i);
			pstmt.setString(2, "version:" + i);
			pstmt.executeUpdate();
			pstmt.clearParameters();
		}
		pstmt.close();
        conn.close();
    }
}
