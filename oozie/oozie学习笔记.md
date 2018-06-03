## 简介

oozie是用于hadoop平台的开源工作流调度引擎
用来管理hadoop作业
属于web应用程序，由oozie client和oozie server两个组件构成。
oozie server运行于Java servlet容器（Tomcat）中的web程序



## 作 用

统一调度hadoop系统中常见的MR任务启动，hdfs操作，shell调度，hive操作等
是的复制的依赖关系。时间触发，时间触发使用xml语言进行表达，开发效率提高。
一组任务使用DAG来表示，使用图形表达逻辑更加清晰
支持很多种任务调度，能完成大部分hadoop任务处理
程序定义支持EL常量和函数，表达更丰富。

web管理地址
http:oozie.host.ip:11000/oozie/

## oozie管理

任务列表查看
任务状态查看
流程返回信息
节点信息查看
流程图信息
日志查看
系统信息查看和配置



## oozie cli 命令：

启动任务
oozie job -oozie http://ip:11000/ -config job.properties -run


停止任务
oozie job -oozie http://ip:11000/ -kill 0032-dsda-434343-43fs(任务ID)


提交任务
oozie job -oozie http://ip:11000/ -config job.properties -submit


开始任务
oozie job -oozie http://ip:11000/ -config job.properties -start 0032-dsda-434343-43fs



启动任务
oozie job -oozie http://ip:11000/ -config job.properties -info 0032-dsda-434343-43fs



## JOB 配置 job.properties



## 节点

A.流程控制节点
start-定义workflow开始
end-定义workflow结束
decision-实现switch功能
sub-workflow-调用子workflow
kill-杀死workflow
fork-并发执行workflow
join-并发执行结束（与fork一起使用）


B.动作节点
shell
java
fs
MR
hive
sqoop



shell节点

--job.properties



--workflow.xml





impala --性能优化

快 -- 内存、C++ 、摈弃MR方式、data local

列式存储、数据仓库



impala VS hive关系

缺点：内存、分区太大会有性能损耗，hive依赖太大



架构 

catalog statestore        (impalad 中心协调)



shell -p  explain profile

 -r  refresh

-f -o -i



impala sql 和hive sql

数据类型  复杂类型不支持

一些函数不支持



impala数据处理






























