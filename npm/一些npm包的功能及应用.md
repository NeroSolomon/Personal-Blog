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

## @hello-pangea/dnd
拖拽包

## core-js
core-js 它是JavaScript标准库的 polyfill（垫片/补丁）, 新功能的es'api'转换为大部分现代浏览器都可以支持
运行的一个'api' 补丁包集合

### 为什么使用了babel-loader对js进行兼容性配置还需要core-js?
在Webpack 5中，使用babel-loader对JavaScript进行兼容性配置可以将新版本的JavaScript语法转换为低版本的语法，以便在旧版浏览器中正常运行。然而，babel-loader只会处理语法转换，而不会处理新增的API或全局对象。对于一些新的API（如Promise、Array.from等）或全局对象（如Symbol、Map等），我们仍然需要使用core-js来提供兼容性支持。


## react-codemirror2
一个开源的编辑器组件

### 如何高度自适应
1. 设置属性
```js
import {UnControlled as CodeMirror} from 'react-codemirror2';
import 'codemirror/lib/codemirror.css';

function MyComponent() {
  return (
    <CodeMirror
      options={{
        viewportMargin: Infinity,
        lineNumbers: true
      }}
    />
  );
}
```

2. 设置样式
```css
.CodeMirror {
   height: auto;
}
.CodeMirror-scroll {
   overflow-y: hidden; 
   overflow-x: auto;
}
```

### zustand
基于local storage的状态管理工具

### axios
axios get方法默认对数据参数，会转换为param[]=xx&param[]=xx这种形式，如果需要转换成param=xx&param=xx这种形式，可以使用

paramsSerializer: params => qs.stringify(params, { arrayFormat: 'repeat' })

### ejs
express的一个模版引擎

### tailwindcss
动态css包

1. 安装tailwindcss
npm install -D tailwindcss

2. 在根目录中配置：tailwind.config.js

```js
/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{html,js,jsx,ts,tsx}",
  ],
  theme: {
    extend: {},
  },
  plugins: [],
}
```

3. 在css文件中引入，注意：是css
```css
@tailwind base;
@tailwind components;
@tailwind utilities;
```

4. 在tsx、jsx中引入此css