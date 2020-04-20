var a = 1
var b = Number(1)
a === b
// true
typeof a
// "number"
typeof b
// "number"

a.length // error
b.length // undefined，证明b是包装对象