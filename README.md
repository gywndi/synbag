# synbag
## synbag is..
1. Simple shardingsphere library
2. Simple shardingsphere REST API server

## Build
```bash
mvn install
```

## Usage
```java
// =================
// Yaml style
// =================
SynbagConfig synbagConfig = SynbagConfig.loadAndConfigSynbag("synbag-config.yml");
DataSource shardingDatasource = synbagConfig.getShardingDatasource();
Connection connection = shardingDatasource.getConnection();
connection.close();

// =================
// Code style
// =================
SynbagConfig synbagConfig = new SynbagConfig();

// DataSource
DataSource[] datasources = ..;
synbagConfig.setDataSources(datasources);

// Sharding table
Map<String, ShardingTable> shardingTables = new HashMap<String, ShardingTable>();
ShardingTable shardingTable = new ShardingTable();
shardingTable.setShardingColumn("userid");
shardingTable.setShardingMethod("crc32");
shardingTables.put("user", shardingTable);
synbagConfig.setShardingTables(shardingTables); // Add sharding policy

// Broadcast table
List<String> broadcastTables = new ArrayList<String>();
synbagConfig.setBroadcastTables(broadcastTables); // Add broadcast policy

// Initialize
synbagConfig.init();
```

## Usage as REST API
```bash
$ java -jar target/synbag-0.0.1.jar

$ curl -X POST -d "query=select * from user" 127.0.0.1/q
{"rowCount":2,"rows":[{"dttm":"2021-07-26 13:29:51.644552","name":"abcde-name0","userid":"abcde0"},{"dttm":"2021-07-26 13:29:51.647925","name":"abcde-name1","userid":"abcde1"}]}
```

Still developping.. Enjoy.