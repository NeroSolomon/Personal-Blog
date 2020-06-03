const { fetchData, promiseResloveFetchData, promiseRejectFetchData } = require('../src/async');

// 回调的形式
test('the data is peanut butter: callback', done => {
  function callback(data) {
    try {
      expect(data).toBe('peanut butter');
      // 让jest在done执行后才结束测试
      done();
    } catch (error) {
      // 假如测试不过会被catch
      done(error);
    }
  }

  fetchData(callback);
});

// promise
// reslove
test('the data is peanut butter: reslove', () => {
  return promiseResloveFetchData().then(data => {
    expect(data).toBe('peanut butter');
  });
});

// reject
test('the data is peanut butter: reject', () => {
  return promiseRejectFetchData().catch(e => {
    expect(e).toBe('peanut butter');
  });
});

// async/await
// reslove
test('the data is peanut butter: reslove async/await', async () => {
  const data = await promiseResloveFetchData();
  expect(data).toBe('peanut butter');
});

// reject
test('the data is peanut butter: reslove async/await', async () => {
  try {
    await promiseRejectFetchData();
  } catch (e) {
    expect(e).toMatch('peanut butter');
  }
});