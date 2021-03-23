function fetchData(callback) {
  const data = 'peanut butter';
  callback && callback(data);
}

function promiseResloveFetchData() {
  return new Promise((reslove, reject) => {
    reslove('peanut butter');
  })
}

function promiseRejectFetchData() {
  return new Promise((reslove, reject) => {
    reject('peanut butter');
  })
}


module.exports = { fetchData, promiseResloveFetchData, promiseRejectFetchData };