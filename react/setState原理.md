[参考链接](https://zhuanlan.zhihu.com/p/44537887)

## state在class组件和function组件里的更新差别
先来看一个例子
```js
class Index extends React.Component<any,any>{
    constructor(props){
        super(props)
        this.state={
            number:0
        }
    }
    handerClick=()=>{
       for(let i = 0 ;i<5;i++){
           setTimeout(()=>{
               this.setState({ number:this.state.number+1 })
               console.log(this.state.number)
           },1000)
       }
    }

    render(){
        return <div>
            <button onClick={ this.handerClick } >num++</button>
        </div>
    }
}
```

```js
function Index(){
    const [ num ,setNumber ] = React.useState(0)
    const handerClick=()=>{
        for(let i=0; i<5;i++ ){
           setTimeout(() => {
                setNumber(num+1)
                console.log(num)
           }, 1000)
        }
    }
    return <button onClick={ handerClick } >{ num }</button>
}
```

------------公布答案-------------<br>
在第一个例子打印结果： 1 2 3 4 5<br>
在第二个例子打印结果： 0 0 0 0 0<br>

解析：<br>
1. 在class组件中，能够通过类的实例保存状态，但例子中的setState没有在react正常的函数执行上下午上执行，而是在setTimeout中执行，批量更新条件被破坏。所以可以直接获取到变化后到state
2. 在function组件中，没有方式来保存状态，每一次函数上下文执行，所有变量、常量都重新声明。所以无论setTimeout执行多少次，都是在当前函数的上下文执行，所以num = 0不会变