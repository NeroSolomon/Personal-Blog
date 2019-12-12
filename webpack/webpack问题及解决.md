## 问题及解决方法

1.Critical dependency: require function is used in a way in which dependencies cannot be statically extracted<br>
解决方法：
```
module: {
  unknownContextCritical : false
}
```

## 当模版html中有 ${xxx} 的字符串时，会报xxx is no defined，但你只是想输出字符串，解决方法<%= '${xxx}' %>