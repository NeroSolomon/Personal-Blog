## JS问题

### 如何实现$？（即可以$.fun()，也可以$()）
```js
// 个人思考：
// 1.一看到 . 就想到对象，但对象不能当作函数使用，那么考虑到原型链
// 2.注意$是全局的，不需要声明，那么就需要注册到全局函数里
// 3.prototype构造对象的属性，__proto__是实例的属性

function JQuery(node) {
  ...
}

window.$ = JQuery;
$.__proto__.fun = function() {
  ...
}
```

### juqery 链式调用
```js
aQuery().init().name()

aQuery.prototype = {
    init: function() {
        return this;
    },
    name: function() {
        return this
    }
}
```

### 如何模拟new
先分析一下new的概念：<br>
1.先创建一个新对象<br>
2.将对象的原型指向构造函数的prototype<br>
3.将构造函数的this指向对象，为对象变量赋值<br>
4.返回对象<br>

模拟：
```
function ObjectFactory() {
  var obj = new Object(); // 构造一个新对象
  var Constructor = [].shift.call(argument); // 获得构造函数
  obj.__proto__ = Constructor.prototype; // 将新对象的原型指向构造函数的prototype
  Constructor.apply(obj, argument); // 将构造函数的this指向新对象
  return obj; // 返回新对象
}
```

### 如何判断客户端
移动端：isMobile: ("ontouchstart" in window || navigator.msMaxTouchPoints) ? true : false<br>
其他：利用navigator.userAgent并使用正则匹配看看有没有客户端信息，例：
```
let ua = navigator.userAgent;

isWechat: /micromessenger/gi.test(ua)
```

### 手动实现call\apply\bind
call
```
Function.prototype.myCall = function (context) {
  context.fn = this; // 获取函数

  const args = arguments.slice(1); // 获取参数
  const result = context.fn(...args); // 调用函数
  delete context.fn; // 删除
  return result; // 返回结果
}
```

apply
```
Function.prototype.myApply = function(context) {
  context.fn = this; // 获取函数，并通过context绑定this

  let result;
  if (arguments[1]) { // 判断是否存在第二个参数
    result = context.fn(...arguments[1])；
  } else {
    result = context.fn()
  }
  delete context.fn; // 删除
  return reslut;
}
```

bind
```
Function.prorotype.myBind = function(context) {
  const _this = this;
  let args = arguments.slice(1);

  return function F() {
    if (this instanceof F) { // 假如是构造函数的调用方式
      return new _this(..args, ...arguments);
    }
    return _this.apply(context, args.concat(arguments));
  }
}
```

### arguments是数组吗？
不是数组，是类数组，有length属性，可以使用下标取值，可以使用：[...arguments]、Array.prototype.slice.call(arguments)和Array.from(arguments)

### this的分类
1.window<br>
2.调用函数的对象<br>
3.构造函数指向新的对象<br>
4.call\apply\bind改变指向的对象

### js模块化
AMD require.js<br>
CMD sea.js<br>
es Module <br>
commom.js node服务端<br>

### 什么是事件循环
1.js是单线程的，任务只能在主线程中按顺序执行<br>
2.为了异步IO不阻塞主线程，js将异步任务挂到任务队列中，先执行同步代码<br>
3.等主线程中的同步代码执行完，才执行任务队列中的异步代码<br>
4.异步代码分为微任务和宏任务，例如promise就是微任务，setTimeout就是宏任务，先执行微任务，再执行宏任务<br>
[事件循环](./事件循环.md)

### 获取图片的原始宽高
原始宽高：不受外部设置宽高影响，image.naturalWidth和image.naturalHeight

### 浏览器的动画最小间隔
1000ms/60FPS = 1.667ms

### == 类型转换规则
null == undefined //不转换<br>
null\undefined == 其余非null\undefined // false<br>
原始类型 == 对象 // 原始类型转换为数字。对象转为对应的原始类型，如果是date对象先使用toString方法，再用valueOf，否则相反，接着再转换为数字<br>
原始类型 == 原始类型 // 转换为数字<br>
例：
```
![] == []
// 首先!优先级比==高，上式转为false == []
// 然后false转为数值Number(false) = 0
// []先调用valueOf()得到[]，无法比较，然后使用toSting得到""
// 所以式子变成 0（原始类型）== ""（原始类型），Number("") = 0
// 所以结果为true
```

## 函数防抖
```javascript
function debounce(fun, ms) {
  let timer;
  return function() {
    const context = this;
    const args = arguments;

    clearTimeout(timer);

    timer = setTimeout(() => {
      fun.apply(context, args)
    }, ms)
  }
}
```

## promise、generator、和async的关系
1.promise是为了解决异步函数回调地狱的问题<br>
2.generator是为了解决promise then语法语义差的问题<br>
3.async是为了解决generator自动执行yield语句的问题，使得异步代码能够同步执行，但是注意await的操作必须要返回一个promise对象，不然不能起等待作用<br>
4.总结：promise、generator、async是一个递进的关系，解决前者的痛点

## promise 每个promise中return的新promise对象，或者resolve中传进的参数，都是紧接着后面.then()中的参数。

## Promise.all
可以同时执行多个promise，然后会return一个promise对象，执行then可以获得所有promise的执行结果，执行结果按顺序存在then的参数中
```javascript
const promise1 = Promise.resolve(3);
const promise2 = 42;
const promise3 = new Promise(function(resolve, reject) {
  setTimeout(resolve, 100, 'foo');
});

Promise.all([promise1, promise2, promise3]).then(function(values) {
  console.log(values);
});
// expected output: Array [3, 42, "foo"]
```

通过async函数，可以不需要then
```javascript
const promise1 = Promise.resolve(3);
const promise2 = 42;
const promise3 = new Promise(function(resolve, reject) {
  setTimeout(resolve, 100, 'foo');
});

async function fun() {
  const reuslt = await Promise.all([promise1, promise2, promise3]);
  console.log(result)
}
fun()
// expected output: Array [3, 42, "foo"]
```


## js获得黏贴内容
```javascript
// 监听paste事件
onPaste(e) {
  const that = this
  if (!e) return
  if (e.clipboardData) {
    for (let i = 0, len = e.clipboardData.items.length; i < len; i++) {
      const item = e.clipboardData.items[i]
      if (item.kind === 'string') {
        item.getAsString(function(str) {
          that.pasteText = str
        })
      }
    }
  } else if (window.clipboardData) {
    const str = window.clipboardData.getData('Text')
    this.pasteText = str
  }
}
```

## 函数的执行过程
1. 创建函数的作用域，压入作用域栈中，确定函数里的this指向
2. 在函数的作用域中添加形参、变量、函数
3. 将函数的作用域添加到作用链域的最顶端
3. 函数代码执行完后，弹出函数作用域

## 为什么利用多个域名来存储网站资源
1. CDN缓存更方便
2. 突破浏览器并发限制
3. 节约cookie带宽
4. 节约主域名的连接数，优化页面响应速度
5. 防止不必要的安全问题

## 什么是执行上下文
当前代码的一个运行环境，一般会存储在执行栈中，栈顶的执行上下文可以访问整个栈中的变量

## 形参和实参
1. 形参：函数声明时设置的参数
2. 实参：函数调用时传入的参数

## requestAnimation
requestAnimation自动适配屏幕的刷新频率，避免掉帧，由系统来决定回调函数的执行时机，节省cpu。

## 将页面加入收藏夹
```js
  var title = document.title;
  var URL = document.URL;
  window.external.addFavorite(url, title);
```