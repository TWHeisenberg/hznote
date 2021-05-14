package com.hznc.datacenter.hbase;

import com.hznc.datacenter.bean.KData;
import com.hznc.datacenter.bean.cnative.CKData;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

public class MyHbaseClient implements HbaseClient {

  private static Logger logger = Logger.getLogger(MyHbaseClient.class);
  // 与hbase数据库连接对象
  public Connection connection;
  // 数据库元数据操作对象
  public Admin admin;
  // 单例模式
  private static volatile MyHbaseClient client;
  // 批量插入的缓存数量
  private int putsBatchSize;


  Map<String, List<Put>> putsmap = new HashMap();

  // 批量插入的缓存 TODO: 是否可以优化，这样容易丢数据
  public void loadPutsCache() {
    putsmap.put("hznc_mkdata:tickdata", new ArrayList());
    putsmap.put("hznc_mkdata:1sdata", new ArrayList());
    putsmap.put("hznc_mkdata:2sdata", new ArrayList());
    putsmap.put("hznc_mkdata:3sdata", new ArrayList());
    putsmap.put("hznc_mkdata:5sdata", new ArrayList());
    putsmap.put("hznc_mkdata:10sdata", new ArrayList());
    putsmap.put("hznc_mkdata:15sdata", new ArrayList());
    putsmap.put("hznc_mkdata:30sdata", new ArrayList());
    putsmap.put("hznc_mkdata:1mindata", new ArrayList());
  }

  MyHbaseClient(){}
  MyHbaseClient(Map<String, String> prop) throws Exception {
    String zkServerIp = prop.get("zkServerIp");
    String zkServerPort = prop.get("zkServerPort");

    putsBatchSize = Integer.parseInt(prop.getOrDefault("putsBatchSize", "100"));
    this.setUp(zkServerIp, zkServerPort);
  }

  public static MyHbaseClient getInstance(Map<String, String> prop) throws Exception {
    if (client == null) {
      synchronized (MyHbaseClient.class) {
        if (client == null) {
          client = new MyHbaseClient(prop);
        }
      }
    }
    return client;
  }

