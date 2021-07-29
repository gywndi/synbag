package net.gywn.common;

import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
public class ShardingTable {
    private static final Logger logger = LoggerFactory.getLogger(ShardingTable.class);

    private String realTableName;
    private String shardingColumn;
    private String shardingMethod;
    private PreciseShardingAlgorithm<Comparable<?>> preciseShardingAlgorithm;
}
