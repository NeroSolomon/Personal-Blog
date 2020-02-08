function findNum(arr) {
  if (!Array.isArray(arr)) return
  const newArr = []
  for (let i = 0; i < arr.length; i++) {
    if (arr.indexOf(arr[i]) != arr.lastIndexOf(arr[i]) && newArr.indexOf(arr[i]) == -1) {
      newArr.push(arr[i])
    }
  }

  return newArr
}

console.log(findNum([2, 3, 1, 0, 2, 5, 3, 4, 4]))