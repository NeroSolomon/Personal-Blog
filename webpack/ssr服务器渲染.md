## ssr，客户端在请求html的时候，将结合css、js渲染好的html一并返回

### 优点
1. 有利于单页应用的seo（单页应用的内容是js运行完创建的）
2. 加速首屏渲染

### 缺点
1. 服务器压力大
2. 生命钩子只会执行componentDidMount之前的钩子

### 可能会遇到的问题

#### document is not defined
原因：在服务端渲染客户端代码的时候，document对象是没有的

解决：https://juejin.cn/post/7352342892785352755
    - use client
    - dynamic