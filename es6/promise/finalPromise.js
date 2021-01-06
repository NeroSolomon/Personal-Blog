// 在promise.then中返回Promise的情况，链式调用，解决回调地狱
// myPromise.js
const PENDING = 'pending'
const FULFILLED = 'fulfilled'
const REJECTED = 'rejected'
class MyPromise {
  constructor(exector) {
    // 捕获错误，如果有错误就执行reject
    try {
      exector(this.resolve, this.reject)
    } catch (e) {
      this.reject(e)
    }
  }


  status = PENDING
  value = undefined
  reason = undefined
  successCallback = []
  failCallback = []

  resolve = value => {
    if (this.status !== PENDING) return
    this.status = FULFILLED
    this.value = value
    // 异步回调传值
    // 调用的时候不需要传值，因为下面push到里面的时候已经处理好了
    while (this.successCallback.length) this.successCallback.shift()()
  }

  reject = reason => {
    if (this.status !== PENDING) return
    this.status = REJECTED
    this.reason = reason
    // 异步回调传值
    // 调用的时候不需要传值，因为下面push到里面的时候已经处理好了
    while (this.failCallback.length) this.failCallback.shift()()
  }

  // 参数是可选的
  then(successCallback = value => value, failCallback = reason => {
    throw reason
  }) {
    let promise2 = new Promise((resolve, reject) => {
      if (this.status === FULFILLED) {
        // x是上一个promise回调函数的return返回值
        // 判断 x 的值时普通值还是promise对象
        // 如果是普通值 直接调用resolve
        // 如果是promise对象 查看promise对象返回的结果
        // 再根据promise对象返回的结果 决定调用resolve还是reject

        // 因为new Promise需要执行完成之后才有promise2，同步代码中没有pormise2，
        setTimeout(() => {
          // 如果回调中报错的话就执行reject
          try {
            let x = successCallback(this.value)
            resolvePromise(promise2, x, resolve, reject)
          } catch (e) {
            reject(e)
          }
        }, 0)
      } else if (this.status === REJECTED) {
        setTimeout(() => {
          // 如果回调中报错的话就执行reject
          try {
            let x = failCallback(this.reason)
            resolvePromise(promise2, x, resolve, reject)
          } catch (e) {
            reject(e)
          }
        }, 0)
      } else {
        // 处理异步的成功错误情况
        this.successCallback.push(() => {
          setTimeout(() => {
            // 如果回调中报错的话就执行reject
            try {
              let x = successCallback(this.value)
              resolvePromise(promise2, x, resolve, reject)
            } catch (e) {
              reject(e)
            }
          }, 0)
        })
        this.failCallback.push(() => {
          setTimeout(() => {
            // 如果回调中报错的话就执行reject
            try {
              let x = failCallback(this.reason)
              resolvePromise(promise2, x, resolve, reject)
            } catch (e) {
              reject(e)
            }
          }, 0)
        })
      }
    });
    // 返回promise
    return promise2
  }
}

function resolvePromise(promise2, x, resolve, reject) {
  // 如果相等了，说明return的是自己，抛出类型错误并返回
  if (promise2 === x) {
    return reject(new TypeError('Chaining cycle detected for promise #<Promise>'))
  }
  // 判断x是不是其实例对象
  if (x instanceof MyPromise) {
    x.then(resolve, reject)
  } else {
    resolve(x)
  }
}

let promise = new MyPromise((resolve, reject) => {
  // 一个异步方法
  setTimeout(() => {
    resolve('succ')
  }, 2000)
})

promise.then(value => {
  document.write(`1<br>resolve ${value}<br>`)
  console.log(1)
  console.log('resolve', value)
  return 'aaa'
}, reason => {
  console.log(2)
  console.log(reason.message)
  return 100
}).then(value => {
  document.write(`3<br>${value}`)
  console.log(3)
  console.log(value);
}, reason => {
  console.log(4)
  console.log(reason.message)
})