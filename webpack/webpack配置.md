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
在服务器上，默认读取目录是项目的根目录，但资源文件是打包在dist下，包括index.html，而index.html和js、css文件在同目录下，所以在index.html中会以“/js”，"/css"的方式引用资源，但根目录是dist所在目录，所以服务器会找不到资源，publicPath实际上就是告诉服务器资源在哪里<br>

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
-  contentBase: './dist',
+  historyApiFallback: {
+    index: '/dist/index.html'
+  }
```

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

## css modules：能够对css进行模块化打包，防止css样式污染全局
其实就是在css-loader后加参数，例如use: 'css-loader?modules'