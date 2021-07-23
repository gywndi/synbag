package net.gywn;

import java.io.*;
import java.sql.*;

import javax.sql.DataSource;

import org.apache.shardingsphere.shardingjdbc.api.yaml.YamlShardingDataSourceFactory;

public class ShardingSphereYaml {

	public static void main(String[] argv) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		File file = new File("sharding-config.yml");
		DataSource dataSource = YamlShardingDataSourceFactory.createDataSource(file);
		
		conn = dataSource.getConnection();
		pstmt = conn.prepareStatement("insert ignore into user(userid, name)values (?,?)");
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
