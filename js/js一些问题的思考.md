## JS问题

1.如何实现$？（即可以$.fun()，也可以$()）
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