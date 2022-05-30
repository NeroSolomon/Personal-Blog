## <<
占位符
```
foo: &myanchor // 用&声明一个变量
  key1: "val1"
  key2: "val2"

bar:
  << : *myanchor // 使用这个变量
  key2: "val2-new"
  key3: "val3

// 最终我们得到
bar:
  key1: "val1"
  key2: "val2-new"
  key3: "val3
```

## volumes

### type
具有Volumes、bind、tmpfs三种选项，有以下区别：

- Volumes由Docker管理，存储在宿主机的某个地方（在linux上是/var/lib/docker/volumes/）。非Docker应用程序不能改动这一位置的数据。Volumes是Docker最好的数据持久化方法。
- Bind mounts的数据可以存放在宿主机的任何地方。数据甚至可以是重要的系统文件或目录。非Docker应用程序可以改变这些数据。
- tmpfs mounts的数据只存储在宿主机的内存中，不会写入到宿主机的文件系统。

## reference
1. https://yeasy.gitbook.io/docker_practice/compose/compose_file