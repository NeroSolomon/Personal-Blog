// 前序遍历 [1, 2, 4, 7, 3, 5, 6, 8]
// 中序遍历 [4, 7, 2, 1, 5, 3, 8, 6]
// 根据前序遍历和中序遍历重建二叉树，并返回root

// 二叉树节点构造函数
function Node(data) {
  this.data = data
  this.left = null
  this.right = null
}

function buildBinaryTree(pre, vin) {
  let result = null
  if (pre.length > 1) {
    const root = pre[0]
    const index = vin.indexOf(root)
    const vinLeft = vin.slice(0, index)
    const vinRight = vin.slice(index + 1)
    pre.shift()
    const preLeft = pre.slice(0, vinLeft.length)
    const preRight = pre.slice(vinLeft.length)
    result = {
      data: root,
      left: buildBinaryTree(preLeft, vinLeft),
      right:buildBinaryTree(preRight, vinRight)
    }
  } else if (pre.length == 1) {
    result = {
      data: pre[0],
      left: null,
      right: null
    }
  }
  return result
}

const pre = [1, 2, 4, 7, 3, 5, 6, 8]
const vin = [4, 7, 2, 1, 5, 3, 8, 6]

const result = buildBinaryTree(pre, vin)

console.log(result)