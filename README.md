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

 - cell：Cell 是由 {row key，column(=< family> + < label>)，version} 唯一确定的单元。Cell 中的数据是没有类型的，全部是字节码形式存储