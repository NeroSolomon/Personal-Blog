var testFun = (function f(num) {
  if (num == 1) {
    return 1
  } else {
    return num * f(num - 1)
  }
})

console.log(testFun(5))
console.log(typeof testFun) // function