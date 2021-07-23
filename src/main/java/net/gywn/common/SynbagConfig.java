package net.gywn.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import javax.sql.DataSource;

import lombok.Data;

@Data
public class SynbagConfig {
    private DataSource[] dataSources;
    private SynbagTable[] shardingTables;
    private SynbagTable[] broadcastTables;
    private DataSource shardingDatasource;

    public static SynbagConfig loadSynbagConfig(final String uldraConfigFile) throws FileNotFoundException, UnsupportedEncodingException{
        FileInputStream fileInputStream = new FileInputStream(new File(uldraConfigFile));
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
        SynbagConfig config = new Yaml(new Constructor(SynbagConfig.class)).loadAs(inputStreamReader,SynbagConfig.class);
        return config;
    }
}