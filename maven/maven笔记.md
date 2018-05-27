

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



install : 安装到本地仓库



### 生命周期

maven有三套生命周期，每一套生命周期相互独立,互不影响。在同一套生命周期，执行后面的命令，前面操作会自动执行。

CleanLifeCycle: 清理生命周期

clean

DefaultLifeCycle: 默认生命周期

compile --> test  --> package  --> install --> deploy

SiteLifeCycle: 站点生命周期

site







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



### 概念模型

两个核心

依赖管理：对jar包管理

项目构建：通过命令进行项目构建



![概念模型](E:\sourceCode\document-file\maven\pic\概念模型.png)



### 传递依赖冲突解决

maven自己调解原则

**第一声明者优先原则**

谁先定义的就用谁传递依赖。



**路径近者优先原则**

直接依赖高于传递依赖



### 排除依赖&版本锁定

**排除依赖**

```xml
<dependency>
  <groupId>junit</groupId>
  <artifactId>junit</artifactId>
  <version>4.12</version>
  <exclusions>
    <exclusion>
      <groupId>org.hamcrest</groupId>
      <artifactId>hamcrest-core</artifactId>
    </exclusion>
  </exclusions>
</dependency>
```



**版本锁定**

```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>org.apache.kafka</groupId>
      <artifactId>kafka_2.11</artifactId>
      <version>0.8.2.2</version>
    </dependency>
  </dependencies>
</dependencyManagement>

<dependencies>
    <dependency>
      <groupId>org.apache.kafka</groupId>
      <artifactId>kafka_2.11</artifactId>
      <version>0.8.2.1</version>
    </dependency>
</dependencies>
```



### 传递依赖的范围

![传递依赖](E:\sourceCode\document-file\maven\pic\直接依赖传递依赖.png)



依赖并不是无休止的传递，当项目中需要的依赖没有传递过去，在自己的工程中添加相应的依赖就可以。





### 私服安装

Nexus

### 私服的仓库类型

hosted: 宿主仓库

存放本公司开发的jar包（正式版本、测试版本、第三方<存在版权问题>）

proxy:代理仓库

代理中央仓库、Apache下测试版本的jar包

virtual:虚拟仓库 --已被废弃 

group:组仓库

将来连接组仓库。包含hosted和proxy.



### 上传jar包到私服

maven中conf/settings.xml

```xml
<!--宿主仓库-->
<servers>
  <server>
    <id>release</id>
    <username>repouser</username>
    <password>repopwd</password>
  </server>
  <server>
    <id>snapshots</id>
    <username>repouser</username>
    <password>repopwd</password>
  </server>
</servers>
```



项目中pom.xml配置上传路径

```xml
<distributionManagement>
  <repository>
    <uniqueVersion>false</uniqueVersion>
    <id>corp1</id>
    <name>Corporate Repository</name>
    <url>scp://repo/maven2</url>
    <layout>default</layout>
  </repository>
  <snapshotRepository>
    <uniqueVersion>true</uniqueVersion>
    <id>propSnap</id>
    <name>Propellors Snapshots</name>
    <url>sftp://propellers.net/maven</url>
    <layout>legacy</layout>
  </snapshotRepository>
</distributionManagement>
```



上传

deploy



### 下载jar包到本地

1、配置模板

```xml
<profiles>
    <profile>
    
      <id>jdk-1.4</id> <!--配置ID-->

      <activation>
        <jdk>1.4</jdk>
      </activation>

      <repositories>
        <repository>
          <id>jdk14</id> <!--仓库ID，可以配置多个仓库，保证ID不重复-->
          <name>Repository for JDK 1.4 builds</name>
          <!--仓库地址，集nexus仓库组的地址-->
          <url>http://www.myhost.com/maven/jdk14</url>
          <!--是否下载releases构件-->
           <releases>
            <enabled>true</enabled>
          </releases>
          <!--是否下载snapshots构件-->
          <snapshots>
            <enabled>true</enabled>
          </snapshots>
        </repository>
      </repositories>
      <pluginRepositories>  
        <!--插件仓库，maven的运行依赖插件，也需要从私服下载插件-->
        <pluginRepository>  
          <id>maven-net-cn</id>  <!--插件仓库ID不重复，如果重复后面会覆盖前面-->
          <name>Maven China Mirror</name>  
          <url>http://maven.net.cn/content/groups/public/</url>  
          <releases>  
            <enabled>true</enabled>  
          </releases>  
          <snapshots>  
            <enabled>false</enabled>  
          </snapshots>      
        </pluginRepository>  
      </pluginRepositories>  
	</profile>
</profiles>
```



2、激活模板

```
<activeProfiles>
	<activeProfile>alwaysActiveProfile</activeProfile>
</activeProfiles>
```



### 使用maven的好处

不再拷贝jar包

环境统一，导入别的maven项目不会报错

代码的耦合度降低

方便项目进行升级

节省人力成本