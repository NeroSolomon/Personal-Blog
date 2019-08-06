## 如何将MongoDB添加到开机服务
[参考文章](https://blog.csdn.net/qq_20332193/article/details/79628765)

1.创建一个data文件夹，里面再创建db和log两个子目录
|-data
|----db
|----log

2.创建mongo.config配置文件，添加以下内容
```
dbpath=具体路径\data\db
logpath=具体路径\data\log\mongod.log
```

3.运行以下命令
```
mongod --config "具体路径\mongo.config" --dbpath "具体路径\data\db" --logpath "具体路径\data\log\mongod.log" --bind_ip 0.0.0.0 --install --serviceName "MongoDB"
```

命令含义
```
--bind_ip	绑定服务IP，若绑定127.0.0.1，则只能本机访问，若绑定0.0.0.0，则默认可以通过ip来访问
--logpath	指定MongoDB日志文件，注意是指定文件不是目录
--logappend	使用追加的方式写日志
--dbpath	指定数据库路径
--port	指定服务端口号，默认端口27017
--serviceName	指定服务名称
--serviceDisplayName	指定服务名称，有多个mongodb服务时执行
--install	指定作为一个Windows服务安装
```

4.启动mongodb服务
```
net start MongoDB
```

5.查看window服务，确认mongodb服务已启动