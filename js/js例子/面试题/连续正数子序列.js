function findContinuousSequence(sum) {
  let start = 1;
  let end = 2;
  let sumTemp = 0;
  let array = [1, 2];
  let ans = [];
  if (sum < 3) return [];
  while (start <= Math.ceil(sum / 2)) {
    sumTemp = ((start + end) * (end - start + 1)) / 2;
    if (sumTemp === sum) {
      ans.push(array.concat());
      array.shift();
      start++;
      end++;
      array.push(end);
    } else if (sumTemp > sum) {
      array.shift();
      start++;
    } else {
      end++;
      array.push(end);
    }
  }
  return ans;
}

const res = findContinuousSequence(3);

console.log(res);