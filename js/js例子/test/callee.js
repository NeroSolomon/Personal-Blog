const fun = function() {
  console.log(arguments.callee)
}

fun()