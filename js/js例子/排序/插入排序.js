function insertionSort(arr) {
  let preIndex, current;
  for (let i = 1; i < arr.length; i++) {
    preIndex = i - 1;
    current = arr[i]
    // 大于current的往后移，前面空出一个位置放current
    while(current < arr[preIndex] && preIndex >= 0) {
      arr[preIndex + 1] = arr[preIndex]
      preIndex--;
    }
    arr[preIndex + 1] = current
  }
}


const arr = [1, 10, 6, 21, 8, 9, 5]
insertionSort(arr);
console.log(arr);