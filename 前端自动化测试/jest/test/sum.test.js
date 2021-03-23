const { sum, rNull } = require('../src/sum');

// expect接受一个待测试脚本中的语句，返回一个“期望的对象”


// toBe匹配器
test('adds 1 + 2 to equal 3', () => {
  expect(sum(1, 2)).toBe(3);
});

// toEqual 检查对象全等
test('Obect assignment', () => {
  const data = { one: 1 };
  data['two'] = 2;
  expect(data).toEqual({ one: 1, two: 2 });
});

// not toBe 相反匹配
test('adds 1 + 2 not equal 3', () => {
  expect(sum(1, 2)).not.toBe(0);
});



// 匹配undefined、null、false

// 匹配null
test('is Null', () => {
  expect(rNull()).toBeNull();
})

// 匹配undefined
test('is Undefined', () => {
  let a;
  expect(a).toBeUndefined();
})

// 匹配非undefined
test('is not undefined', () => {
  expect(rNull()).toBeDefined();
})

// 匹配任何 if 语句为真
test('isTruthy', () => {
  expect( 0 == 0).toBeTruthy();
})

// 匹配任何 if 语句为假
test('isFalsy', () => {
  expect( 0 != 0).toBeFalsy();
})


// 数字比较

// 大于
test('4 greater than 3', () => {
  const value = 4;
  expect(value).toBeGreaterThan(3);
})

// 大于等于
test('4 is greater or equal than 4', () => {
  const value = 4;
  expect(value).toBeGreaterThanOrEqual(4);
})

// 小于
test('3 is less than 4', () => {
  const value = 3;
  expect(value).toBeLessThan(4);
})

// 小于等于
test('4 is less or equal than 4', () => {
  const value = 3;
  expect(value).toBeLessThan(4);
})

// 字符串

// 不匹配
test('there is no i in team', () => {
  expect('team').not.toMatch(/i/);
})

// 匹配
test('there is a stop in Christoph', () => {
  expect('Christoph').toMatch(/stop/);
})


// 数组

// 包含某个特定项
test('the shopping list has beer on it', () => {
  const shoppingList = ['beer', 'apple'];
  expect(shoppingList).toContain('beer');
})

// 抛出错误
// test('throw error', () => {
//   function compileAndroidCode() {
//     throw new Error('you are using the wrong JDK');
//   }

//   expect(compileAndroidCode()).throw(Error);
// })