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