## 性能优化

### 函数式性能优化
注意：在函数式组件里每次重新渲染(setXXX, props改变)，函数组件都会重头开始重新执行

#### memo、useCallback
[参考链接](https://www.cnblogs.com/taoweng/p/11897040.html)

### immutable
[参考链接](https://blog.csdn.net/javascript_panjialin/article/details/53841688)
1. 通过is、和===快速对比prop的工具，用于在shouldComponentUpdate中控制组件rerender
```js

import { is } from 'immutable';
shouldComponentUpdate: (nextProps, nextState) => {
  return !(this.props === nextProps || is(this.props, nextProps)) ||
         !(this.state === nextState || is(this.state, nextState));
}
```
2. 快速创建一个不可变对象，对这个对象操作会生成一个对象的拷贝
```js
// 假如不使用immutable
import '_' from 'lodash';

const Component = React.createClass({
  getInitialState() {
    return {
      data: { times: 0 }
    }
  },
  handleAdd() {
    let data = _.cloneDeep(this.state.data);
    data.times = data.times + 1;
    this.setState({ data: data });
    // 如果上面不做 cloneDeep，下面打印的结果会是已经加 1 后的值。
    console.log(this.state.data.times);
  }

```

```js
  getInitialState() {
    return {
      data: Map({ times: 0 })
    }
  },
  handleAdd() {
    this.setState({ data: this.state.data.update('times', v => v + 1) });
    // 这时的 times 并不会改变
    console.log(this.state.data.get('times'));
  }
```

### 七大优化方式
[参考链接](https://mp.weixin.qq.com/s/JYglFA5sTnUikimfyLRbCQ)

1. 优化项目体积
  a. include 或 exclude 限制 loader 范围<br>
  b. happypack多进程编译
```js
/* 多线程编译 */
new HappyPack({
    id:'babel',
    loaders:['babel-loader?cacheDirectory=true']
})
```
  c. 缓存babel编译过的文件
```js
  loaders:['babel-loader?cacheDirectory=true']
```
  d. 按需加载、引入
```js
// 例如对antd的按需
// .babelrc 增加对 antd 样式按需引入
["import", {
    "libraryName":
    "antd",
    "libraryDirectory": "es",
    "style": true
}]

```

2. 路由懒加载

3. 组件颗粒化

4. shouldComponentUpdate ,PureComponent 和 React.memo ,immetable.js 助力性能调优

5. 规范写法，合理处理细节问题，少用每次创建新引用的props，使用key不要用index、避免重复更新等

6. 不要滥用状态管理

7. 海量数据优化-时间分片，虚拟列表