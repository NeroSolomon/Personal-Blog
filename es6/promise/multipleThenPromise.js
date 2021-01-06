// myPromise.js
/*
const PENDING = 'pending'
const FULFILLED = 'fulfilled'
const REJECTED = 'rejected'
class MyPromise {
  constructor(exector) {
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
    if (this.status !== PENDING) return
    this.status = FULFILLED
    this.value = value
    // 判断成功回调是否存在，如果存在就调用
    this.successCallback && this.successCallback(this.value)
  }

  reject = reason => {
    if (this.status !== PENDING) return
    this.status = REJECTED
    this.reason = reason
    // 判断失败回调是否存在，如果存在就调用
    this.failCallback && this.failCallback(this.reason)
  }

  then(successCallback, failCallback) {
    if (this.status === FULFILLED) {
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

// promise的then方法应该可以重复调用

promise.then(value => {
  console.log(1)
  console.log('resolve1', value)
})

promise.then(value => {
  console.log(2)
  console.log('resolve2', value)
})

promise.then(value => {
  console.log(3)
  console.log('resolve3', value)
})

// 但这个只会输出3和resolve3 success，显然不符合要求
*/

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
  // 定义一个成功回调的空数组
  successCallback = []
  // 定义一个失败回调的空数组
  failCallback = []

  resolve = value => {
    if(this.status !== PENDING) return
    this.status = FULFILLED
    this.value = value
    // 判断成功回调是否存在，如果存在就调用
    // 循环回调数组. 把数组前面的方法弹出来并且直接调用
    // shift方法是返回数组的第一项，会改变原数组，相当于在数组中每次弹出第一项
    // 数组变空了说明所有回调都执行了
    while(this.successCallback.length) this.successCallback.shift()(this.value)
  }

  reject = reason => {
    if(this.status !== PENDING) return
    this.status = REJECTED
    this.reason = reason
    // 判断失败回调是否存在，如果存在就调用
    // 和成功原理一样
    while(this.failCallback.length) this.failCallback.shift()(this.reason)
  }

  then (successCallback, failCallback) {
    if(this.status === FULFILLED) {
      successCallback(this.value)
    } else if (this.status === REJECTED) {
      failCallback(this.reason)
    } else {
      // 等待
      // 将成功回调和失败回调都保存在数组中
      this.successCallback.push(successCallback)
      this.failCallback.push(failCallback)
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

// promise的then方法应该可以重复调用

promise.then(value => {
  document.write(`1: resolve1 ${value}<br>`)
  console.log(1)
  console.log('resolve1', value)
})

promise.then(value => {
  document.write(`2: resolve2 ${value}<br>`)
  console.log(2)
  console.log('resolve2', value)
})

promise.then(value => {
  document.write(`3: resolve3 ${value}<br>`)
  console.log(3)
  console.log('resolve3', value)
})