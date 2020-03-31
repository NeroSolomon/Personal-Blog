function printMatixClockWisely(num, cols, rows) {
  if (numbers == null || cols <= 0 || rows <= 0) return
  let start = 0;
  while (cols > start * 2 && rows > start * 2) {
    printMatixInCircle(num, cols, rows, start);
    ++start;
  }
}

function printMatixInCircle(num, cols, rows, start) {
  let endX = cols - 1 - start;
  let endY = rows - 1 - start;

  for (let i = start; i <= endX; ++i) {
    let number = num[start][i];
    console.log(number)
  }

  if (start < endY) {
    for (let i = start + 1; i <= endY; i++) {
      let number = num[i][endX];
      console.log(number)
    }
  }

  if (start < endX && start < endY) {
    for (let i = endX - 1; i >= start; --i) {
      let number = num[endY][i];
      console.log(number);
    }
  }

  if (start < endX && start < endY - 1) {
    for (let i = endY - 1; i >= start + 1; --i) {
      let number = numbers[i][start];
      console.log(number);
    }
  }
}