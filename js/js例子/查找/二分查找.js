function binarySearch(arr, n) {
  let low = 0;
  let hight = arr.length - 1;
  let result;
  while(low < hight) {
    const mid = Math.floor((low + hight) / 2);
    if (arr[mid] = n) {
      result = mid;
      break;
    } else if (arr[mid] < n) {
      low = mid + 1;
    } else {
      hight = mid - 1;
    }
  }
  return result;
}