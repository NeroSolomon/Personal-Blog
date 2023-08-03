## 一些包的功能及应用

### browser-sync
保存代码立即刷新浏览器的工具

### connect-history-api-fallback
用于记录单页应用的H5 history api

### redux-logger
用于在控制台中显示store的变化

### redux-thunk、redux-promise
dispatch只能处理同步的action，采用这些中间件可以使用在action中实现异步操作[参考地址](https://segmentfault.com/a/1190000007248878)

### qs
qs和JSON使用方法一样，也是有parse和stringify方法
```javascript
var a = {name:'hehe',age:10};
 qs.stringify(a)
// 'name=hehe&age=10'
JSON.stringify(a)
// '{"name":"hehe","age":10}'
```

## @babel/cli
Babel附带了一个内置的CLI，可以用来从命令行编译文件。

## @babel/core
babel-core 的作用是把 js 代码分析成 ast ，再转换为低版本的js

## @babel/plugin-proposal-class-properties
转换class

## @babel/plugin-proposal-object-rest-spread
编辑rest对象到es5

## @babel/preset-env
各环境的babel预设

## @babel/preset-react
Babel preset for all React plugins.

## patch-package
可以改动node_modules中的依赖包，并不随node_modules的删除失效：https://blog.csdn.net/weixin_42232325/article/details/122452729

1. 安装：npm i patch-package
2. 修改package.json下的script：“postinstall”: “patch-package”
3. 修改依赖包源码
4. 执行命令：npx patch-package 【依赖包名】，会发现生成一个文件
5. 这时删除依赖包重新下载，发现改动依旧存在

## tailwindcss
识别html的类名，生成对应css