function find(index) {
  if (index < 0) return -1;
  let digits = 1;
  while(true) {
    let numbers = countOfIntegers(digits);
    if (index < numbers * digits)
      return digitAtIndex(index, digits);
    index -= digits * numbers;
    digits++;
  }
  return -1
}

// 计算某个数位下的数字总数
function countOfIntegers(digits) {
  if (digits == 1) return 10;
  let count  = Math.pow(10, digits - 1);
  return (9 * count);
}

// 找出结果
function digitAtIndex(index, digits) {
  let number = beginNumber(digits) + Math.floor(index / digits); // 370
  let indexFromRight = digits - index % digits; // 3 - 1 = 2
  for (let i = 1; i < indexFromRight; ++i) number = Math.floor(number / 10);
  return number % 10;
}

// 找出当前位数的第一个数
function beginNumber(digits) {
   if (digits == 1) return 0;
   return Math.pow(10, digits - 1);
}

/**
  例如，序列的第1001位
  序列的前10位是0～9这10个只有一位的数字。显然第1001位在这10个数字之后，因此这10个数字可以直接跳过。我们再从后面紧跟这的序列中找第991（991 = 1001 - 10）位的数字。
  接下来180位数字是90个10～99位的两位数。由于991 > 180 (180 = 90 * 2) 所以991位在所有的两位数之后。我们再跳过90个两位数，继续从后面找881（881 = 991 - 180）位。
  接下来2700位是900个100～999的三位数。由于881<2700，所以第811位是某个三位数中的一位。由于881 = 270*3 + 1，这意味着第811位是从100开始的第270个数字即370的中间一位，也就是7
*/