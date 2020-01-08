## React笔记

### 根据路由动态引入js文件（webpack3）
通过webpack，我们可以把资源文件打包进bundle.js中，但随着项目越来越大，页面/组件越来越多，假如继续打包到同一个bundle.js中的话，就会使得首页加载得越来越慢，试想一下，假如存在两个页面：/index和/account，我们在访问/index时却要等待/account的资源加载完毕，显然是不合理的。<br>

那么，我们如何通过我们的需求加载资源呢，路由则是一个很好的切入点，只有我们访问了对应的路由，才去加载对应的资源。<br>

首先，我们需要在webpack中作配置，webpack称打包的资源为chunk，我们需要把不同页面对应的路由打包成不同的chunk：
```
output: {
  ...
  chunkFilename: 'js/[name].[chunkhash:8].js'
  ...
},
```

然后，我们就修改路由，webpack根据路由打包资源文件，原来我们的routes.js可能长这样
```
import React from 'react';
import App from './containers/app/App.jsx';

<Route path="app" breadcrumbName="menu.app" component={App} />
```

修改后，我们的routes.js长这样
```
import React from 'react';
const App = (location, callback) => {
  require.ensure([], require => {
    callback(null, require('./containers/app/App.jsx').default);
  }, 'app');
};

<Route path="app" breadcrumbName="menu.app" getComponent={App} />
```

现在构建项目，你会发现你的项目加载资源是通过路由动态加载了<br>
参考网址:[https://github.com/eyasliu/blog/issues/8](https://github.com/eyasliu/blog/issues/8)

### 在jsx中的标签上计算，动态添加属性
```jsx
<span
  {...('cn' == locale ? { className: style['f-wsn'] } : {})}
></span>
```

### 组件预加载
[react-loadable](https://github.com/jamiebuilds/react-loadable#preloading)