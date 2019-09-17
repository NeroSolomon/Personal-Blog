## JS问题

### 如何实现$？（即可以$.fun()，也可以$()）
```
个人思考：
1.一看到 . 就想到对象，但对象不能当作函数使用，那么考虑到原型链
2.注意$是全局的，不需要声明，那么就需要注册到全局函数里
3.prototype构造对象的属性，__proto__是实例的属性

function JQuery(node) {
  ...
}

window.$ = JQuery;
$.__proto__.fun = function() {
  ...
}
```

### 如何模拟new
先分析一下new的概念：<br>
1.先创建一个新对象<br>
2.将对象的原型执行构造函数的prototype<br>
3.将构造函数的this指向对象<br>
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

call
```
Function.prorotype.myCall = function(context) {
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