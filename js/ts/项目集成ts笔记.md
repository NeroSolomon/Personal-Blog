### css module
当我们import css时发现typescript会报错，那是因为ts不认识css
那要怎么解决呢？很简单，我们只需要告诉ts css是什么就行了
修改.tsconfig.json
```json
{
  "include": [
    "src/typing.d.ts"
  ]
}

```

添加typing.d.ts
```js
declare module '*.module.scss' {
  // 告诉ts，这类文件的类型
  const classes: { readonly [key: string]: string }
  export default classes
}
```

[参考链接](https://juejin.cn/post/6844903560056930311)