## css的一些知识点

### z-index
[参考网址](https://webdesign.tutsplus.com/zh-hans/articles/what-you-may-not-know-about-the-z-index-property--webdesign-16892)

### Reset/Normalize：
Reset：将所有浏览器的默认样式都统一化，注重的是跨浏览器统一样式，用户还要自行添加一些默认样式。
Normalize：会根据各个浏览器的不同保留有用的浏览器特色样式，修复浏览器的一些BUG，更注重易用性。

### 更改滚动条样式
```css
::-webkit-scrollbar{
  width: 5px;
  height: 5px;
}

::-webkit-scrollbar-thumb{
  border-radius: 1em;
  background-color: rgba(50,50,50,.3);
}

::-webkit-scrollbar-track{
  border-radius: 1em;
  background-color: rgba(50,50,50,.1);
}
```

### word-wrap、word-break、white-space的区别
1. word-wrap: 控制是否允许单词内换行
2. word-break: 控制单词在换行的时候如何断开
3. white-space: 控制空白符号的处理方式