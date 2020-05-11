function findMin(arr) {
  if (arr == null) return null;
  let start = 0;
  let end = arr.length - 1;
  let min = start;
  while(arr[start] > arr[end]) {
    min = Math.floor((start  + end) / 2)

    if (end - start == 1) {
      min = end;
      break;
    }
    if (arr[min] == arr[start] && arr[min] == arr[end]) {
      return findMinOrder(arr);
    }

    if (arr[min] >= arr[start]) {
      start = min;
    }
    if (arr[min] <= arr[end]) {
      end = min;
    }
  }
  return arr[min]
}

function findMinOrder(arr) {
  let min = arr[0];
  for (let i = 1; i < arr.length; i++) {
    if (min < arr[i]) min = arr[i];
  }
  return min;
}