// myPromise.js
/*
// 定义成常量是为了复用且代码有提示
const PENDING = 'pending' // 等待
const FULFILLED = 'fulfilled' // 成功
const REJECTED = 'rejected' // 失败
// 定义一个构造函数
class MyPromise {
  constructor (executor) {
    // executor是一个执行器，进入会立即执行，并传入resolve和reject方法
    executor(this.resolve, this.reject)
  }

  // 实例对象的一个属性，初始为等待
  status = PENDING
  // 成功之后的值
  value = undefined
  // 失败之后的原因
  reason = undefined

  // resolve和reject为什么要用箭头函数？
  // 如果直接调用的话，普通函数this指向的是window或者undefined
  // 用箭头函数就可以让this指向当前实例对象
  resolve = value => {
    // 判断状态是不是等待，阻止程序向下执行
    if(this.status !== PENDING) return
    // 将状态改成成功
    this.status = FULFILLED
    // 保存成功之后的值
    this.value = value
  }
  reject = reason => {
    if(this.status !== PENDING) return
    // 将状态改为失败
    this.status = REJECTED
    // 保存失败之后的原因
    this.reason = reason
  }
  then (successCallback, failCallback) {
    //判断状态
    if(this.status === FULFILLED) {
      // 调用成功回调，并且把值返回
      successCallback(this.value)
    } else if (this.status === REJECTED) {
      // 调用失败回调，并且把原因返回
      failCallback(this.reason)
    }
  }
}

let promise = new MyPromise((resolve, reject) => {
  // 我们期待的效果是延迟2秒后执行成功的回调并且将参数打印
  // 但是结果是什么也没有给我们返回
  // 这是因为主线程代码会立即执行，setTimeout是异步代码，then也会马上执行
  // 这个时候判断promise状态，状态是pending，所以then里面的回调函数都不会被执行
  setTimeout(() => {
    resolve("success");
  }, 2000);
});

promise.then(
  (value) => {
    document.write(`resolve ${value}`);
    console.log("resolve", value);
  },
  (reason) => {
    document.write(`reject ${reason}`);
    console.log("reject", reason);
  }
);
*/


// 上面的方法无法处理异步代码，所以需要改造一下

// myPromise.js
const PENDING = 'pending'
const FULFILLED = 'fulfilled'
const REJECTED = 'rejected'
class MyPromise {
  constructor (exector) {
    exector(this.resolve, this.reject)
  }
  status = PENDING
  value = undefined
  reason = undefined
  // 定义一个成功回调参数
  successCallback = undefined
  // 定义一个失败回调参数
  failCallback = undefined

  resolve = value => {
    if(this.status !== PENDING) return
    this.status = FULFILLED
    this.value = value
    // 判断成功回调是否存在，如果存在就调用
    this.successCallback && this.successCallback(this.value)
  }

  reject = reason => {
    if(this.status !== PENDING) return
    this.status = REJECTED
    this.reason = reason
    // 判断失败回调是否存在，如果存在就调用
    this.failCallback && this.failCallback(this.reason)
  }

  then (successCallback, failCallback) {
    if(this.status === FULFILLED) {
      successCallback(this.value)
    } else if (this.status === REJECTED) {
      failCallback(this.reason)
    } else {
      // 等待
      // 因为并不知道状态，所以将成功回调和失败回调存储起来
      // 等到执行成功失败函数的时候再传递
      this.successCallback = successCallback
      this.failCallback = failCallback
    }
  }
}

let promise = new MyPromise((resolve, reject) => {
  // 我们期待的效果是延迟2秒后执行成功的回调并且将参数打印
  // 但是结果是什么也没有给我们返回
  // 这是因为主线程代码会立即执行，setTimeout是异步代码，then也会马上执行
  // 这个时候判断promise状态，状态是pending，所以then里面的回调函数都不会被执行
  setTimeout(() => {
    resolve("success");
  }, 2000);
});

promise.then(
  (value) => {
    document.write(`resolve ${value}`);
    console.log("resolve", value);
  },
  (reason) => {
    document.write(`reject ${reason}`);
    console.log("reject", reason);
  }
);