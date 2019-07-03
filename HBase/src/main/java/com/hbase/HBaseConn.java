package com.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Table;

import java.io.IOException;

public class HBaseConn {

    private static final HBaseConn INSTANCE = new HBaseConn();
    private static Configuration configuration;
    private static Connection connection;

    private HBaseConn(){
        try {
            if(configuration == null) {
                configuration = HBaseConfiguration.create();
                configuration.set("hbase.zookeeper.quorum", "localhost:2181");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private Connection getConnection() {
        if(connection ==null || connection.isClosed()){
            try {
                connection = ConnectionFactory.createConnection(configuration);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return connection;
    }

    public static Connection getHBaseConn() {
        return INSTANCE.getConnection();
    }

    public static Table getTable(String tableName) throws IOException {
        return INSTANCE.getConnection().getTable(TableName.valueOf(tableName));
    }

    public static void closeConn() {
        if(connection != null) {
            try {
                connection.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 测试HBase连接和读取Table
     * @param args
     */
    public static void main(String[] args) {
        Connection conn = getHBaseConn();
        System.out.println(conn.isClosed());
        try {
            Table table = getTable("test");
            System.out.println(table.getName().getNameAsString());
            table.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        closeConn();
        System.out.println(conn.isClosed());
    }
}
