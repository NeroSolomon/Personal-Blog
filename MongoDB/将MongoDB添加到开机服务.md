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

4.启动mongodb服务
```
net start MongoDB
```

5.查看window服务，确认mongodb服务已启动