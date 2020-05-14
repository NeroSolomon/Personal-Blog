function debounce(fun, ms) {
  let timer
  return function () {
    const arg = arguments
    const that = this
    clearTimeout(timer)
    timer = setTimeout(() => {
      fun.apply(that, arg)
    }, ms)
  }
}