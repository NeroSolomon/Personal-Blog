## 背景
为了方便QA同学管理用例，可以用xlsx管理用例

通过固定格式书写xlsx图表，再通过nodejs将xlsx内容转换成json文件，再转换成测试脚本。

## 方案

### 安装npm包
```bash
yarn add node-xlsx -D
```

### 写插件
```js
// read-xlsx.js
const fs = require('fs');
const XLSX = require('xlsx');

const read = ({file, sheet}) => {
  const sheet2JSONOpts = {
    defval: ''
  };
  const buf = fs.readFileSync(file);
  const workbook = XLSX.read(buf, {type: 'buffer'});
  console.log(workbook);
  const rows = XLSX.utils.sheet_to_json(workbook.Sheets[sheet], sheet2JSONOpts);
  return rows;
};

module.exports = {
  read,
}
```

### 注册插件
```js
// plugins/index.js
const xlsx = require('node-xlsx').default;
const fs = require('fs');
const readXlsx = require('./read-xlsx');
/**
 * @type {Cypress.PluginConfig}
 */
module.exports = (on, config) => {
  // `on` is used to hook into various events Cypress emits
  // `config` is the resolved Cypress config
  on('task', {
    parseXlsx({ filePath }) {
      return new Promise((resolve, reject) => {
        try {
          const jsonData = xlsx.parse(fs.readFileSync(filePath));
          resolve(jsonData);
        } catch (e) {
          reject(e);
        }
      });
    },
    readXlsx: readXlsx.read,
  });
  return config;
};
```

