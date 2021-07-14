## 问题及解决方法

1.Critical dependency: require function is used in a way in which dependencies cannot be statically extracted<br>
解决方法：
```
module: {
  unknownContextCritical : false
}
```

## 当模版html中有 ${xxx} 的字符串时，会报xxx is no defined，但你只是想输出字符串，解决方法<%= '${xxx}' %>

## 使用${require('xxx')}，在html中引用其它资源

## 给不同的html页面打包不同的资源文件
```javascript
export default {
  entry: {
    chunks1: './js/chunks1.js',
    chunks2: './js/chunks2.js'
  },
  plugins: [
    new HtmlWebpackPlugin({
      template: './src/chunks1.html',
      favicon: './src/favicon.ico',
      filename: 'chunks1.html',
      chunks: ['chunks1']
    }),
    new HtmlWebpackPlugin({
      template: './src/chunks2.html',
      favicon: './src/favicon.ico',
      filename: 'chunks2.html',
      chunks: ['chunks2']
    })
  ]
}
```

## tree shaking
默认对es6语法开启，但需要在package.json中通过以下设置，排除副作用
```json
{
  "sideEffects": false, //所有文件没有副作用，可以排除未引用但
  "sideEffects": ["./src/index.js"] // index.js有副作用，不能排除
}
```

什么是副作用，就是import的函数中有对未import的变量的引用

## runtime
是指运行中的代码

形如import('abc').then(res=>{})这种异步加载的代码，在webpack中即为运行时代码。

在VueCli工程中常见的异步加载路由即为runtime代码