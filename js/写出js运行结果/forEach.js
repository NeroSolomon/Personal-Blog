var a = [1, 2, 3, 4]
a.forEach(item => item++)
console.log(a)
// 输出[1, 2, 3, 4]，数组没有改变

var b = [{a: 2}, {a: 3}]
b.forEach(item => item.a++)
console.log(b)
// 输出[{a: 3}, {a: 4}]

/* 
  这是为什么呢？
  是因为forEach不会改变数组的值
  在第一个forEach中，栈内存里面存的是基本类型，所以不会改变
  在第二个forEach中，栈内存里存的是对象的引用地址，所以我们其实没有改变栈中的引用地址，只是拿着引用地址改变了堆内存里的值，所以能够修改
*/