### 新建方法
```js
// support/commands.js
let tmpDict = {};
Cypress.Commands.add('setTmpDict', (tmp) => {
  let tmpData;
  if (tmp.constructor === Object) {
    tmpData = tmp;
  } else {
    tmpData = JSON.parse(tmp);
  }
  Object.assign(tmpDict, tmpDict, tmpData);
});

let rowsLength;
Cypress.Commands.add('xlsxToJson', (inputFile) => {
  const nodeValues = {};
  // 替换节点为真实值
  cy.task('readXlsx', {file: 'cypress/fixtures/data.xlsx', sheet: 'Nodes'}).then((rows) => {
    // eslint-disable-next-line guard-for-in
    for (const data in rows) {
      nodeValues[rows[data].nodeId] = rows[data].nodeValue;
    }
  });
  cy.task('readXlsx', {file: 'cypress/fixtures/data.xlsx', sheet: 'Sheet'}).then((rows) => {
    cy.log(nodeValues);
    // console.log(typeof rows);
    // eslint-disable-next-line no-undef
    rowsLength = rows.length;
    let dataJson = {};
    let caseId = '';

    // eslint-disable-next-line guard-for-in
    for (const data in rows) {
      if (rows[data].isRun === 'yes' && rows[data].handleMethod === 'setTmpDict') {
        let dic;
        dic = JSON.parse(rows[data].handleValue);
        for (var key in dic) {
          if (dic[key][0] === '$') {
            if (dic[key].slice(1) === 'strNum') {
              dic[key] = (+new Date()).toString().slice(8);
            }
          }
          console.log(dic[key]);
        }
        Object.assign(tmpDict, tmpDict, dic);
      } else if (rows[data].isRun === 'yes' && rows[data].handleMethod === 'toSheet') {
        let sheetName = rows[data].handleValue;
        cy.task('readXlsx', {file: 'cypress/fixtures/data.xlsx', sheet: rows[data].handleValue}).then((rows) => {
          cy.log(data);
          let dataJsonTmp = {};
          let caseId = '';
          for (const data in rows) {
            if (rows[data].isRun === 'yes' && rows[data].handleMethod === 'setTmpDict') {
              let dic;
              dic = JSON.parse(rows[data].handleValue);
              for (var key in dic) {
                if (dic[key][0] === '$') {
                  if (dic[key].slice(1) === 'strNum') {
                    dic[key] = (+new Date()).toString().slice(8);
                  }
                }
                console.log(dic[key]);
              }
              Object.assign(tmpDict, tmpDict, dic);
            } else {
              let tmp;
              let handleActionOption;
              // handleValue处理
              if (rows[data].handleValue.constructor == String) {
                if (rows[data].handleValue[0] === '{' && rows[data].handleValue[rows[data].handleValue.length - 1] === '}' && rows[data].handleValue[1] !== '{') {
                  // 处理如{"test":"body{{tmpData}}"}
                  // console.log(rows[data].handleValue);
                  // 处理json中的变量
                  if (rows[data].handleValue.indexOf('{{') != -1) {
                    let tmpArr;
                    tmpArr = rows[data].handleValue.split('{{');
                    let resultArry = tmpArr[0];
                    for (var i = 1; i < tmpArr.length; i++) {
                      let tmpData;
                      tmpData = tmpArr[i].split('}}');
                      resultArry += tmpDict[tmpData[0]] + tmpData[1];
                    }
                    // console.log(resultArry);
                    rows[data].handleValue = resultArry;
                  }
                  rows[data].handleValue = JSON.parse(rows[data].handleValue);
                } else if (rows[data].handleValue.indexOf('{{') != -1) {
                  let tmpArr;
                  tmpArr = rows[data].handleValue.split('{{');
                  let resultArry = tmpArr[0];
                  for (var i = 1; i < tmpArr.length; i++) {
                    let tmpData;
                    tmpData = tmpArr[i].split('}}');
                    resultArry += tmpDict[tmpData[0]] + tmpData[1];
                  }
                  // console.log(resultArry);
                  rows[data].handleValue = resultArry;
                } else if (rows[data].handleValue[0] === '@') {
                  // console.log(rows[data].handleValue);
                  // 替换node的实际值
                  rows[data].handleValue = nodeValues[rows[data].handleValue.slice(1)];
                  // 替换的实际值中有引用,处理json中的变量,与上面的代码重复，暂不优化
                  if (rows[data].handleValue[0] === '{' && rows[data].handleValue[rows[data].handleValue.length - 1] === '}' && rows[data].handleValue[1] !== '{') {
                    // 处理json中的变量
                    if (rows[data].handleValue.indexOf('{{') != -1) {
                      let tmpArr;
                      tmpArr = rows[data].handleValue.split('{{');
                      let resultArry = tmpArr[0];
                      for (var i = 1; i < tmpArr.length; i++) {
                        let tmpData;
                        tmpData = tmpArr[i].split('}}');
                        resultArry += tmpDict[tmpData[0]] + tmpData[1];
                      }
                      // console.log(resultArry);
                      rows[data].handleValue = resultArry;
                    }
                    // console.log(rows[data].handleValue);
                    rows[data].handleValue = JSON.parse(rows[data].handleValue);
                  }
                }
              }
              // handleAction处理变量，进行替换
              if (rows[data].handleAction.indexOf('{{') != -1) {
                let tmpArr;
                tmpArr = rows[data].handleAction.split('{{');
                let resultArry = tmpArr[0];
                for (var i = 1; i < tmpArr.length; i++) {
                  let tmpData;
                  tmpData = tmpArr[i].split('}}');
                  resultArry += tmpDict[tmpData[0]] + tmpData[1];
                }
                // console.log(resultArry);
                rows[data].handleAction = resultArry;
              }
              // handleActionOption处理
              if (rows[data].handleActionOption !== '') {
                if (rows[data].handleActionOption[0] === '{') {
                  rows[data].handleActionOption = JSON.parse(rows[data].handleActionOption);
                } else rows[data].handleActionOption = rows[data].handleActionOption;
              } else rows[data].handleActionOption = {};
              // exceptNode处理
              if (rows[data].exceptNode[0] === '@') {
                // 替换node的实际值
                rows[data].exceptNode = nodeValues[rows[data].exceptNode.slice(1)];
              }
              // exceptResult处理
              if (rows[data].exceptResult.indexOf('{{') != -1) {
                // {{tmpData}}
                let tmpArr;
                tmpArr = rows[data].exceptResult.split('{{');
                let tmpArr1;
                tmpArr1 = tmpArr[1].split('}}');
                rows[data].exceptResult = tmpArr[0] + tmpDict[tmpArr1[0]] + tmpArr1[1];
              }
              tmp = {
                isRun: rows[data].isRun,
                handleMethod: rows[data].handleMethod,
                handleValue: rows[data].handleValue,
                handleAction: rows[data].handleAction,
                handleActionOption: rows[data].handleActionOption,
                exceptMethod: rows[data].exceptMethod,
                exceptNode: rows[data].exceptNode,
                exceptResult: rows[data].exceptResult,
                desc: rows[data].desc,
              };
              // console.log(tmp);
              if (rows[data].caseId !== '') {
                caseId = sheetName + '>' + rows[data].caseId;
              }
              if (caseId in dataJsonTmp) {
                dataJsonTmp[caseId].push(tmp);
              } else {
                dataJsonTmp[caseId] = [tmp];
              }
            }
          }
          // console.log(dataJsonTmp);
          Object.assign(dataJson, dataJson, dataJsonTmp);
          console.log(dataJson);
          //  cy.task是异步任务,js 是单线程的 意思就是同时只能执行一个任务,写在.then()里的是微任务，js的执行顺序是 主程序任务 -> 异步任务,会把主程序任务都执行完再去执行异步任务
          const arrayData = [];
          for (const item in dataJson) {
            arrayData.push(
              {
                caseId: item,
                step: dataJson[item],
              },
            );
          }
          cy.writeFile('cypress/fixtures/xlsxData.json', arrayData);
        });
      } else if (rows[data].handleMethod !== 'toSheet') {
        let tmp;
        let handleActionOption;
        // handleValue处理
        if (rows[data].handleValue.constructor == String) {
          if (rows[data].handleValue[0] === '{' && rows[data].handleValue[rows[data].handleValue.length - 1] === '}' && rows[data].handleValue[1] !== '{') {
            // 处理如{"test":"body{{tmpData}}"}
            // console.log(rows[data].handleValue);
            // 处理json中的变量
            if (rows[data].handleValue.indexOf('{{') != -1) {
              let tmpArr;
              tmpArr = rows[data].handleValue.split('{{');
              let resultArry = tmpArr[0];
              for (var i = 1; i < tmpArr.length; i++) {
                let tmpData;
                tmpData = tmpArr[i].split('}}');
                resultArry += tmpDict[tmpData[0]] + tmpData[1];
              }
              // console.log(resultArry);
              rows[data].handleValue = resultArry;
            }
            rows[data].handleValue = JSON.parse(rows[data].handleValue);
          } else if (rows[data].handleValue.indexOf('{{') != -1) {
            let tmpArr;
            tmpArr = rows[data].handleValue.split('{{');
            let resultArry = tmpArr[0];
            for (var i = 1; i < tmpArr.length; i++) {
              let tmpData;
              tmpData = tmpArr[i].split('}}');
              resultArry += tmpDict[tmpData[0]] + tmpData[1];
            }
            // console.log(resultArry);
            rows[data].handleValue = resultArry;
          } else if (rows[data].handleValue[0] === '@') {
            // console.log(rows[data].handleValue);
            // 替换node的实际值
            rows[data].handleValue = nodeValues[rows[data].handleValue.slice(1)];
            // 替换的实际值中有引用,处理json中的变量,与上面的代码重复，暂不优化
            if (rows[data].handleValue[0] === '{' && rows[data].handleValue[rows[data].handleValue.length - 1] === '}' && rows[data].handleValue[1] !== '{') {
              // 处理json中的变量
              if (rows[data].handleValue.indexOf('{{') != -1) {
                let tmpArr;
                tmpArr = rows[data].handleValue.split('{{');
                let resultArry = tmpArr[0];
                for (var i = 1; i < tmpArr.length; i++) {
                  let tmpData;
                  tmpData = tmpArr[i].split('}}');
                  resultArry += tmpDict[tmpData[0]] + tmpData[1];
                }
                // console.log(resultArry);
                rows[data].handleValue = resultArry;
              }
              console.log(rows[data].handleValue);
              rows[data].handleValue = JSON.parse(rows[data].handleValue);
            }
          }
        }
        // handleAction处理变量，进行替换
        if (rows[data].handleAction.indexOf('{{') != -1) {
          let tmpArr;
          tmpArr = rows[data].handleAction.split('{{');
          let resultArry = tmpArr[0];
          for (var i = 1; i < tmpArr.length; i++) {
            let tmpData;
            tmpData = tmpArr[i].split('}}');
            resultArry += tmpDict[tmpData[0]] + tmpData[1];
          }
          // console.log(resultArry);
          rows[data].handleAction = resultArry;
        }
        // handleActionOption处理
        if (rows[data].handleActionOption !== '') {
          if (rows[data].handleActionOption[0] === '{') {
            rows[data].handleActionOption = JSON.parse(rows[data].handleActionOption);
          } else rows[data].handleActionOption = rows[data].handleActionOption;
        } else rows[data].handleActionOption = {};
        // exceptNode处理
        if (rows[data].exceptNode[0] === '@') {
          // 替换node的实际值
          rows[data].exceptNode = nodeValues[rows[data].exceptNode.slice(1)];
        }
        // exceptResult处理
        if (rows[data].exceptResult.indexOf('{{') != -1) {
          // {{tmpData}}
          let tmpArr;
          tmpArr = rows[data].exceptResult.split('{{');
          let tmpArr1;
          tmpArr1 = tmpArr[1].split('}}');
          rows[data].exceptResult = tmpArr[0] + tmpDict[tmpArr1[0]] + tmpArr1[1];
        }
        tmp = {
          isRun: rows[data].isRun,
          handleMethod: rows[data].handleMethod,
          handleValue: rows[data].handleValue,
          handleAction: rows[data].handleAction,
          handleActionOption: rows[data].handleActionOption,
          exceptMethod: rows[data].exceptMethod,
          exceptNode: rows[data].exceptNode,
          exceptResult: rows[data].exceptResult,
          desc: rows[data].desc,
        };
        // console.log(tmp);
        if (rows[data].caseId !== '') {
          // eslint-disable-next-line no-const-assign
          caseId = rows[data].caseId;
        }
        if (caseId in dataJson) {
          dataJson[caseId].push(tmp);
        } else {
          dataJson[caseId] = [tmp];
        }
        if (data == rowsLength - 1) {
          const arrayData = [];
          for (const item in dataJson) {
            arrayData.push(
              {
                caseId: item,
                step: dataJson[item],
              },
            );
          }
          cy.writeFile('cypress/fixtures/xlsxData.json', arrayData);
        }
      }
    }
  });
});
```

