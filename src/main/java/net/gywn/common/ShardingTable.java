package net.gywn.common;

import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;

import lombok.Data;

@Data
public class ShardingTable {
    private String realTableName;
    private String shardingColumn;
    private String shardingMethod;
    private PreciseShardingAlgorithm<Comparable<?>> preciseShardingAlgorithm;
}
