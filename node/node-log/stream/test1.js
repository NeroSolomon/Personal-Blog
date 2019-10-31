// 标准输入输出
// process.stdin.pipe(process.stdout);

// 返回文件
// const http = require('http')
// const server = http.createServer((req, res) => {
//   if (req.method == 'POST') {
//     req.pipe(res)
//   }
// })
// server.listen(3000)


// // 两个文件互传
// const fs = require('fs');
// const path = require('path');

// const fileName = path.resolve(__dirname, './stream-data1.txt');

// const fileName2 = path.resolve(__dirname, './stream-data2.txt');

// //读取文件的stream对象
// const readStream = fs.createReadStream(fileName);
// // 写对象的stream对象
// const writeStream = fs.createWriteStream(fileName2);
// // 拷贝
// readStream.pipe(writeStream);
// readStream.on('end', function() {
//   console.log('拷贝完成');
// })

// 将文件内容传回请求方
const http = require('http')
const fs = require('fs');
const path = require('path');

const server = http.createServer((req, res) => {
  if (req.method == 'GET') {
    const fileName = path.resolve(__dirname, './stream-data1.txt');
    const stream = fs.createReadStream(fileName);
    stream.pipe(res);
  }
})
server.listen(3000);