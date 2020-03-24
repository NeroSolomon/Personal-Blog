function throttle(fun, ms) {
  let timer

  return function() {
    let that = this
    let arg = arguments
    if (!timer) {
      timer = setTimeout(() => {
        fun.apply(that, arg)
        timer = null
      }, ms)
    }
  }
}