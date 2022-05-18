## cypress接入ci/cd
通过以下方式，可以将自动化测试接入自动化测试

### 构建cypress运行镜像
首先，我们需要构建一个镜像，里面有cypress自动化测试需要的依赖，供cypress运行，以web-console为例

当然，大家如果没有特殊需求的话，可以使用以下镜像进行自动化测试。

- xxxxx:cypress
- xxx/browsers:node14.17.0-chrome91-ff89

1. step 1：创建Dockerfile，填入以下内容
```Dockerfile
FROM xxxxx:ci

RUN apt-get update && \
    apt-get install libgtk2.0-0 libgtk-3-0 libgbm-dev libnotify-dev libgconf-2-4 libnss3 libxss1 libasound2 libxtst6 xauth xvfb
```

2. step 2：构建
```bash
docker build -t xxxxx:cypress .
```

3. step 3：推送
```bash
docker push xxxxx:cypress 
```

### 更改gitlab-ci.yml
在gitlab-cli中添加stage
```yml
stages:
  - test

variables:
  CYPRESS_CACHE_FOLDER: "$CI_PROJECT_DIR/cache/Cypress"

# 缓存参考：https://gitlab.com/cypress-io/cypress-example-docker-gitlab/-/blob/master/.gitlab-ci.yml
cache:
  key:
    files:
      - package.json
  paths:
    - node_modules/
    - cache/Cypress

test:
  tags:
    - gpxrunner                                               # 使用tag为docker的runner触发构建
  stage: test
  image:
        name: xxx/:cypress
  script:
    # install dependencies
    - export GENERATE_SOURCEMAP=false
    - export SASS_BINARY_SITE=https://npm.taobao.org/mirrors/node-sass/
    - npm install --registry=https://registry.npm.taobao.org
    # start the server in the background
    - npm run start &
    # run Cypress tests
    - npm run cypress:run
```

### 效果
#### 单个测试结果

#### 整体测试结果

### 上传测试报告
Thanks for @chenzhouyi ，补全了生成测试报告及上传的流程。

1. Step 1: 安装测试报告生成插件`cypress-mochawesome-reporter`
```bash
npm i cypress-mochawesome-reporter
```

2. Step 2: 配置cypress.json，制定测试报告插件和报告生成路径
```json
{
  "reporter": "cypress-mochawesome-reporter", // 指定插件
  "reporterOptions": {
    "reportDir": "cypress/report", // 输出目录 
    "charts": true,
    "embeddedScreenshots": true, // 录制屏幕
    "saveJson": true, // 保存一份json数据
    "timestamp": "yyyy-mm-dd_HHMM",
    "reportTitle": "aladdin 前端自动测试报告",
    "reportPageTitle": "aladdin 前端自动测试报告",
    "inlineAssets": true // 这里配置将所有资源内嵌到单个 html 中
  }
}

```

3. Step 3: 上传报告
```js
const axios = require('axios')
const FormData = require('form-data')
const path = require('path')
const glob = require('glob-promise')
const { createReadStream, promises } = require('fs')

const { readFile } = promises

const API_URL = 'http://xxx'
const ORG = 'xxx'
const GROUP = 'xxx'
const PROJECT = 'xxx'
const REPO = 'xx-ui'
const FOCUS = 'integration'
const REPORT_DIR = './report' // 报告位置
const AUTH_USER = 'xxx'

const AUTH_KEY = process.env.AUTH_KEY
const AUTH_ENV_BASE_URL = 'https://xxx'

const getReportPath = async () => {
  const dirPath = path.resolve(__dirname, '..', REPORT_DIR)

  const [htmlPath] = await glob(path.resolve(dirPath, './index*.html'))
  const [jsonPath] = await glob(path.resolve(dirPath, './index*.json'))

  return {
    htmlPath,
    jsonPath,
  }
}

const getReportStatus = async (jsonPath) => {
  const { stats } = JSON.parse(await readFile(jsonPath, 'utf-8'))
  return stats
}

const getAuthToken = async () => {
  try {
    const {
      data: { token, tokenV1 },
    } = await axios.post(AUTH_ENV_BASE_URL, { ttl: 86400, user: AUTH_USER, key: AUTH_KEY })
    return {
      token,
      tokenV1,
    }
  } catch (e) {
    console.log(e.response.data)
  }
}

const getVersion = () => {
  return new Date().getTime()
}

const uploadReport = async () => {
  console.log('get token')
  const { htmlPath, jsonPath } = await getReportPath()
  const { token, tokenV1 } = await getAuthToken()
  const targetUrl = `${API_URL}/auto_test_reports:upload`
  const version = getVersion()

  const reportFile = createReadStream(htmlPath)
  const stat = await getReportStatus(jsonPath)
  // 处理数据
  const formData = new FormData()
  const headers = {
    ...formData.getHeaders(),
    'X-Auth-Token': tokenV1,
    'X-Auth-Project': PROJECT,
    'X-Access-Token': token,
  }

  formData.append('organisation', ORG)
  formData.append('group', GROUP)
  formData.append('repo', REPO)
  formData.append('focus', FOCUS)
  formData.append('version', version)
  formData.append('file', reportFile)
  formData.append('passed', stat.passes)
  formData.append('failed', stat.failures)
  formData.append('skipped', stat.skipped)

  console.log('start upload report')
  try { // 上传报告
    await axios.post(targetUrl, formData, {
      headers,
    })
    console.log('upload report success')
  } catch (e) {
    console.log('upload fail')
    console.log(e.response.data)
  }
}

;(async () => {
  await uploadReport()
})()

```

## reference
1. [cypress GitHub ci/cd](https://docs.cypress.io/guides/continuous-integration/gitlab-ci#Basic-Setup)

### Q&A