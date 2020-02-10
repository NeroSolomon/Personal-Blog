// 有两个排好序的数组，现在不另外添加内存的情况下合并这两个数组，并要求时间复杂度在O(n)
function combine(arr1, arr2) {
  let len1 = arr1.length - 1
  let len2 = arr2.length - 1
  let len3 = len1 + len2 + 1
  while (len3 >= 0 && len1 >= 0 && len2 >= 0) {
    if (arr1[len1] < arr2[len2]) {
      arr1[len3--] = arr2[len2--]
    } else {
      arr1[len3--] = arr1[len1--]
    }
  }
}

const arr1 = [1, 2, 4, 6, 8, 9]

const arr2 = [3, 3, 4, 5, 7, 10, 10]

combine(arr1, arr2)
console.log(arr1)

// 感悟：要想元素移动得少，必须从后开始确定位置，从前开始插入的话，会导致后面的元素频繁移动