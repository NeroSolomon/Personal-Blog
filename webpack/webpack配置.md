## webpack配置学习记录
1.path：资源的打包的静态目录，也可以说是静态目录<br>

2.publicPath：生产环境中cdn资源的目录<br>

个人理解，例如：
```
output: {
  path: `${__dirname}/dist`,
  publicPath: 'https://www.example.com/'
}
```
1.当我们按照以上配置构建项目时，所有资源文件就会打包到dist文件夹中<br>
2.dist文件夹中的资源就是我们网站线上所用的所有资源，其中index.html引用了各种静态资源<br>
3.在线上时，我们的资源时存放在cdn上的，所以我们的静态资源的地址需要指向cdn，在用户访问网站的时候，浏览器就会请求较近cdn上的资源，如果没有找到，就会回源到资源文件所在的cdn地址<br>
4.所以我们需要通过publicPath指定生产环境中index.html引用的静态资源的目录<br>
![配置化publicPath](./webpack/images/F2DAD5D9-FD84-4cc7-9444-32893966B1FC)<br>
![webpack读取配置构建](./webpack/images/B113CFCB-9BA3-4d6f-A62B-60FF1C2A8A57.png)<br>
![线上路径](./webpack/images/B2C263D0-F1E4-4bc2-A589-183F8C32D9A7.png)<br>