import webpack from 'webpack';
import browserSync from 'browser-sync';
import webpackDevMiddleware from 'webpack-dev-middleware';
import webpackHotMiddleware from 'webpack-hot-middleware';
import getConfig from '../webpack.config';
import proxyConfig from '../src/proxy.js';

const config = getConfig();
const bundler = webpack(config);

// browserSync 实时更新浏览器的工具
browserSync({
  port: 3000,
  ui: {
    port: 3001,
  },
  server: {
    baseDir: 'src',
    // 中间件
    // 模块热更新
    middleware: [
      ...proxyConfig,
      webpackDevMiddleware(bundler, {
        publicPath: config.output.publicPath,
        stats: {
          colors: true,
        },
      }),
      // 和middleware配合使用
      webpackHotMiddleware(bundler),
    ],
  },
  // 监听文件变化
  files: ['src/*', 'src/**/*', 'src/**/**/*'],
  listen: '127.0.0.1',
});
