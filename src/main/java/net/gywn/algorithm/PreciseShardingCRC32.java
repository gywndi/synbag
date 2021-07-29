package net.gywn.algorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.zip.CRC32;
import java.util.zip.Checksum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;

public class PreciseShardingCRC32 implements PreciseShardingAlgorithm<Comparable<?>> {
    private static final Logger logger = LoggerFactory.getLogger(PreciseShardingCRC32.class);

	public String doSharding(Collection<String> availableTargetNames,
			PreciseShardingValue<Comparable<?>> shardingValue) {
		ArrayList<String> list = new ArrayList<String>(availableTargetNames);
		Checksum checksum = new CRC32();
		try {
			byte[] bytes = shardingValue.getValue().toString().getBytes();
			checksum.update(bytes, 0, bytes.length);
			int seq = (int) (checksum.getValue() % list.size());
			return list.get(seq);
		} catch (Exception e) {
		}
		return list.get(0);
	}
}