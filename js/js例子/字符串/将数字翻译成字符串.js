function getTranslationCount(num) {
  if (num < 0) return 0;
  const str = num + '';
  return getTranslate(str);
}

function getTranslate(str) {
  let len = str.length;
  let counts = [];
  let count = 0;

  for (let i = len - 1; i >= 0; --i) {
    count = 0;
    if (i < len-1) {
      count = counts[i+1];
    } else {
      count = 1;
    }

    if (i < len-1) {
      let digit1 = str[i] - '0';
      let digit2 = str[i + 1] - '0';
      let converted = digit1 * 10 + digit2;
      if (converted >= 10 && converted <=25) {
        if (i < len - 2) {
          count += counts[i+2];
        } else {
          count +=1;
        }
      }
    }
    counts[i] = count;
  }
  count = counts[0];
  return count;
}

const count = getTranslationCount(11);
console.log(count);