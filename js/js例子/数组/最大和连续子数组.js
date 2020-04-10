function FindGreatestSumOfSubArray(array) {
  // write code here
  if (array.length <= 0) return 0;
  let max = array[0];
  let pre = array[0];
  for (let i = 1; i < array.length; i++) {
    if (pre < 0) pre = 0;
    max = Math.max(max, pre+array[i]);
    pre = pre+array[i];
  }
  return max;
}

const array = [6,-3,-2,7,-15,1,2,2]
const max = FindGreatestSumOfSubArray(array);
console.log(max);