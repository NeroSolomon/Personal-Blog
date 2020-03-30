function adjust(arr) {
  const cardinals = arr.filter(item => item % 2 > 0)
  const evens = arr.filter(item => item % 2 == 0)
  return cardinals.concat(evens);
}

console.log(adjust([1,2,3,4,5,6,7,8]));