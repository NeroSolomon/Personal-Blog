## npm cli开发

1. npm init
2. 在package.json中添加
   ```json
    {
        "bin": {
            // 这里是执行命令
            // 也就是直接执行mycli
            "mycli": "./index.js"
        },
    }
   ```
3. 编辑index.js
   ```js
    #!/usr/bin/env node
    // 上面这个很重要，这里表明 index.js 是 node 可执行文件。

    console.log("执行成功")
   ```
4. npm i -g
5. mycli
6. 参考链接：https://segmentfault.com/a/1190000039110450