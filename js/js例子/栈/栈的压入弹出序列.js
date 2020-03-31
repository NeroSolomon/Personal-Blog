function isPopOrder(pushV, popV) {
  let stack = [];
  let idx = 0;
  for (var i = 0; i < pushV.length; i++) {
    stack.push(pushV[i]);
    while (stack.length && stack[stack.length - 1] == popV[idx]) {
      stack.pop();
      idx++;
    }
  }
  return stack.length == 0;
}

const pushV = [1, 2, 3, 4, 5];
const popV = [4, 5, 3, 2, 1];

console.log(isPopOrder(pushV, popV))