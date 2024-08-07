## webpack配置学习记录
1.path：资源的打包的静态目录，也可以说是静态目录<br>

2.publicPath：生产环境中cdn资源的目录, 或者说是服务器读取静态资源时在路径前添加的目录<br>

个人理解，例如：
```
output: {
  path: `${__dirname}/dist`,
  publicPath: '/dist/'
}
```
在服务器上，默认读取目录是项目的根目录，但资源文件是打包在dist下，包括index.html，而index.html和js、css文件在同目录下，所以在index.html中会以“/js”，"/css"的方式引用资源，但根目录是dist所在目录，所以服务器会找不到资源，publicPath实际上就是资源目录前缀： src="/dist/js"<br>



但如果你的资源不是放到dist的话：<br>
1.当我们按照以上配置构建项目时，所有资源文件就会打包到dist文件夹中<br>
2.dist文件夹中的资源就是我们网站线上所用的所有资源，其中index.html引用了各种静态资源<br>
3.在线上时，我们的资源时存放在cdn上的，所以我们的静态资源的地址需要指向cdn，在用户访问网站的时候，浏览器就会请求较近cdn上的资源，如果没有找到，就会回源到资源文件所在的cdn地址<br>
4.所以我们需要通过publicPath指定生产环境中index.html引用的静态资源的目录<br>
![配置化publicPath](./webpack/images/F2DAD5D9-FD84-4cc7-9444-32893966B1FC)<br>
![webpack读取配置构建](./webpack/images/B113CFCB-9BA3-4d6f-A62B-60FF1C2A8A57.png)<br>
![线上路径](./webpack/images/B2C263D0-F1E4-4bc2-A589-183F8C32D9A7.png)<br>

## webpack-dev-server
误区：一开始我以为webpack-dev-server命令也会生成dist目录，但其实不会，服务器是会读取内存里的文件吗，这点和webpack命令不同<br>
还有一个坑：假如配置了contentBase: './dist'，而没有dist目录的话，会没有展示，假如没有dist目录，则需要以下配置
```javascript
  {
    devServer: {
-     contentBase: './dist',
+     historyApiFallback: {
+       index: '/dist/index.html'
+     }
    }
  }

```

### devServer.proxy
配置详解：
```js
{
  devServer: {
    proxy: {
      '/api': {
        target: 'http://localhost:3000', // 现在，对 /api/users 的请求会将请求代理到 http://localhost:3000/api/users
        pathRewrite: { '^/api': '' }, // 如果不希望传递/api，则需要重写路径：
        secure: false, // 默认情况下，将不接受在 HTTPS 上运行且证书无效的后端服务器。 如果需要，可以这样修改配置
        bypass: function (req, res, proxyOptions) { // 有时不想代理所有内容。 可以基于函数的返回值绕过代理。
          if (req.headers.accept.indexOf('html') !== -1) {
            console.log('Skipping proxy for browser request.');
            return '/index.html';
          }
        },
        changeOrigin: true, // 默认情况下，代理时会保留主机头的来源，可以将 changeOrigin 设置为 true 以覆盖此行为。
      }
    },
    // 如果想将多个特定路径代理到同一目标，则可以使用一个或多个带有 context 属性的对象的数组：
    proxy: [
      {
        context: ['/auth', '/api'],
        target: 'http://localhost:3000',
      },
    ],
  }
}
```
1. 

