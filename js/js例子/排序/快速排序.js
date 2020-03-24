function quickSort(arr, left = 0, right = arr.length - 1) {
  if (left < right) {
    const refer = partition(arr, left, right);
    quickSort(arr, left, refer - 1);
    quickSort(arr, refer + 1, right);
  }
}

function partition(arr, left, right) {     // 分区操作
    const refer = left;
    let index = left + 1;
    for (let i = index; i < arr.length; i++) {
      if (arr[i] < arr[refer]) {
        swap(arr, i, index);
        // 发生交换后指向下一个数
        index++;
      }
    }
    // 和最后一个小于refer的数交换位置
    swap(arr, refer, index - 1);

    // 返回refer最终确定的位置
    return index - 1;
}

function swap(arr, i, j) {
    var temp = arr[i];
    arr[i] = arr[j];
    arr[j] = temp;
}

let arr = [8, 10, 6, 21, 8, 9, 5]
quickSort(arr);
console.log(arr);