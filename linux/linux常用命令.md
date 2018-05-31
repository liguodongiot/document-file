

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



