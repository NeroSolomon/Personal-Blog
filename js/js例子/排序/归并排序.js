function mergeSort(arr) {
  const len = arr.length;
  if (len < 2) return arr;
  const mid = Math.floor(len / 2);
  const left = arr.slice(0, mid);
  const right = arr.slice(mid)
  return merge(mergeSort(left), mergeSort(right));
}

function merge(left, right) {
  const result = [];

  while(left.length && right.length) {
    if (left[0] < right[0]) {
      result.push(left.shift());
    } else {
      result.push(right.shift())
    }
  }

  while(left.length) {
    result.push(left.shift());
  }

  while(right.length) {
    result.push(right.shift());
  }

  return result;
}

let arr = [1, 10, 6, 21, 8, 9, 5]
arr = mergeSort(arr);
console.log(arr);