## 封装一个自己的npm包

### webpack相关
假如你的库被项目引入时，需要获得这项目的依赖，例如react，那么就要告诉你的库，通过什么去引用react。可以通过webpack在构建的时候告诉库。

```js
export default {
  // webpack配置
  externals: {
    react: {
      commonjs: 'react',
      commonjs2: 'react',
      amd: 'react',
      root: 'React',
    },
    'react-dom': {
      commonjs: 'react-dom',
      commonjs2: 'react-dom',
      amd: 'react-dom',
      root: 'ReactDOM',
    },
  }
}
```

### package.json相关

- files: 说明哪些文件需要上传到npm
- main: 引入的入口文件
- peerDependencies: 注明引用这个库，项目需要安装的依赖

### 上传到npm库
1. 登录你的库
2. beta版本：`$ npm version prerelease`  `$ npm publish --tag beta`
3. 修改版本版本号
3. 正式版本：`$ npm publish`


### 基于antd封装npm库
https://zhuanlan.zhihu.com/p/80754775