// 在矩阵中判断是否出现一条和字符串一样的路径

function hasPath(matrix, rows, cols, str) {
  if (!matrix || rows < 0 || cols < 0 || !str) return false;
  let visited = [];
  let strIndex = 0;

  // 找到第一个匹配的位置
  for (let row = 0; row < rows; row++) {
    for (let col = 0; col < cols; col++) {
      if (hasPathCore(matrix, row, col, rows, cols, str, strIndex, visited)) {
        return true;
      }
    }
  }

  return false;
}

function hasPathCore(matrix, row, col, rows, cols, str, strIndex, visited) {
  if (strIndex == str.length) return true;
  let hasPath = false;
  if (row < rows && col < cols && matrix[row * cols + col] == strIndex[strIndex] && !visited[row * cols + col]) {
    strIndex++;
    visited[row * cols + col] = true;

    hasPath = hasPath(matrix, row + 1, col, rows, cols, str, strIndex, visited) ||
      hasPath(matrix, row - 1, col, rows, cols, str, strIndex, visited) ||
      hasPath(matrix, row, col + 1, rows, cols, str, strIndex, visited) ||
      hasPath(matrix, row, col - 1, rows, cols, str, strIndex, visited)

    if (!hasPath) {
      strIndex--;
      visited[row * cols + col] = false;
    }
  }

  return hasPath;
}