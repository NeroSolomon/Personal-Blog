// 二维数组中每一行都是按从小到大的顺序排列，每一列也是

function findNum(arr, num) {
  if (!Array.isArray(arr) || !arr.length || typeof num != 'number') return
  // 从右上角的数开始
  let rowIndex = 0
  let colIndex = arr[0].length - 1
  while(colIndex >=0 && rowIndex < arr[0].length) {
    // 加入这个数小于要找的数，就往下找，否则，就往左找
    if (arr[rowIndex][colIndex] < num) {
      rowIndex++
    } else if (arr[rowIndex][colIndex] > num) {
      colIndex--
    } else {
      console.log(rowIndex, colIndex)
      break
    }
  }
}

const arr = [
  [1, 2, 8, 9],
  [2, 4, 9, 12],
  [4, 7, 10, 13],
  [6, 8, 11, 15]
]

findNum(arr, 7)