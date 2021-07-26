package net.gywn.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.StandardShardingStrategyConfiguration;
import org.apache.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.sql.DataSource;

import net.gywn.algorithm.PreciseShardingCRC32;

@ToString
public class SynbagConfig {
    private ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();

    @Setter
    private DataSource[] dataSources;

    @Setter
    private Map<String, ShardingTable> shardingTables;

    @Setter
    private List<String> broadcastTables;

    @Getter
    private DataSource shardingDatasource;

    // Load yaml config file and config sharding datasource
    public static SynbagConfig loadAndConfigSynbag(final String configFile) throws Exception {
        try (FileInputStream fileInputStream = new FileInputStream(new File(configFile));
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8")) {

            // Load yaml config file
            SynbagConfig config = new Yaml(new Constructor(SynbagConfig.class)).loadAs(inputStreamReader,
                    SynbagConfig.class);

            // Config sharding datasource
            config.init();

            return config;
        }
    }

    // Config sharding datasource
    public void init() throws Exception {
        // ===========================
        // Check config elements
        // ===========================
        checkConfig();

        // ===========================
        // Sharding table config
        // ===========================
        for (Entry<String, ShardingTable> entry : shardingTables.entrySet()) {

            String logicalTableName = entry.getKey();
            ShardingTable shardingTable = entry.getValue();

            // Sharding table rule
            String datanodes = String.format("ds${0..%d}.%s", dataSources.length - 1, shardingTable.getRealTableName());
            TableRuleConfiguration tableRule = new TableRuleConfiguration(logicalTableName, datanodes);

            // Regist sharding rule
            tableRule.setDatabaseShardingStrategyConfig(new StandardShardingStrategyConfiguration(
                    shardingTable.getShardingColumn(), shardingTable.getPreciseShardingAlgorithm()));
            shardingRuleConfig.getTableRuleConfigs().add(tableRule);

            // Regist sharding table(defined by logical table name)
            shardingRuleConfig.getBindingTableGroups().add(logicalTableName);

        }

        // ===========================
        // Broastcast table config
        // ===========================
        for (String broadcastTable : broadcastTables) {
            shardingRuleConfig.getBroadcastTables().add(broadcastTable);
        }

        // ===========================
        // Set sharding datasource
        // ===========================
        // DataSource array to map
        Map<String, DataSource> map = new HashMap<String, DataSource>();
        for (int i = 0; i < dataSources.length; i++) {
            map.put(String.format("ds%d", i), dataSources[i]);
        }
        shardingDatasource = ShardingDataSourceFactory.createDataSource(map, shardingRuleConfig, null);
    }

    // Check elements in SynbagConfig
    private void checkConfig() throws Exception {

        // check if sharding datasource's been initalized
        if (shardingDatasource != null) {
            throw new Exception("Already initialized");
        }

        // check parameter datasources
        if (dataSources == null) {
            throw new Exception("Sharding datasources null");
        }

        // set shardingTables empty
        if (shardingTables == null) {
            shardingTables = new HashMap<String, ShardingTable>();
        }

        // set broadcastTables empty
        if (broadcastTables == null) {
            broadcastTables = new ArrayList<String>();
        }

        // Set default value for each sharding table
        for (Entry<String, ShardingTable> entry : shardingTables.entrySet()) {

            String logicalTableName = entry.getKey();
            ShardingTable shardingTable = entry.getValue();

            if (shardingTable.getShardingMethod() == null) {
                shardingTable.setShardingMethod("crc32");
            }

            if (shardingTable.getRealTableName() == null) {
                shardingTable.setRealTableName(logicalTableName);
            }

            // Sharding algorithm, default is CRC32
            switch (shardingTable.getShardingMethod().toLowerCase()) {
                case "crc32":
                    shardingTable.setPreciseShardingAlgorithm(new PreciseShardingCRC32());
                    break;
                default:
                    shardingTable.setPreciseShardingAlgorithm(new PreciseShardingCRC32());
            }
        }
    }
}