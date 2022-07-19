## vue的一些总结

### vue的.native是什么
.native是给组件绑定原生事件的方法，例如说绑定一个封装好的button组件的click方法，发现不起作用。就需要使用@click.native

### 客户端渲染
页面在 JavaScript，CSS 等资源文件加载完毕后开始渲染，路由为客户端路由，也就是我们经常谈到的 SPA（Single Page Application）。

### 什么是服务端渲染
1.服务端渲染就是客户端请求浏览器，浏览器请求后端，后端返回html资源
2.优点：<br>
      a.首屏渲染快，后端直接返回页面数据，不用js处理好dom节点后再生成页面（MVVM框架的SPA应用页面生成方式） <br>
      b.利于seo，爬虫不会等页面生成才分析页面，所以SPA应用供爬虫分析的内容就大大减少了。而服务端渲染，直接把页面返回方便了爬虫<br>
3.缺点：<br>
      a.加重服务端的负担<br>
      b.只能执行到Mounted的生命钩子<br>
4.参考网址：[参考](https://www.jianshu.com/p/10b6074d772c)

### 同构渲染
英文表述为 Isomorphic 或 Universal，即编写的 JavaScript 代码可同时运行于浏览器及 Node.js 两套环境中，用服务端渲染来提升首屏的加载速度，首屏之后的路由由客户端控制，即在用户到达首屏后，整个应用仍是一个 SPA。

### vue中，如果在实例创建之后添加新的属性到实例上，它不会触发视图更新
```
<template>
     <div>
      <p @click="addd(obj)">{{obj.d}}</p>
      <p @click="adde(obj)">{{obj.e}}</p>
      </div>
</template>

<script>
  export default {
      data(){
            return {
                  obj:{}
            }
      },
      mounted() {
            this.obj = {d: 0};
            this.obj.e = 0;
            console.log('after--', this.obj);
      },
      methods: {
            addd(item) {
                  item.d = item.d + 1;
                  console.log('item--',item);
            },
            adde(item) {
                  item.e = item.e + 1;
                  console.log('item--',item);
            }
      }
  }
</scirpt>
```

在上面例子中由于e是在实例创建之后新增的属性，所以adde方法被调用后，视图不会被更新，但在控制台中可以发现e更新了<br>
虽然Vue 不允许在已经创建的实例上动态添加新的根级响应式属性 (root-level reactive property)。然而它可以使用 Vue.set(object, key, value) 方法将响应属性添加到嵌套的对象上：
```
mounted() {
      this.obj = {d: 0};
      this.$set(this.obj, 'e', 0);
      console.log('after--', this.obj);
}
```

### 自定义指令
```javascript
// 注册一个全局自定义指令 `v-focus`
Vue.directive('focus', {
  // 当被绑定的元素插入到 DOM 中时……
  inserted: function (el) {
    // 聚焦元素
    el.focus()
  }
})

// 局部指令
directives: { 
    focus: { 
        // 指令的定义
        inserted: function (el) {
            el.focus() 
        }
    }
}

// 然后你可以在模板中任何元素上使用新的 v-focus 属性，如下：
<input v-focus>
```

### v-model
```javascript
// v-modal实际是使用了一个名为value的prop，和监听input事件（但像单选框、复选框等类型的输入控件可能会将value特性用于不同的目的，这时候就需要使用model属性来解决）

// text
Vue.component('base-text', {
  props: {
    value: String
  },
  template: `
    <input
      type="text"
      v-bind:value="value"
      v-on:change="$emit('input', $event.target.value)"
    >
  `
})

<base-text v-model="value"></base-text>

// checkbox
Vue.component('base-checkbox', {
  model: {
    prop: 'checked',
    event: 'change'
  },
  props: {
    checked: Boolean
  },
  template: `
    <input
      type="checkbox"
      v-bind:checked="checked"
      v-on:change="$emit('change', $event.target.checked)"
    >
  `
})

// react中是在组件中保留了value和onchange的props
```

### 作用域插槽，用于子组件向父组件传值
```javascript
// 子组件绑定数据
<slot name="test" v-bind="data"></slot>

<template v-slot:test="data">{{ data }}</template>
// 在父组件中可以拿到子组件绑定的数据
```

### 监听vue全局错误 vue.config.errorHandler
```javascript
Vue.config.errorHandler = function (err, vm, info) {
  console.error('error---', err)
  console.info('vm---', vm)
  console.info('info---', info)
}
```

### created，已实例化，但还没有挂载，也就是html还没渲染

### mounted，已完成挂载，html完成渲染

### vuex mapState，可以将state里面的属性快速映射成computed属性
```javascript
computed: {
  ...mapState({
    // 箭头函数可使代码更简练
    count: state => state.count
  })
}

// 或者计算属性和state的属性名一致的话，可以直接给字符串
computed: {
  ...mapState(['count'])
}
```

### .sync语法糖，是为了能够让子组件更方便地改变父组件的值，其实和原本的emit方式一样
```javascript
//普通写法
<text-document
    v-bind:title="title"
    v-on:update:title="title = $event"
/>

//语法糖写法
<text-document   
	v-bind:title.sync="title"  
/>
  
Vue.component('text-document', { 
  props: ['title'], 
  template: `
    <div> 
      <button @click='setNewTitle'>更新标题</button>
    </div>
  `,
  methods:{
   setNewTitle:function(){
       //手动进行更新父组件中的值
       this.$emit('update:title', 'new title')
    } 
  }
})

var vm = new Vue({
  el:'#app',
  data:{
     title:'old title'
  }, 
});
```

## vue中的update方法
update会在data的数据发生变化的时候被触发，所以重复给data里面的属性赋同样的值不会触发update，<br>
但没有相同引用的对象都是不相等的，所以赋值对象的话，无论对象是否一致，都会触发update
```javascript
'string' == 'string' // true
{ a: 1 } == { a: 1 } // false
```

所以：
```vue
<template>
  <div id="app">
    {{ message }}
    {{ obj.a }}
    <button @click="message = 'new string'">change string<button>
    <button @click="obj = { a: 1 }">change obj<button>
  </div>
</template>

<script>
  var app = new Vue({
    el: '#app',
    data: {
      message: 'old string',
      obj: { a: 1 }
    },
    updated() {
      console.log('update')
    }
  })
</script>
// change string只会触发一次update，后面因为值都相等，所以就不会触发update了
// change obj每一次都会触发update
```

## vue中的template标签
类似原生js中使用createDocumentFragment创建的文档片段，他不会被渲染在dom中，只是作为一个容器<br>
react中也有类似的React.Fragment