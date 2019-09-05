## 总结promise、Generator和async的关系

## 什么是promise
promise是为了解决es5中异步执行函数中的回调地狱实现的语法糖，使用then的方法链式调用回调
```
const promise = new Promise(function(resolve, reject) {
  // ... some code

  if (/* 异步操作成功 */){
    resolve(value);
  } else {
    reject(error);
  }
});

promise.then(function(value) {
  // success
}, function(error) {
  // failure
});
```
then中的第一个参数是执行resolve方法的函数，函数的参数是resolve提供的参数

## promise的缺点
promise使用then的链式调用虽然解决了回调地狱的问题，但是失去了语义性

## 什么是Generator
generator可以解决promise语义性，使得异步代码可以像同步代码一样执行
```
function* helloWorldGenerator() {
  yield 'hello';
  yield 'world';
  return 'ending';
}

var hw = helloWorldGenerator();
// generator函数不会执行，需要收到调用next()才会一条条执行

hw.next()
// { value: 'hello', done: false }

hw.next()
// { value: 'world', done: false }

hw.next()
// { value: 'ending', done: true }

hw.next()
// { value: undefined, done: true }
```

## 如何让Generator函数自动执行
可以使用thunk、co这种自动执行函数的插件，原理是自动调用next方法，等回调执行完之后再把执行权交还给Generator

## 什么是async
async函数也是一种让异步代码像同步代码执行的函数
```
const fs = require('fs');

async function f() {
  await readFile('/etc')
  await readFile('/etc2')
  return 'hello world'; // 和return await 'hello world'一样
}

// return的值会变成then中函数的参数
f().then(v => console.log(v))
```
通过async函数可以不用co、thunk等插件，使得异步代码可以按顺序一条条执行