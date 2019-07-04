# Hbase

## Hbase简介

- Hbase是一个分布式的、面向列的开源数据库
- Hbase是在Hadoop和HDFS之上提供分布式数据存储
- Hbase适合非结构化数据存储

### Hbase与HDFS

- Hbase建立在Hadoop文件系统之上，利用Hadoop的文件系统的容错能力
- Hbase提供对数据的随机实时读/写访问功能
- Hbase内部使用哈希表，并存储索引，可将在HDFS文件中的数据进行快速查找

### Hbase数据模型

 - RowKey：决定一行记录的唯一标识，Hbase只支持三种查询方式（**`基于RowKey的单行查询`**、**`基于RowKey的范围查询`**、**`全表查询`**），RowKey是按照字典排序的，最多只能存储64k的字节数据

 - Column Family & qualifier：HBase表中每个列（qualifier）都属于某个列族，必须在使用表之前定义。 列名都以列族作为前缀，例如 courses:history、courses:math 都属于 courses 这个列族

 - TimeStamp：Hbase中使用不同的timestame来标识相同rowkey行对应的不同版本的数据。在写入数据时如果用户没有指定对应的timestamp，系统会自动添加一个与系统时间相同的timestamp。相同rowkey的数据按照timestamp倒序排列，默认查询的是最新的版本

 - cell：Cell 是由 {row key，column(=< family> + < qualifier>)，version} 唯一确定的单元。Cell 中的数据是没有类型的，全部是字节码形式存储 

 ### Hbase与传统关系型数据库对比

 ![对比](image/hbasevs.png)

 ## Hbase安装

 ### 下载解压安装包：http://archive.cloudera.com/cdh5/cdh/5/hbase-1.2.0-cdh5.7.0.tar.gz
 ### 修改配置文件
 - hbase-env.sh
 ```bash
 export JAVA_HOME=$JAVA_HOME  
 #默认使用hbase自带的zookeeper  
 export HBASE_MANAGES_ZK=true  
 # Configure PermSize. Only needed in JDK7. You can safely remove it for JDK8+  
 # JDK8需加注释
export HBASE_MASTER_OPTS="$HBASE_MASTER_OPTS -XX:PermSize=128m -XX:MaxPermSize=128m"
export HBASE_REGIONSERVER_OPTS="$HBASE_REGIONSERVER_OPTS -XX:PermSize=128m -XX:MaxPermSize=128m"
```

- hbase-site.xml

|name|value|description|
|---|---|---|
|hbase.rootdir|hdfs://localhost:9000/hbase|regionserver的共享目录，用来持久化Hbase|
|hbase.zookeeper.property.dataDir|/Users/liufukang/app/hbase-1.2.0-cdh5.7.0/zookeeper|快照的存储位置|
|hbase.cluster.distributed|true|是否以分布式模式运行|
|hbase.zookeeper.quorm|localhost|zookeeper集群的地址列表|

- regionservers
> regionserver的地址

### 启动
`start-hbase.sh`

## Hbase基础架构
- 印象笔记已整理，TODO

## Hbase读写流程
- TODO

## Hbase实战
### Hbase Java API
- [开发HBase数据库操作类](https://github.com/kangapp/Hbase/blob/master/HBase/src/main/java/com/hbase/HBaseUtil.java)

- [通过多种过滤器过滤数据，实现HBase高级查询](https://github.com/kangapp/Hbase/blob/master/HBase/src/test/java/com/test/HBaseFilterTest.java)
    - 基于行的过滤器
    > PrefixFilter：行的前缀匹配  
    > PageFilter：基于行的分页
    - 基于列的过滤器
    > ColumnPrefixFilter：列前缀匹配  
    > FirstKeyOnlyFilter：只返回每一行的第一列
    - 基于单元值得过滤器
    > KeyOnlyFilter：返回的数据不包括单元值，只返回行键和列  
    > TimestampsFilter：根据数据的时间戳版本进行过滤
    - 基于列和单元值的过滤器
    > SingleColumnValueFilter：对该列的单元值进行比较过滤
    > SingleColumnValueExcludeFilter：对该列的单元值进行比较过滤
    - 比较过滤器
    > 比较过滤器通常需要一个比较运算符以及一个比较器来实现过滤  
    > RowFilter、FamilyFilter、QualifierFilter、ValueFilter

## HBase优化

### 服务端优化策略

#### HBase概念
- HBase写入时当memstore达到一定的大小会flush到磁盘保存成HFile，当HFile小文件太多会执行compact操作进行合并，会影响数据的读写。
    - minor compaction:选取一些小的、相邻的StoreFile将他们合并成一个更大的StoreFile
    - major compaction:将所有的StoreFile合并成一个StoreFile，清理无意义数据：被删除的数据、TTL过期数据、版本号超过设定版本号的数据
- 当Region的大小达到某一阈值之后，会执行split操作，会影响数据的读写。
    - split:当一个region达到一定的大小就会自动split成两个region

#### 优化
- GC和jvm设置
- hbase-site.xml相关设置

### 常用优化策略

#### 预先分区
- 创建HBase表的时候会自动创建一个Region分区
- 创建HBase表的是有预先创建一些空的Regions
- 将经常访问的数据放到多个region中，不常访问的数据放到一个region中，可以有效防止数据倾斜的问题

### Rowkey优化

- 利用HBase默认排序特点，将一起访问的数据放到一起
- 防止热点问题，避免使用时序或单调的递增递减等

### Column优化
- 列族的名称和列的描述命名尽量简短
- 同一张表中的ColumnFamily的数量不要超过3个

### Schema优化
- 高表和宽表

### 读写优化策略

#### 写优化策略
- 同步批量提交（默认） or 异步批量提交
- WAL优化（默认开启），是否必须，持久化等级

#### 读优化策略
- 客户端：scan缓存设置，批量获取
- 服务端：BlockCache配置是否合理，HFile是否过多
- 表结构设计问题

## HBase备份和恢复

### 数据备份
- CopyTable
```bash
hbase org.apache.hadoop.hbase.mapreduce.CopyTable --new.name=[new tableName] [old tableName]
```
- Export/Import
```bash
hbase org.apache.hadoop.hbase.mapreduce.Export [tableName] [hdfs path]
```
```bash
hbase org.apache.hadoop.hbase.mapreduce.Import [tableNmae] [hdfs path]
```
- Snapshot
> hbase-site.xml hbase.snapshot.enabled = true 开启快照

```bash
#列出快照
> list_snapshots
#创建快照
> snapshot 'FileTable','FileTable_snap'
#删除快照
> delete_snapshot 'FileTable_snap'
#通过快照恢复数据，需要提起disable表
> restore_snapshot 'FileTable_snap'
```
- Replication