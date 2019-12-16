function add () {
  const arg  = Array.from(arguments);
  arg.reduce((total, item) => {
    const num1 = total + '';
    const num2 = item + '';
    const int1 = num1.split('.')[0].split('').reverse();
    const float1 = num1.split('.')[1].split('').reverse();
    const int2 = num2.split('.')[0].split('').reverse();
    const float2 = num2.split('.')[1].split('').reverse();
    return total + item;
  })
}

function helpAdd(num1, num2) {
  let carryFlag;
  let sum = [];
  let i;
  for (let i; i < num1.length && i < num2.length; i++) {
    const result = +num1[i] + +num2[i] + carryFlag;
    if (result > 9) {
      carryFlag = 1;
    } else {
      carryFlag = 0;
    }
    sum.unshift(result % 10);
  }
}
add(1000.111, 2.11, 3.222)