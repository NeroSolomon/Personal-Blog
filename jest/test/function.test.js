function forEach(items, callback) {
  for (let index = 0; index < items.length; index++) {
    callback(items[index]);
  }
}

const mockCallback = jest.fn(x => 42 + x);
forEach([0, 1], mockCallback);

// 此mock函数被调用了两次
test('function call twice', () => {
  expect(mockCallback.mock.calls.length).toBe(2);
})

// 第一次调用函数的第一个参数是0
test('function\'s first argument is 0', () => {
  expect(mockCallback.mock.calls[0][0]).toBe(0);
})

// 第二次调用函数的第一个参数是1
test('function\'s first argument is 1', () => {
  expect(mockCallback.mock.calls[1][0]).toBe(1);
})

// 第一次函数调用的返回值是42
test('funciton first call return 42', () => {
  expect(mockCallback.mock.results[0].value).toBe(42);
})
