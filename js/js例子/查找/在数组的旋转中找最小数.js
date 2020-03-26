function min(arr) {
  if (!arr.length) return;
  let index1 = 0;
  let index2 = length - 1;
  let indexMid = index1;
  while(arr[index1] >= arr[index2]) {
    if (index2 - index1 == 1) {
      indexMid = index2;
      break;
    }

    indexMid = Math.floor((index1 + index2) / 2);
    if (numbers[indexMid] >= numbers[index1]) {
      index1 = indexMid;
    } else if (numbers[indexMid] <= numbers[index2]) {
      index2 = indexMid
    }
  }
  return numbers[indexMid];
}

