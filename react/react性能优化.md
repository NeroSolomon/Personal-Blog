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