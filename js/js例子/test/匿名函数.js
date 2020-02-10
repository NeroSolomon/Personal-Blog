var name = 'lala'
var obj = {
  name: 'hihi',
  sayName: function() {
    return function() {
      return this.name
    }()
  }
}

// console.log(obj.sayName()) // lala

var obj2 = {
  name: 'xix',
  sayName: function() {
    return this.name
  }
}

// obj2.sayName = obj2.sayName

console.log(obj2.sayName = obj2.sayName) // 输出一个函数体