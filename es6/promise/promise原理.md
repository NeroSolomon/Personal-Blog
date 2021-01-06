## promise 原理

### 简单用法

```js
const promise = new Promise((resolve) => {
  setTimeout(() => {
    resolve(1);
  }, 2000);
});
promise.then((a) => alert(a));
promise.then((a) => alert(a + 1));
```

1. 构造函数接收一个 executor 立即执行函数
2. executor 立即执行函数接收一个 resolve 函数
3. promise 对象的 then 方法绑定状态变为 fulfilled 时的回调
4. resolve 函数被调用时会触发 then 方法中的回调

### 构造函数的简单实现

```js
function Promise(fn) {
  var state = 'pending',
    value = null,
    callbacks = [];  //callbacks为数组，因为可能同时有很多个回调

  this.then = function (onFulfilled) {
    if (state === 'pending') {
      callbacks.push(onFulfilled);
      return this;
    }
    onFulfilled(value)
    return this;
    // 返回this支持链式调用
    /*
      getUserId().then(function (id) {
          // 一些处理
      }).then(function (id) {
          // 一些处理
      });
    */
  };

  function resolve(value) {
    // 假如只像这样执行方法，就可能导致一个问题：在then方法注册回调之前，resolve函数就执行了
    /*
    callbacks.forEach(function (callback) {
      callback(value);
    });
    */

    // 例如：
    /*
    function getUserId() {
      return new Promise(function (resolve) {
        resolve(9876);
      });
    }
    getUserId().then(function (id) {
        // 一些处理
    }); */
    // 改状态
    value = newValue;
    state = 'fulfilled';
    // 所以resolve需要在then方法注册之后执行，这里通过setTimeout将resolve放到队列末尾
    setTimeout(function() {
      callbacks.forEach(function (callback) {
        callback(value);
      });
    }, 0)
  }

  fn(resolve);
}
```

### then 方法

1. then 方法返回一个新的 promise 对象
2. executor 自执行函数中的 resolve 参数调用时执行 then 方法的第一个回调函数 onResolved
3. executor 自执行函数中的 reject 参数调用时执行 then 方法的第二个回调函数 onReject

```js
Promise.prototype.then = function (onResolved, onRejected) {
  var self = this;
  var promise2;
  onResolved =
    typeof onResolved === "function"
      ? onResolved
      : function (value) {
          return value;
        };
  onRejected =
    typeof onRejected === "function"
      ? onRejected
      : function (reason) {
          throw reason;
        };
  //promise对象当前状态为resolved
  if (self.status === "resolved") {
    return (promise2 = new Promise(function (resolve, reject) {
      try {
        //调用onResolve回调函数
        var x = onResolved(self.data);
        //如果onResolve回调函数返回值为一个promise对象
        if (x instanceof Promise) {
          //将它的结果作为promise2的结果
          x.then(resolve, reject);
        } else {
          resolve(x); //执行promise2的onResolve回调
        }
      } catch (e) {
        reject(e); //执行promise2的onReject回调
      }
    }));
  }
  //promise对象当前状态为rejected
  if (self.status === "rejected") {
    return (promise2 = new Promise(function (resolve, reject) {
      try {
        var x = onRejected(self.data);
        if (x instanceof Promise) {
          x.then(resolve, reject);
        } else {
          resolve(x);
        }
      } catch (e) {
        reject(e);
      }
    }));
  }
  //promise对象当前状态为pending
  //此时并不能确定调用onResolved还是onRejected，需要等当前Promise状态确定。
  //所以需要将callBack放入promise1的回调数组中
  if (self.status === "pending") {
    return (promise2 = new Promise(function (resolve, reject) {
      self.onResolvedCallback.push(function (value) {
        try {
          var x = onResolved(self.data);
          if (x instanceof Promise) {
            x.then(resolve, reject);
          } else {
            resolve(x);
          }
        } catch (e) {
          reject(e);
        }
      });
      self.onRejectedCallback.push(function (reason) {
        try {
          var x = onRejected(self.data);
          if (x instanceof Promise) {
            x.then(resolve, reject);
          } else {
            resolve(x);
          }
        } catch (e) {
          reject(e);
        }
      });
    }));
  }
};
```

[参考网址](https://www.cnblogs.com/163yun/p/9505378.html)
