import webpack from 'webpack';
import path from 'path';

const getConfig = (env) => {
  const isProd = env === 'production';

  // 插件
  const plugins = [
    new webpack.DefinePlugin({
      'process.env.NODE_ENV': JSON.stringify(
        isProd ? 'production' : 'development'
      ),
    }),
  ];

  // 热更新
  !isProd && plugins.push(new webpack.HotModuleReplacementPlugin());

  // 生产构建时不打包的包
  const externals = isProd
    ? {
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
        },
      }
    : {};

  return {
    mode: isProd ? 'production' : 'development',
    entry: isProd ? './src/index.js' : './src/dev.js',
    output: {
      publicPath: isProd ? '/dist' : '/',
      filename: isProd ? 'main.js' : 'bundle.js',
      library: 'atl-components',
      libraryTarget: 'umd',
      libraryExport: 'default',
    },
    externals,
    plugins,
    module: {
      rules: [
        {
          test: /\.js[x]?$/, // 用正则来匹配文件路径，这段意思是匹配 js 或者 jsx
          exclude: /node_modules/,
          include: [path.resolve(__dirname, 'src')],
          loader: 'babel-loader', // 加载模块 "babel-loader"
        },
        {
          test: /\.less$/,
          include: [path.join(__dirname, 'src')],
          use: [
            'style-loader',
            {
              loader: 'css-loader',
              options: {
                importLoaders: 1,
                modules: {
                  localIdentName: '[name]__[local]--[hash:base64:5]',
                },
              },
            },
            'less-loader',
            {
              loader: 'less-loader',
              options: {
                javascriptEnabled: true,
              },
            },
          ],
        },
        {
          test: /\.less$/,
          include: [path.join(__dirname, 'node_modules/antd')],
          use: [
            'style-loader',
            {
              loader: 'css-loader',
              options: {
                importLoaders: 1,
              },
            },
            'less-loader',
            {
              loader: 'less-loader',
              options: {
                javascriptEnabled: true,
              },
            },
          ],
        },
        {
          test: /\.css$/,
          use: [
            {
              loader: 'style-loader',
            },
            {
              loader: 'css-loader',
              options: {
                modules: {
                  mode: 'local',
                  localIdentName: '[name]-[local]',
                },
              },
            },
          ],
        },
      ],
    },
  };
};

export default getConfig;
