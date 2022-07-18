#!/usr/bin/env node
const { program } = require('commander')

console.log('开始测试')

// cli版本
program.version(require('./package').version, '-v, --version', 'cli的版本')

// 设置选项
program.option('-d, --debug', '调试一下').option('-l, --list', '测试一下').action((opts, command) => {
    // 进行逻辑处理
    if (opts.debug) {
        console.log('调试成功')
        console.log(require('./data.json').val)
    } else if (opts.list) {
        console.log('测试成功')
    }
})

program.parse()
