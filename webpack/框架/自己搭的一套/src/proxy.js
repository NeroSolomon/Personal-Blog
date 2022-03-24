import proxy from 'http-proxy-middleware'

// 测试代理，可自行增加
const proxyConfig = [
  proxy('/api/v1', {
    target: 'http://localhost:4000',
    changeOrigin: true,
    ws: true
  }),
  proxy('/login', {
    target: 'http://int-auth.nie.netease.com',
    changeOrigin: true,
    ws: true,
    pathRewrite: { '^/login': '/login' },
    autoRewrite: true,
  })
]

export default proxyConfig;