  // 连接配置
  public void setUp(String zkServerIp, String zkServerPort) throws Exception {
    loadPutsCache();
    // 取得一个数据库配置参数对象
    Configuration conf = HBaseConfiguration.create();
    // 设置连接参数：hbase数据库所在的主机ip
    conf.set("hbase.zookeeper.quorum", zkServerIp);
    // 设置连接参数：hbase数据库使用的接口
    conf.set("hbase.zookeeper.property.clientPort", zkServerPort);

    // 获取数据库连接对象, 多线程去插入
    int corePoolSize = 4;
    int maximumPoolSize = 10;
    long kpAlive = 60L;
    ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, kpAlive, TimeUnit.SECONDS, new SynchronousQueue() );
    connection = ConnectionFactory.createConnection(conf, executor);
    // 获取一个数据库元数据操作对象
    admin = connection.getAdmin();
    System.out.println("HBaseDemo.setUp()->admin :" + admin);
  }


  // 插入单条数据
  public void singlePut(String tablename, KData value, boolean isTick) throws IOException {
    //获取数据表对象
    Table table = connection.getTable(TableName.valueOf(tablename));
    String rowkey = value.getRowKey(isTick);
    Put put = convertToPut(value, isTick);
    table.put(put);
    table.close();
    logger.debug("put "+rowkey+" to "+tablename+" ok.");
  }


  // 保存缓存中残留的数据
  public void clearRemainingPuts() throws IOException {

    Set<Entry<String, List<Put>>> entries = putsmap.entrySet();
    for(Entry<String, List<Put>> entry : entries){
      String tablename = entry.getKey();
      List<Put> puts = entry.getValue();

      // 缓存中的最后数据
      if(!puts.isEmpty()){
        Table table = connection.getTable(TableName.valueOf(tablename));
        table.put(puts);
        puts.clear();
        table.close();
      }
    }

  }

  public void batchPut(String tablename, KData value, boolean isTick) throws IOException {
    List<Put> puts = putsmap.get(tablename);
    String rowkey = value.getRowKey(isTick);
    Put put = convertToPut(value, isTick);

    puts.add(put);

    // 批量插入
    if(puts.size() % putsBatchSize == 0){
      Table table = connection.getTable(TableName.valueOf(tablename));
      table.put(puts);
      table.close();
      puts.clear();
    }

    logger.debug(String.format("put %s to %s ok.", rowkey, tablename));
  }

  public Put convertToPut(KData value, boolean isTick){
    // ROWKEY
    String rowkey = value.getRowKey(isTick);
    Put put = new Put(Bytes.toBytes(rowkey));

    //插入各字段值
    put.addColumn(Bytes.toBytes("cf_data"), Bytes.toBytes("01cSymbol"),
        Bytes.toBytes(String.valueOf(value.getSymbol())));
    put.addColumn(Bytes.toBytes("cf_data"), Bytes.toBytes("02dbClosePrice"),
        Bytes.toBytes(String.valueOf(value.getClosePrice_diff())));
    put.addColumn(Bytes.toBytes("cf_data"), Bytes.toBytes("03dbHeightPrice"),
        Bytes.toBytes(String.valueOf(value.getHighestPrice_diff())));
    put.addColumn(Bytes.toBytes("cf_data"), Bytes.toBytes("04dbLowPrice"),
        Bytes.toBytes(String.valueOf(value.getLowestPrice_diff())));
    put.addColumn(Bytes.toBytes("cf_data"), Bytes.toBytes("05dbOpenPrice"),
        Bytes.toBytes(String.valueOf(value.getOpenPrice_diff())));
    put.addColumn(Bytes.toBytes("cf_data"), Bytes.toBytes("06dbSum"),
        Bytes.toBytes(String.valueOf(value.getTurnover_diff())));
    put.addColumn(Bytes.toBytes("cf_data"), Bytes.toBytes("07dbYTClosePrice"),
        Bytes.toBytes(String.valueOf(value.getYtClosePrice())));
    put.addColumn(Bytes.toBytes("cf_data"), Bytes.toBytes("08uTime"),
        Bytes.toBytes(String.valueOf(value.getUtime(isTick))));
    put.addColumn(Bytes.toBytes("cf_data"), Bytes.toBytes("09uVolume"),
        Bytes.toBytes(String.valueOf(value.getVolume_diff())));
    put.addColumn(Bytes.toBytes("cf_data"), Bytes.toBytes("10uVolume_Sell"),
        Bytes.toBytes(String.valueOf(value.getVolume())));
    put.addColumn(Bytes.toBytes("cf_data"), Bytes.toBytes("11zpos"),
        Bytes.toBytes(String.valueOf(value.getOpenInterest())));
    put.addColumn(Bytes.toBytes("cf_data"), Bytes.toBytes("12zpos_diff"),
        Bytes.toBytes(String.valueOf(value.getOpenInterest_diff())));
    put.addColumn(Bytes.toBytes("cf_data"), Bytes.toBytes("13avgPrice"),
        Bytes.toBytes(String.valueOf(value.getAveragePrice())));

    if(isTick){ // tick数据有21个字段
      put.addColumn(Bytes.toBytes ( "cf_data" ), Bytes. toBytes ( "14tdHighestPrice" ), Bytes.toBytes (String.valueOf(value.getHighestPrice())) );	//当日最高价
      put.addColumn(Bytes.toBytes ( "cf_data" ), Bytes. toBytes ( "15tdLowestPrice" ), Bytes.toBytes (String.valueOf(value.getLowestPrice()) ));		//当日最低价
      put.addColumn(Bytes.toBytes ( "cf_data" ), Bytes. toBytes ( "16UpperLimitPrice" ), Bytes.toBytes (String.valueOf(value.getUpperLimitPrice()) ));//涨停板价
      put.addColumn(Bytes.toBytes ( "cf_data" ), Bytes. toBytes ( "17LowerLimitPrice" ), Bytes.toBytes (String.valueOf(value.getLowerLimitPrice()) ));//跌停板价
      put.addColumn(Bytes.toBytes ( "cf_data" ), Bytes. toBytes ( "18BidPrice1" ), Bytes.toBytes (String.valueOf(value.getBidPrice1()) ));			//申买价一
      put.addColumn(Bytes.toBytes ( "cf_data" ), Bytes. toBytes ( "19BidVolume1" ), Bytes.toBytes (String.valueOf(value.getBidVolume1()) ));			//申买量一
      put.addColumn(Bytes.toBytes ( "cf_data" ), Bytes. toBytes ( "20AskPrice1" ), Bytes.toBytes (String.valueOf(value.getAskPrice1()) ));			//申卖价一
      put.addColumn(Bytes.toBytes ( "cf_data" ), Bytes. toBytes ( "21AskVolume1" ), Bytes.toBytes (String.valueOf(value.getAskVolume1()) ));			//申卖量一
    }
    // 标识数据来源
    put.addColumn(Bytes.toBytes ( "cf_data" ), Bytes. toBytes ( "from" ), Bytes.toBytes ("dc" ));
    return put;
  }

  public KData getKdataByRowkey(String tablename, String rowkey) throws IOException {
    Get get = new Get(Bytes.toBytes(rowkey));

    Table table = admin.getConnection().getTable(TableName.valueOf(tablename));

    Result result = table.get(get);
    KData kdata = new CKData(); // TODO

    kdata.setSymbol(Bytes.toString(result.getValue(Bytes.toBytes("cf_data"), Bytes.toBytes("01cSymbol"))));
    kdata.setClosePrice_diff(toDouble(result.getValue(Bytes.toBytes("cf_data"), Bytes.toBytes("02dbClosePrice"))));
    kdata.setHighestPrice_diff(toDouble(result.getValue(Bytes.toBytes("cf_data"), Bytes.toBytes("03dbHeightPrice"))));
    kdata.setLowestPrice_diff(toDouble(result.getValue(Bytes.toBytes("cf_data"), Bytes.toBytes("04dbLowPrice"))));
    kdata.setOpenPrice_diff(toDouble(result.getValue(Bytes.toBytes("cf_data"), Bytes.toBytes("05dbOpenPrice"))));
    kdata.setTurnover_diff(toDouble(result.getValue(Bytes.toBytes("cf_data"), Bytes.toBytes("06dbSum"))));
    kdata.setYtClosePrice(toDouble(result.getValue(Bytes.toBytes("cf_data"), Bytes.toBytes("07dbYTClosePrice"))));
    kdata.setUtime(toLong(result.getValue(Bytes.toBytes("cf_data"), Bytes.toBytes("08uTime"))));
    kdata.setVolume_diff(toLong(result.getValue(Bytes.toBytes("cf_data"), Bytes.toBytes("09uVolume"))));
    kdata.setVolume(toLong(result.getValue(Bytes.toBytes("cf_data"), Bytes.toBytes("10uVolume_Sell"))));
    kdata.setOpenInterest(toDouble(result.getValue(Bytes.toBytes("cf_data"), Bytes.toBytes("11zpos"))));
    kdata.setOpenInterest_diff(toDouble(result.getValue(Bytes.toBytes("cf_data"), Bytes.toBytes("12zpos_diff"))));
    kdata.setAveragePrice(toDouble(result.getValue(Bytes.toBytes("cf_data"), Bytes.toBytes("13avgPrice"))));

    kdata.setHighestPrice(toDouble(result.getValue(Bytes.toBytes ( "cf_data" ), Bytes. toBytes ( "14tdHighestPrice" ))));	//当日最高价
    kdata.setLowestPrice(toDouble(result.getValue(Bytes.toBytes ( "cf_data" ), Bytes. toBytes ( "15tdLowestPrice" ))));		//当日最低价
    kdata.setUpperLimitPrice(toDouble(result.getValue(Bytes.toBytes ( "cf_data" ), Bytes. toBytes ( "16UpperLimitPrice" ))));//涨停板价
    kdata.setLowerLimitPrice(toDouble(result.getValue(Bytes.toBytes ( "cf_data" ), Bytes. toBytes ( "17LowerLimitPrice" ))));//跌停板价
    kdata.setBidPrice1(toDouble(result.getValue(Bytes.toBytes ( "cf_data" ), Bytes. toBytes ( "18BidPrice1" ))));			//申买价一
    kdata.setBidVolume1(toInt(result.getValue(Bytes.toBytes ( "cf_data" ), Bytes. toBytes ( "19BidVolume1" ))));			//申买量一
    kdata.setAskPrice1(toDouble(result.getValue(Bytes.toBytes ( "cf_data" ), Bytes. toBytes ( "20AskPrice1" ))));			//申卖价一
    kdata.setAskVolume1(toInt(result.getValue(Bytes.toBytes ( "cf_data" ), Bytes. toBytes ( "21AskVolume1" ))));			//申卖量一

    return kdata;
  }

  private double toDouble(byte[] value){
    return Double.valueOf(Bytes.toString(value));
  }

  private long toLong(byte[] value){
    return Long.valueOf(Bytes.toString(value));
  }

  private int toInt(byte[] value){
    return Integer.valueOf(Bytes.toString(value));
  }


}
