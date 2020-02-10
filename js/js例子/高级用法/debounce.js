function debounce(fun, ms) {
  let timer
  return function () {
    const arg = arguments
    const that = this
    if (timer) clearTimeout(timer)
    else {
      timer = setTimeout(() => {
        fun.apply(that, arg)
      }, ms)
    }
  }
}