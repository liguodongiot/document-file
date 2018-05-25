

[TOC]





## JVM规范



##JVM运行机制

###JVM启动流程

###JVM基本结构

###内存模型

###编译和解释运行的概念





## JVM配置参数

###Trace跟踪参数

`-verbose:gc`
`-XX:printGC`
打印gc的简要信息

`-XX:+PrintGCDetails`

打印gc的详细信息

`-XX:+PrintGCTimeStamps`

打印CG发生的时间戳

`-XX:+PrintHeapAtGC`

每次一次GC后，都打印堆信息

`-XX:+TraceClassLoading`

监控类的加载

`-XX:+PrintClassHistogram`

按下Ctrl+Break后，打印类的信息







###堆的分配参数

`-Xmx –Xms`指定最大堆和最小堆。如-Xmx20m -Xms5m。







###栈的分配参数