package net.gywn;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.StandardShardingStrategyConfiguration;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;

import net.gywn.algorithm.PreciseShardingCRC32;

public class ShardingSphereJava {

	private final static PreciseShardingAlgorithm<Comparable<?>> preciseShardingAlgorithm = new PreciseShardingCRC32();

	public static void main(String[] argv) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;

		// =============================
		// DataSource map
		// =============================
		Map<String, DataSource> dsMap = new HashMap<String, DataSource>();
		String[] databases = { "shard01", "shard02" };
		for (int i = 0; i < databases.length; i++) {
			BasicDataSource tds = new BasicDataSource();
			tds.setUrl("jdbc:mysql://127.0.0.1:3306/" + databases[i] + "?autoReconnect=true&useSSL=false");
			tds.setUsername(databases[i]);
			tds.setPassword(databases[i]);
			tds.setMaxTotal(10);
			dsMap.put("ds" + i, tds);
		}

		// =============================
		// Sharding table
		// =============================
		ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();

		String table = "user";
		String shardKey = "userid";

		// real target table
		TableRuleConfiguration tableRule = new TableRuleConfiguration(table,
				String.format("ds${0..%d}.%s", dsMap.size() - 1, table));

		// custom sharding rule
		tableRule.setDatabaseShardingStrategyConfig(
				new StandardShardingStrategyConfiguration(shardKey, preciseShardingAlgorithm));
		shardingRuleConfig.getTableRuleConfigs().add(tableRule);
		
		// regist sharding table
		shardingRuleConfig.getBindingTableGroups().add(table);

		// Broadcast table
		shardingRuleConfig.getBroadcastTables().add("config");

		// =============================
		// Sharding datasource
		// =============================
		DataSource ds = ShardingDataSourceFactory.createDataSource(dsMap, shardingRuleConfig, null);

		conn = ds.getConnection();
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
