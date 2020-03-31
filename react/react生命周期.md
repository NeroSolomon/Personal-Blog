## react生命周期

## react 15的生命周期
1. constructor，绑定实例的this
2. getDefaultProps, 初始化props
3. getInitialState, 初始化state
4. componentWillMount, 准备加载组件
5. render, 加载组件，创建虚拟dom树
6. componentDidMount, 组件首次渲染完成时调用
7. componentWillReceivePorps(nextProps), 除加载阶段，每次props更新时调用
8. shouldComponentUpdate(nextProps, nextState), return false不更新，return true更新，默认return true
9. componentWillUpdata(nextProps, nextState), 即将重新渲染时调用
10. render 更新dom树
11. componentDidUpdate, 重新渲染完成
12. componentWillUnmount, 即将卸载组件

## react 16的生命周期
1. constructor，绑定实例的this
2. static getDerivedStateFromProps(props, state), 每次获取新的props和state时调用，返回一个新的state，返回null表示不用更新
3. render, 加载组件，创建虚拟dom树
4. componentDidMount
5. static getDerivedStateFromProps(props, state)
6. shouldComponentUpdate(nextProps, nextState)
7. render, 更新dom树
8. getSnapshotBeforeUpdate(prevProps, prevState) 在render触发后，渲染完成之前，返回一个值，作为componentDidUpdate
9. componentDidUpdate
10. componentWillUnmount, 即将卸载组件
11. componentDidCatch 任何一处js发生错误时，触发的钩子

[参考链接](https://segmentfault.com/a/1190000016617400?utm_source=tag-newest)