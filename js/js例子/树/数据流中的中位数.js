let arr=[];
// 读取数据流
function Insert(num)
{
    // write code here
    arr.push(num);
    let i = arr.length - 1;
    while(i > 0) {
      if (arr[i] < arr[i-1]) {
        [arr[i], arr[i-1]] = [arr[i - 1], arr[i]];
        i--;
      } else {
        break;
      }
    }
}

// 得到中位数
function GetMedian(){
    // write code here
    if (!arr.length) {
      return null;
    }
    else if (arr.length%2) {
      return arr[Math.floor(arr.length / 2)];
    } else {
      let mid = arr.length/2;
      return ((arr[mid] + arr[mid - 1]) / 2);
    }
}