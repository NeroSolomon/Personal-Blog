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

1. 配置webpack
```js
var path = require('path')
var webpack = require('webpack')
module.exports = {
  mode: 'development',
  entry:'./src/index.js', //入口文件路径
  output: {
    filename: 'main.js',
    // 打包组件名， module.exports = nero-compnents
    library: 'nero-components', 
    libraryTarget: 'umd',
    libraryExport: 'default'
  },
  externals: {
    // 提供使用外部依赖库的方式
    antd: {
      commonjs: 'antd',
      commonjs2: 'antd',
      amd: 'antd',
      root: 'antd',
    },
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
    }
  },
  module: {
    rules: [
      {
        test: /\.js[x]?$/,  // 用正则来匹配文件路径，这段意思是匹配 js 或者 jsx
        exclude: /node_modules/,
        include: [path.resolve(__dirname, 'src')],
        loader: 'babel-loader' // 加载模块 "babel-loader" 
      },
       {
         test: /\.s[ac]ss$/,
         use: [
           'style-loader',
           { loader: 'css-loader', options: { importLoaders: 1 } },
           'less-loader',
           { loader: 'sass-loader', options: { javascriptEnabled: true } }
         ]
       },
      {
        test: /\.css$/,
        use: [{
          loader: "style-loader"
        }, 
        {
          loader: 'css-loader',
          options:{ 
            modules: {
              mode: 'local',
              localIdentName: '[name]-[local]',
            },
          }
        }
        ]
      },
      
    ]
  },
  plugins: [
      new webpack.HotModuleReplacementPlugin(),
     ],
  }
```

2. 配置package.json
```json
{
  "name": "nero-components",
  "version": "1.0.0",
  "description": "我的组件库",
  "main": "dist/main.js",
  "files": [
    "dist"
  ],
  "scripts": {
    "test": "echo \"Error: no test specified\" && exit 1",
    "build": "webpack"
  },
  "devDependencies": {
    "@babel/cli": "^7.2.3",
    "@babel/core": "^7.5.5",
    "@babel/plugin-proposal-class-properties": "^7.2.3",
    "@babel/plugin-proposal-object-rest-spread": "^7.2.0",
    "@babel/preset-env": "^7.5.5",
    "@babel/preset-react": "^7.0.0",
    // 本地开发使用的
    "antd": "4.11.2",
    "babel-core": "^6.26.3",
    "babel-loader": "^8.0.6",
    "babel-plugin-import": "^1.12.0",
    "babel-preset-es2015": "^6.24.1",
    "babel-preset-stage-1": "^6.24.1",
    "classnames": "^2.2.6",
    "css-loader": "^3.2.0",
    "eslint": "^6.2.1",
    "eslint-plugin-import": "^2.18.2",
    "eslint-plugin-jsx-a11y": "^6.2.3",
    "eslint-plugin-react": "^7.14.3",
    "eslint-watch": "^6.0.0",
    "node-sass": "^6.0.1",
    "prettier": "^2.3.2",
    "prop-types": "^15.7.2",
    // 本地开发测试使用的
    "react": "^17.0.1",
    "react-dom": "^17.0.1",
    "sass": "^1.35.1",
    "sass-loader": "^12.1.0",
    "style-loader": "^1.0.0",
    "webpack": "^4.39.3",
    "webpack-cli": "^3.3.7",
    "webpack-dev-server": "^3.8.0"
  },
  "dependencies": {},
  "peerDependencies": {
    // 交由外部库安装的
    "antd": ">=4.11.2",
    "react": ">=17.0.1",
    "react-dom": ">=17.0.1"
  },
  "repository": {
    "type": "git",
    "url": "git+https://github.com/NeroSolomon/nero-components.git"
  },
  "author": "Nero",
  "license": "ISC",
  "bugs": {
    "url": "https://github.com/NeroSolomon/nero-components/issues"
  },
  "homepage": "https://github.com/NeroSolomon/nero-components#readme"
}

```

3. 配置babel
```json
{
  "presets": ["@babel/preset-env", "@babel/preset-react"],
  "plugins": [
    // 解析class
    "@babel/plugin-proposal-class-properties",
    // 解析 reset -> ...
    "@babel/plugin-proposal-object-rest-spread",
    // 按需引入antd组件样式
      ["import", {"libraryName": "antd", "libraryDirectory": "lib", "style":"css"
      }],
  ]
}
```

4. 安装依赖

5. 暴露插件
```js
// /src/index.js
import React, { useEffect } from 'react'
import { Button } from 'antd'

const TooltipButton = props => {
  const { children } = props
  return (
      <Button>{children}</Button>
  )
}

TooltipButton.propTypes = {
}

TooltipButton.defaultProps = {
}

export default TooltipButton

```

6. 使用 yalc 进行本地调试
> yalc 是一个可以在本地模拟 npm package 发布环境的工具。yalc 主要本地化了一个 npm 的存储库，通过 yalc publish 可以把构建的产物发布到本地。通过 yalc add <pkg> 可以达到 npm install <pkg> 或 yarn add <pkg> 的效果。

step 1: 全局安装yalc
```
yarn global add yalc
```

step 2: 在组件目录下
```
# build package
yarn build

# 发布
yalc publish
```

step 3: 在使用项目下
```
# 引用package
yalc add my-component-package

# 更新引用package的依赖
yarn
```

7. 打包

8. 发布

Q&A：

1. 为什么我在本地用npm link测试的时候老是提示react重复

  因为在npm link的时，未经发布（package.json files属性处理），所以组件库中含有node_modules目录，如果不删除直接测试，则优先引用的是组件库node_modules中的react。

  而外部库的node_modules中也含有react，则导致两份react同时存在，导致报错

  所以，我们本地link测试时，记得先把组件库的node_modules删除，模仿npm环境