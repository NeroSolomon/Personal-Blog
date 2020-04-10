function moreThanHalfNum(numbers) {
  const len = numbers.length;
  if (len == 0) return 0;
  let num = numbers[0], count = 1;
  // 找出最大的数
  for (let i = 1; i < len; i++) {
    if (num == numbers[i]) {
      count++;
    } else {
      count--;
    }
    if (count == 0) {
      num = numbers[i];
      count = 1;
    }
  }
  // 统计最大的数出现的次数
  count = 0;
  for (let i = 0; i < len; i++) {
    if (numbers[i] == num) count++;
  }

  // 如果超过一半
  if (count * 2 > len) return num;
  return 0;
}