## vue的一些总结

### vue的.native是什么
.native是给组件绑定原生事件的方法，例如说绑定一个封装好的button组件的click方法，发现不起作用。就需要使用@click.native

### 什么是服务端渲染
1.服务端渲染就是客户端请求浏览器，浏览器请求后端，后端返回html资源
2.优点：<br>
      a.首屏渲染快，后端直接返回页面数据，不用js处理好dom节点后再生成页面（MVVM框架的SPA应用页面生成方式） <br>
      b.利于seo，爬虫不会等页面生成才分析页面，所以SPA应用供爬虫分析的内容就大大减少了。而服务端渲染，直接把页面返回方便了爬虫<br>
3.缺点：<br>
      a.加重服务端的负担<br>
      b.只能执行到Mounted的生命钩子<br>
4.参考网址：[参考](https://www.jianshu.com/p/10b6074d772c)

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
