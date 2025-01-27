## 找到type、enum、interface定义范围
```ts
const ts = require('typescript');

function findTypeDefinitionLines(filePath: string) {
  // 创建编译器选项
  const compilerOptions: any = {
    jsx: ts.JsxEmit.React,
    target: ts.ScriptTarget.ESNext,
  };

  // 创建程序
  const program = ts.createProgram([filePath], compilerOptions);
  const sourceFile = program.getSourceFile(filePath);

  if (!sourceFile) {
    throw new Error('Could not find source file');
  }

  const results: Array<{
    type: string;
    name: string;
    startLine: number;
    endLine: number;
    text: string;
  }> = [];

  function visit(node: any) {
    // 获取开始和结束的行号
    const start = sourceFile.getLineAndCharacterOfPosition(node.getStart(sourceFile));
    const end = sourceFile.getLineAndCharacterOfPosition(node.getEnd());
    const nodeText = node.getText(sourceFile);

    // 检查枚举声明
    if (ts.isEnumDeclaration(node)) {
      results.push({
        type: 'enum',
        name: node.name.text,
        startLine: start.line + 1,
        endLine: end.line + 1,
        text: nodeText
      });
    }
    // 检查类型别名声明
    else if (ts.isTypeAliasDeclaration(node)) {
      results.push({
        type: 'type',
        name: node.name.text,
        startLine: start.line + 1,
        endLine: end.line + 1,
        text: nodeText
      });
    }
    // 检查接口声明
    else if (ts.isInterfaceDeclaration(node)) {
      results.push({
        type: 'interface',
        name: node.name.text,
        startLine: start.line + 1,
        endLine: end.line + 1,
        text: nodeText
      });
    }

    ts.forEachChild(node, visit);
  }

  visit(sourceFile);
  return results;
}

const res = findTypeDefinitionLines('./src/App.tsx');
console.log(res);
```