### 调用方法
```js
// integration/xlsx2Json.js
const AUTH_PROJECT = Cypress.env('AUTH_PROJECT');
const CODE = (+new Date()).toString().slice(8);

const tmpDict = {};
describe('The Home Page', () => {
  it('xlsxToJson', () => {
    tmpDict['code'] = CODE;
    tmpDict['AUTH_PROJECT'] = AUTH_PROJECT;
    cy.setTmpDict(tmpDict);
    cy.xlsxToJson();
    // cy.log(tmpDict);
  });
})
```

### 执行测试
```js
import caseData from '../fixtures/xlsxData.json';
import {bool} from "prop-types";

describe('Start ', function () {
  for (const cdata in caseData) {
    //{ retries: 1, openMode: 1 }, 去掉重试，因为测试有上下文，重试意义不大
    it(caseData[cdata].caseId, function () {
      let testData;
      testData = caseData[cdata].step;
      for (const data in testData) {
        if (testData[data].isRun === 'yes' || testData[data].isRun === '') {
          if (testData[data].desc !== '') {
            cy.log(testData[data].desc);
          }
          // cy[log](testData[data].handle_value);
          if (testData[data].handleAction) {
            // 命令过长，如cy.get().first().type("")
            if (testData[data].handleAction[0] === '.') {
              const targetObj = testData[data].handleAction;
              let objArry = targetObj.split(')');
              objArry.pop();
              // cy.log(objArry);
              let cyCode = [];
              let tmp;
              for (const arry in objArry) {
                let tmp = objArry[arry];
                let tmpArry = tmp.split('(');
                // 判断（）中的类型进行转换
                if (tmpArry[1][0] === '"' || tmpArry[1][0] === '\'') {
                  // 判断长度，如出现.type("分区",{"force": true})，里面多个参数,暂时只支持（str，obj)格式，后续需要再扩展
                  if (tmpArry[1].split(',').length > 1) {
                    let valueArray;
                    valueArray = tmpArry[1].split(',');
                    tmpArry[1] = [valueArray[0].split('"')[1], JSON.parse(valueArray[1])];
                  } else {
                    tmpArry[1] = tmpArry[1].split('"')[1];
                  }
                  console.log(tmpArry[1]);
                } else if (tmpArry[1] == 'true' || tmpArry[1] == 'false') {
                  tmpArry[1] = bool(tmpArry[1]);
                } else if (tmpArry[1][0] === '{') {
                  tmpArry[1] = JSON.parse(tmpArry[1]);
                } else {
                  tmpArry[1] = parseInt(tmpArry[1]);
                }
                cyCode.push([tmpArry[0].slice(1), tmpArry[1]]);
              }
              cy.log(cyCode);
              cy[testData[data].handleMethod](testData[data].handleValue).as('testData');
              for (const attr in cyCode) {
                if (attr == cyCode.length - 1) {
                  console.log(cyCode[attr][0], cyCode[attr][1]);
                  if (cyCode[attr][0] === 'should') {
                    cy.get('@testData').should('contain.text', cyCode[attr[0]][1]);
                  } else {
                    if (cyCode[attr[0]][1]) {
                      console.log(cyCode[attr[0]][1].constructor);
                      // 如出现.type("分区",{"force": true})，里面多个参数,暂时只支持（str，obj)格式，后续需要再扩展
                      if (cyCode[attr[0]][1].constructor === ['test', {}].constructor) {
                        console.log(cyCode[attr[0]][0]);
                        console.log(cyCode[attr[0]][1][0]);
                        console.log(cyCode[attr[0]][1][1]);
                        cy.get('@testData')[cyCode[attr[0]][0]](cyCode[attr[0]][1][0], cyCode[attr[0]][1][1]).as('testData');
                      } else {
                        cy.get('@testData')[cyCode[attr[0]][0]](cyCode[attr[0]][1]).as('testData');
                      }
                    } else {
                      cy.get('@testData')[cyCode[attr[0]][0]]().as('testData');
                    }
                  }
                } else {
                  console.log(cyCode[attr][0], cyCode[attr][1]);
                  if (cyCode[attr[0]][1]) {
                    cy.get('@testData')[cyCode[attr[0]][0]](cyCode[attr[0]][1]).as('testData');
                  } else {
                    cy.get('@testData')[cyCode[attr[0]][0]]().as('testData');
                  }
                }
              }
            } else {
              cy[testData[data].handleMethod](testData[data].handleValue)[testData[data].handleAction](testData[data].handleActionOption);
            }
            if (testData[data].exceptMethod) {
              cy[testData[data].exceptMethod](testData[data].exceptNode).should('contain.text', testData[data].exceptResult);
            }
          } else {
            if (testData[data].handleMethod === 'wait') {
              cy.wait(parseInt(testData[data].handleValue, 10));
            } else if (testData[data].handleMethod) {
              cy[testData[data].handleMethod](testData[data].handleValue);
            }
            if (testData[data].exceptMethod) {
              cy[testData[data].exceptMethod](testData[data].exceptNode).should('contain.text', testData[data].exceptResult);
            }
          }
        }
      }
    });
  }
  Cypress.on('uncaught:exception', (err, exceptionIgnore) => {
    // returning false here prevents Cypress from
    // failing the test
    console.log('uncaught:exception!');
    return false;
  });
});
```