## 如何实现webpack的监听及热更新
1.首先需要browser-sync来将更新反应到浏览器上<br>
2.然后需要webpack-dev-middleware来监听文件变化并重新加载，但其实还需要webpack-hot-middleware来配合使用，因为webpack-dev-middleware其实没办法处理这么多工作量<br>
3.传入config，调用webpack方法，返回一个complier<br>
```javascript
const webpack = require('webpack');
const browserSync = require("browser-sync").create();
const webpackDevMiddleware = require('webpack-dev-middleware');
const  webpackHotMiddleware = require('webpack-hot-middleware');
const getConfig = require('./../webpack.config.fn.js');

const config = getConfig();
const compiler = webpack(config);
// 此处的compiler是webpack的核心，其有很多api，例如compiler.run()就会安装config构建生产目录

browserSync.init({
  port: 3000,
  ui: {
    port: 3001
  },
  server: {
    // 服务启动的根目录
    baseDir: 'src',
    middleware: [
      // watch file Change, just for development
      webpackDevMiddleware(compiler, {
        // required
        publicPath: config.output.publicPath,

        // pretty colored output
        stats: { colors: true },

        // only show error when compile
        logLevel: 'error'
      }),

      // use it with webpack-dev-middleware
      webpackHotMiddleware(compiler)
    ]
  },
  // no need to watch '*.js' here, webpack will take care of it for us,
  // including full page reloads if HMR won't work
  files: ['src/*.html']
})
```
4.由于服务器启动的目录是根目录，所以js文件也需要打包到src目录下，但是webpack-dev-middleware会将文件放到内存中，所以我们是无法看到的<br>
```javascript
output: {
  path: path.resolve(__dirname, 'dist'),
  filename: isDev ? 'bundle.js' : 'js/app.bundle.[hash].js',
  publicPath: isDev ? '/' : '/dist/'
},
```
5.由于根目录是src目录，所以记得index.html里要手动引入bundle.js，正式构建的时侯写个插件把手动引入的script删除<br>
这里踩了一个很大的坑，我在webpack配置里使用了splitChunks，导致打包出来的js文件有多个，但是我只引入了bundle.js，所以加入的代码就死活不能执行<br>

## webpack自定义插件
1.创建插件js文件，这里使用html-webpack-plugin来作为例子，因为上面提到写插件来删除script标签，其他生命钩子请按需查询
```javascript
  function updateIndexHTML() {
    // ...
  }

  updateIndexHTML.prototype.apply = function(compiler) {
    compiler.plugin('compilation', function(compilation) {
      // Hook into html-webpack-plugin event
      // webpack 3 or 4
      compilation.plugin('html-webpack-plugin-before-html-processing', function(pluginData) {
        pluginData.html = pluginData.html + 'Hello world'
      });

      // webpack 4
      // compilation.hooks.htmlWebpackPluginBeforeHtmlProcessing.tapAsync(pluginName, (htmlPluginData, callback) => {
      //   htmlPluginData.html += 'Hello world';
      //   callback(null, htmlPluginData);
      // });
    });
  };

  module.exports = updateIndexHTML;
```

2.在webpack配置文件中引入，并直接通过new来使用
```javascript

const updateIndexHTML = require('./tools/update-index-html.js');

return {
  plugins: [new updateIndexHTML()]
}
```

## css modules：（:global()）能够对css进行模块化打包，防止css样式污染全局
其实就是在css-loader后加参数，例如use: 'css-loader?modules'

## extract-text-webpack-plugin，可以用来抽离打包到js中的css，并通过配置获得对应的css
例如，自定义ui框架主题

1. less modifyVars：antd主题设置，全局自定义修改antd主题
例如:
```javascript
const theme = {
  'primary-color': '#1DA57A',
  'link-color': '#1DA57A'
}

// 在lessloader的处理中加入use: {options: { modifyVars: theme}}
```
项目实战配置：
```js
  // 定义
  let antdLoader = extractAntPlugin.extract({
    fallback: 'style-loader',
    use: [
      'css-loader?sourceMap',
      `less-loader?{'sourceMap':true,'modifyVars':${antdTheme}}`
    ]
  });

  // 在loader中使用
  module: {
    loaders: [
      {
        test: /\.less$/,
        include: [path.join(__dirname, 'node_modules/antd')],
        use: antdLoader
      }
    ]
  }
```

2. css module， 防止css样式污染全局
```js
  let cssLoader = extractCssPlugin.extract({
    fallback: 'style-loader',
    use: [
      // 规定module命名: modules&localIdentName=[name]__[local]--[hash:base64:5]
      'css-loader?sourceMap&modules&localIdentName=[name]__[local]--[hash:base64:5]',
      'sass-loader?sourceMap'
    ]
  });

  // 在loader中使用
  module: {
    loaders: [
      {
        test: /\.s?css$/,
        include: [path.join(__dirname, 'src')],
        use: cssLoader
      }
    ]
  }
```

## html-loader，可以在html中使用${require('html-loader!./xxx.html')} 引入html，webpack就会对这个html进行编译

