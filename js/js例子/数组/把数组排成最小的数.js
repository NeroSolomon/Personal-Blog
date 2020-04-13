function printMinNumber(numbers) {
  numbers.sort(compare);
  let result = "";
  for (let i = 0; i < numbers.length; i++) {
    result +=numbers[i]
  }
  return result;
}

function compare(a, b) {
  let str1 = a + "" + b;
  let str2 = b + "" + a;
  for (let i = 0; i < str1.length; i++) {
    if (str1.charAt(i) > str2.charAt(i)) {
      return 1;
    }
    if (str1.charAt(i) < str2.charAt(i)) {
      return -1;
    }
  }
  return 1;
}