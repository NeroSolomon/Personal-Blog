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

  // 使用all方法是用类直接调用，那么all一定是一个静态方法：Promise.all
  static all(array) {
    //  结果数组
    let result = []
    // 计数器
    let index = 0
    return new Promise((resolve, reject) => {
      let addData = (key, value) => {
        result[key] = value
        index++
        // 如果计数器和数组长度相同，那说明所有的元素都执行完毕了，就可以输出了
        if (index === array.length) {
          resolve(result)
        }
      }
      // 对传递的数组进行遍历
      for (let i = 0; i < array.length; i++) {
        let current = array[i]
        if (current instanceof MyPromise) {
          // promise对象就执行then，如果是resolve就把值添加到数组中去，如果是错误就执行reject返回
          current.then(value => addData(i, value), reason => reject(reason))
        } else {
          // 普通值就加到对应的数组中去
          addData(i, array[i])
        }
      }
    })
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