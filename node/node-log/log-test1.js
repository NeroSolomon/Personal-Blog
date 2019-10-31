const fs = require('fs');
const path = require('path');

const fileName = path.resolve(__dirname, './data/log-data1.txt');

// 读取文件内容
// fs.readFile(fileName, (err, data) => {
//   if (err) {
//     console.error(err);
//     return;
//   }
//   // data是二进制类型，需要转换
//   console.log(data.toString());
// })

// 写文件
// const content = '新写入内容'
// const opt = {
//   flag: 'a' // 追加。覆盖用'w'
// }
// fs.writeFile(fileName, content, opt, (err) => {
//   if (err) console.log(err);
// })

// 判断文件是否存在
fs.exists(fileName, (exists) => {
  console.log(exists);
})