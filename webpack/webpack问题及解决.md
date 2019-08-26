## 问题及解决方法

1.Critical dependency: require function is used in a way in which dependencies cannot be statically extracted<br>
解决方法：
```
module: {
  unknownContextCritical : false
}
```