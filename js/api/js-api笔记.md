## scrollIntoView
让元素滚动到可视区域
```js
element.scrollIntoView()
```

## getBoundingClientRect
获得元素相对视口的位置
```js
const obj = element.getBoundingClientRect()
```

## XMLHttpRequest
使用流程
1. 创建一个XMLHttpRequest对象
2. 设置监听状态变化的函数
3. 打开链接，设置url，方法，data
4. 发送http请求
5. 当readystate为4时，获取数据
6. 操作数据

readystate
1. 0：还没open
2. 1：open了还没send
3. 2：调用send方法
4. 3：正在接收数据，但还没接收完
5. 4：接收完数据