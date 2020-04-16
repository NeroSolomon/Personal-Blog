function getMaxValue(values, rows, cols) {
  if (values == null || rows < 0 || cols <= 0) {
    return 0;
  }
  let maxValues = [];
  for (let i = 0; i < rows; ++i) {
    for(let j = 0; j < cols; ++j) {
      let left = 0;
      let up = 0;
      if (i > 0) {
        up = maxValues[j];
      }
      if (j > 0) {
        left = maxValues[j - 1];
      }
      maxValues[j] = Math.max(left, up) + values[i * cols + j];
    }
  }
  let maxValue = maxValues[cols - 1];
  return maxValue;
}