function bubbleSort(arr) {
  for (let i = 0; i < arr.length - 1; i++) {
    // 记得 - i， 因为外层每一轮循环都将一个大数放到数组后面
    for (let j = 0; j < arr.length - 1 - i; j++) {
      let temp = arr[j];
      if (arr[j] > arr[j + 1]) {
        arr[j] = arr[j + 1];
        arr[j + 1] = temp;
      }
    }
  }
}

const arr = [1, 10, 6, 21, 8, 9, 5]
bubbleSort(arr);
console.log(arr);

// 时间复杂度：O(n²)