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

### react错误捕获
componentDidCatch(error, info)，一个生命钩子函数，可以捕获当前组件下组件树任何位置暴露的问题

### 封装hook来发送网络请求
[参考链接](https://www.robinwieruch.de/react-hooks-fetch-data)

### react和vue的区别
1. react使用单向数据流，vue双向绑定，数据改变自动改动视图
2. react all in js，vue 使用模板将html、js、css分开
3. react其他功能需要安装依赖，例如redux、vue则内置vuex，而且vue提供了大量语法糖
4. react依赖较多，vue更少，因为react某些设计更复杂，例如虚拟dom实现，所以体积更大

### useEffect和useLayoutEffect的区别

1. useEffect 是异步执行的，而useLayoutEffect是同步执行的。
2. useEffect 的执行时机是浏览器完成渲染之后，而 useLayoutEffect 的执行时机是浏览器把内容真正渲染到界面之前，和 componentDidMount 等价。

简单例子：

```js
  // 如果这里用useEffect，会导致文本集合还未在页面中消失时，就将topTabActiveKey改动，导致动画效果闪动
  useLayoutEffect(() => {
    setTopTabActiveKey("listKey");
  }, [fileFilterType]);

  const tabsItems: TabsProps["items"] = useMemo(() => {
    return [
      {
        key: "listKey",
        label: "文件列表",
        children: renderFileListContent(),
      },
      ...(fileFilterType === FileFilterType.flod
        ? [
            {
              key: "textSetKey",
              label: "文本集合",
              children: renderTextSetContent(),
            },
          ]
        : []),
    ];
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [fileFilterType]);
```