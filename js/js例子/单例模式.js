let ProxyCreateSingleton = (function(){
  let instance = null
  return function(name){
      if(instance){
          return instance
      }
      return instance = new Singleton(name)
  }
})()


function Singleton(name) {
  this.name = name
}

Singleton.prototype.sayName = function() {
  console.log(this.name)
}

// 雷点：箭头函数的this是声明时的作用域的this，而非调用时的this，这里会导致拿到的是window.name
// Singleton.prototype.sayName = () =>{
//   console.log(this.name)
// }

const winner = new ProxyCreateSingleton('winner')
winner.sayName()