## output.chunkFilename
```javascript
module.exports = {
    entry: {
        "index": "pages/index.jsx"
    },
    output: {
        filename: "[name].min.js",
        chunkFilename: "[name].min.js"
    }
}

```
1.在entry中引入的js会按照output.filename命名，但如果是异步引入的就会使用output.chunkFilename来命名
```javascript
require.ensure(["modules/tips.jsx"], function(require) {
    var a = require("modules/tips.jsx");
    // ...
}, 'tips');
```
2.如果传入第三个参数，就会按照第三个参数来命名：tips.min.js，否则就是一个自动分配的name

## html-webpack-plugin
templateParameters: 通过变量覆盖模版html的值
```js
new HtmlWebpackPlugin({
  favicon: './src/favicon.ico',
  filename: `${__dirname}/dist/index.html`,
  template: `${__dirname}/src/index.html`,
  inject: true,
  templateParameters: {
    version: version
  }
})
```

```html
<html lang="en" data-version="<%= version %>"></html>
```

## 动态引入组件
```js
import(/* webpackChunkName: "home" */ '../components/HomeView.js')
```
注释为webpack打包出的chunkname

## optimization
webpack配置一级属性<br>
子属性解析：<br>
1. splitChunks 根据不同策略来分割打包出来的bundle，包括minSize，maxSize

## performance
文件的一些打包体积限制，例如：
1. maxAssetSize
2. maxEntrypointSize

## modules

### rules
1. eslint-loader，当存在eslint问题时，出现构建提示

### resolve
alias配置路径，例如：
```js
{
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'code/react-ui'),
    },
  },
}
```

elsint配合：
```js
settings: {
  'import/resolver': {
    alias: {
      map: [
        ['@', './code/react-ui/'],
      ],
    }
  }
}

// 安装
//  "eslint-import-resolver-alias": "^1.1.2",
//  "eslint-plugin-import": "^2.22.1",
```

jsconfig.json / tsconfig.json
```json
{
   "compilerOptions": {
      "paths": {
        "@": ["./code/react-ui/"]
      }
    },
}
```

#### resolve.modules
告诉 webpack 解析模块时应该搜索的目录
如果你想要添加一个目录到模块搜索目录，此目录优先于 node_modules/ 搜索

### output

#### library
将bundle打包成一个对象

1. 假如index.js 长这样
```js
module.exports = 'hello world'
```

2. webpack默认打包出是这样的
```js
(function () {
  return 'hello world'
})()
```
假如我们想拿到hello world，则没有办法。

3. 如果想拿到hello word，则需要配置一个对象接收它
```js
{
  output: {
    name: 'result'
  }
}
```

4. 打包出来的时候长这样
```js
var result = (function () {
  return 'hello world'
})()
```

5. 我们可以这样使用
```html
<body>
  <script src="./dist/bundle.js"></script>
  <script>
    console.log(result)
  </script>
</body>
```

6. 假如我们想在commom.js中使用
```js
{
  output: {
    name: 'result',
    type: 'commonjs2'
  }
}
```

7. 打包后结果是
```js
module.exports = (function () {
  return 'hello world'
})()
```

8. 使用方法
```js
import result from './bundle'

console.log(result)
```

##### name
对象名

##### type
将对象打包到哪个环境 global/window/umd，umd：所有环境都能使用

#### Module Federation
参考链接：https://zhuanlan.zhihu.com/p/144267429

### externals

externals 配置选项提供了「从输出的 bundle 中排除依赖」的方法。

相反，所创建的 bundle 依赖于那些存在于用户环境(consumer's environment)中的依赖

### dll
打包缓存

参考链接：https://www.cnblogs.com/skychx/p/webpack-dllplugin.html

### 使用ESM

将模块标记为 ESM 

默认情况下，webpack 将自动检测文件是 ESM 还是其他模块系统。

Node.js 通过设置 package.json 中的属性来显式设置文件模块类型。 

在 package.json 中设置 "type": "module" 会强制 package.json 下的所有文件使用 ECMAScript 模块。 

设置 "type": "commonjs" 将会强制使用 CommonJS 模块。

### 预置依赖
可以通过预置依赖的方式，直接在项目中引入第三方库，无需在代码文件中import
```js
{
  plugins: [
    new webpack.ProvidePlugin({
      _: 'lodash',
    }),
  ]
}
```

可以更细力度预置，预置一个文件
```js
{
  module: {
    rules: [
      {
        test: require.resolve('./src/index.js'),
        use: 'imports-loader?wrapper=window',
      },
    ],
  }
}
```

### devserver

#### overlay
报错时覆盖页面

### open
服务启动时打开浏览器

