

###maven的两大核心

依赖管理：对jar包管理过程

项目构建：项目在编码完成后，对项目进行编译、测试、打包、部署等一系列操作都通过命令来实现。



通过maven命令将web项目发布到tomcat

```
mvn tomcat:run
```



### maven仓库类型

![mvn_warehouse](E:\sourceCode\document-file\maven\pic\mvn_warehouse.png)



### 配置本地仓库

__$MAVEN_HOME/conf/settings.xml__

```xml
<!--
localRepository
The path to the local repository maven will use to store artifacts.
Default: ${user.home}/.m2/repository
-->
<localRepository>/path/to/local/repo</localRepository>
```



### 常用命名

clean 清理



compile 编译



 test 单元测试

单元测试类名有要求：XxxTest.java



package 打包

web project  --war包

java projeck --jar包

将项目打包，打包项目根目录下targat







###JDK编译版本插件

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-compiler-plugin</artifactId>
  <version>3.5.1</version>
  <configuration>
    <source>1.7</source>
    <target>1.7</target>
  </configuration>
</plugin>
```



###依赖范围

添加依赖范围：默认是compile





![依赖范围](E:\sourceCode\document-file\maven\pic\依赖范围.png)



注：如果使用到tomcat自带的jar包，将项目中依赖作用范围设置为provided，如下

```xml
<dependency>
  <groupId>javax.servlet</groupId>
  <artifactId>jsp-api</artifactId>
  <version>2.0</version>
  <scope>provided</scope>
</dependency>
```







