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
默认对es6语法开启，所以要注意typescript的target是啥