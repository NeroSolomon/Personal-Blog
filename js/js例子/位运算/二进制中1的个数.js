function find(num) {
  const str = num.toString(2);
  let sum = 0;
  for (let i = 0; i < str.length; i++) {
    if (str[i] == 1) {
      sum++;
    }
  }
  return sum;
}

console.log(find(2));
