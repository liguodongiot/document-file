kerberos使用手册：http://wzktravel.github.io/2016/03/01/how-to-use-kerberos-in-CDH/

https://www.cloudera.com/documentation/enterprise/latest/topics/cm_sg_regen_kerb_princs.html

http://web.mit.edu/kerberos/krb5-latest/doc/admin/database.html

http://web.mit.edu/kerberos/krb5-latest/doc/user/index.html



### kerberos是什么？

> Kerberos提供了一种单点登录(SSO)的方法。考虑这样一个场景，在一个网络中有不同的服务器，比如，打印服务器、邮件服务器和文件服务器。这些服务器都有认证的需求。很自然的，不可能让每个服务器自己实现一套认证系统，而是提供一个中心认证服务器（AS-Authentication Server）供这些服务器使用。这样任何客户端就只需维护一个密码就能登录所有服务器。
>
> 因此，在Kerberos系统中至少有三个角色：认证服务器（AS），客户端（Client）和普通服务器（Server）。客户端和服务器将在AS的帮助下完成相互认证。在Kerberos系统中，客户端和服务器都有一个唯一的名字，叫做Principal。同时，客户端和服务器都有自己的密码，并且它们的密码只有自己和认证服务器AS知道。





```
在Kerberos中，Principal是参加认证的基本实体。
一般来说有两种，一种用来表示Kerberos数据库中的用户， 另一种用来代表某一特定主机，也就是说Principal是用来表示客户端和服务端身份的实体。
```



### 名词解释

**KDC**：即Key Distribution Center, 密钥分发中心，负责颁发凭证
**Kinit**：Kerberos认证命令，可以使用密码或者Keytab。
**Realm**：Kerberos的一个管理域，同一个域中的所有实体共享同一个数据库
**Principal**：Kerberos主体，即我们通常所说的Kerberos账号(name@realm) ，可以为某个服务或者某个用户所使用 
**Keytab**：以文件的形式呈现，存储了一个或多个Principal的长期的key，用途和密码类似，用于kerberos认证登录；其存在的意义在于让用户不需要明文的存储密码，和程序交互时不需要人为交互来输入密码。





### 普通用户

http://web.mit.edu/kerberos/krb5-latest/doc/user/index.html

每人尽量使用自己的账号登陆集群中机器，否则可能在切换kerberos账户时覆盖其他人账号或被覆盖。

```shell
# 使用密码切换用户 此命令需要输入你在kerberos中的密码
kinit username

kinit成功之后你获取的票据就会缓存到本地，可以用klist查看.

# 修改密码

kpasswd
kpasswd finance

# 更新credentials
kinit -R

kinit: Ticket expired while renewing credentials


# 销毁credentials 注销
kdestroy

销毁之后不能查看到票据
klist
klist: No credentials cache found (ticket cache FILE:/tmp/krb5cc_0)


# 使用keytab 免密码切换用户，但是需要提前生成好keytab，并保证只有自己有读写权限。
kinit -k -t user.keytab username

kinit -kt /home/xxx/xxx.keytab xxx 等同于 kinit xxx


# 查看此keytab中所有principal
klist -k user.keytab

klist -k /etc/krb5.keytab
```









### kerberos管理页面

进入kerberos管理页面，有两种方式：

在Krb5 server所在机器并且当前用户是root的话，可以使用`kadmin.local`免密码进入；

当前用户是非root用户或在其他机器上时，可以使用`kadmin $admin_user`进入，需要输入此admin用户的密码。

如果需要直接执行命令，使用`-q`参数。



```shell
# 列出所有principal: list_principals, listprincs
kadmin.local -q "listprincs" 

# 登录
kadmin $admin_user

listprincs

# 表达式筛选
listprincs *-app5-*

# 查看principal信息: get_principal, getprinc
getprinc [-terse] principal
-terse输出更加简洁，各信息之间以tab作为分隔符。
---
getprinc aaa@AAABBB.HOST
getprinc -terse aaa@AAABBB.HOST


# 添加principal: add_principal, addprinc
addprinc [options] principal

-randkey
随机生成一个值作为principal的key
-pw
设置密码，此选项一般用在脚本中。
---

addprinc -pw password aaa@AAABBB.HOST

addprinc -randkey aaa@AAABBB.HOST

可以对principal设置过期时间等

# 修改principal: modify_principal, modprinc
与addprinc命令选项基本一致, modprinc [options] principal

modprinc -maxlife 1days -maxrenewlife 365days +allow_renewable krbtgt/dada@AAABBB.HOST

# 删除principal: delete_principal, delprinc
delprinc [-force] principal
-force 不会提示是否删除

---

delprinc -force aaa@AAABBB.HOST

# 修改密码: change_password, cpw
change_password [options] principal

-randkey
随机生成一个值作为principal的key
-pw
设置密码，此选项一般用在脚本中。
--- 

cpw aaa@AAABBB.HOST
cpw -pw liguodong aaa@AAABBB.HOST


# 生成keytab
生成单一的keytab: 
xst -k ${USERNAME}.keytab ${USERNAME}@DOMAIN.COM

合并多个keytab
$ ktutil
ktutil: rkt a.keytab
ktutil: rkt b.keytab
ktutil: wkt merge.keytab
ktutil: exit

```

