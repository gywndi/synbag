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
```
Usage: <main class> [-c=<configFile>] [-p=<port>] [-r=<route>]
  -c, --config-file=<configFile>
                        Config file
  -p, --port=<port>     API server port
  -r, --route=<route>   Route home
```

### Simple REST API server test
```bash
$ java -jar target/synbag-0.0.1.jar

## Query
$ curl -X POST --data-urlencode "query=select * from user" 127.0.0.1:5280/q
{"rowCount":2,"rows":[{"dttm":"2021-07-26 13:29:51.644552","name":"abcde-name0","userid":"abcde0"},{"dttm":"2021-07-26 13:29:51.647925","name":"abcde-name1","userid":"abcde1"}]}

## INSERT
$ curl -X POST -d '{type=INS, extra={value={ver=4,info=v.4}}}' 127.0.0.1:5280/t/config

## UPDATE
$ curl -X POST -d '{type=UPD, extra={value={info=v.5}, where={ver=4}}}' 127.0.0.1:5280/t/config

## DELETE
$ curl -X POST -d '{type=DEL, extra={where={ver=4}}}' 127.0.0.1:5280/t/config

## GET
$ curl -X POST -d '{type=GET, extra={where={userid=abcde51}}}' 127.0.0.1:5280/t/user

## GET ALL
$ curl -X POST -d '{type=GET}' 127.0.0.1:5280/t/user 

## GET with extra options
$ curl -X POST -d '{type=GET, extra={orderBy="name desc"}}' 127.0.0.1:5280/t/user
$ curl -X POST -d '{type=GET, extra={orderBy="name desc", limit=10}}' 127.0.0.1:5280/t/user
$ curl -X POST -d '{type=GET, extra={orderBy="name desc", limit=10, offset=5}}' 127.0.0.1:5280/t/user 
$ curl -X POST -d '{type=GET, extra={orderBy="name desc", clause="name like \"%abcde-name8%\""}}' 127.0.0.1:5280/t/user
```

Still developping.. Enjoy.