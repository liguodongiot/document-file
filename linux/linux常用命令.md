

### 用户及权限

```shell
# 添加一个名为 tommy 的用户
adduser tommy  
# 修改密码
passwd tommy  
```







### 进程和端口

```shell
# 根据端口号查看进程号
netstat -apn | grep 8888


# 根据进程ID查询进程
ps -aux | grep pid

# 查看端口
lsof -i:9597

# 查看进程
ps -ef | grep elasticsearch
```



### 文件磁盘

```shell
# 查看各文件夹大小
du -lh --max-depth=1
```



### su

```shell
su 是切换到其他用户，但是不切换环境变量
su - 是完整的切换到一个用户环境

1.su的作用是变更为其它使用者的身份，需要键入该使用者的密码（超级用户除外）。

2.格式
su [选项]... [-] [USER [ARG]...]

3.主要参数
-f， --fast：不必读启动文件（如 csh.cshrc 等），仅用于csh或tcsh两种Shell。
-l， --login：加了这个参数之后，就似乎是重新登陆为该使用者一样，大部分环境变量（例如HOME、SHELL和USER等）都是以该使用者（USER）为主，并且工作目录也会改变。假如没有指定USER，缺省情况是root。
-m， -p ，--preserve-environment：执行su时不改变环境变数。
-c command：变更账号为USER的使用者，并执行指令（command）后再变回原来使用者。
USER：欲变更的使用者账号，ARG传入新的Shell参数。

```







### FTP

```shell
# 参考：https://blog.csdn.net/u013850277/article/details/56486370

# host是FTP服务器的域名或IP地址
ftp host

dir 命令：显示目录和文件列表。

ls 命令：显示简易的文件列表。

# cd 命令：进入指定的目录
cd /home/lgd/backup

# lcd: 进入linux目录
lcd /home/lgd/backup

# 创建ftp目录
mkdir  /online/20180531/aaa

#上传
mput xma_v1.1.1_201805311116.zip



```



