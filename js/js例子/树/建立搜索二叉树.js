// 二叉树节点构造函数
function Node(data) {
  this.data = data
  this.left = null
  this.right = null
}

// 二叉树构造函数
function BinaryTree() {
  // 根结点
  this.root = null
  function insertNode(node, current) {
    if (node.data > current.data) {
      if (current.right) {
        // 大于放右子树
        insertNode(node, current.right)
      } else {
        // 右边的树节点不存在时
        current.right = node
      }
    } else {
      if (current.left) {
        // 小于放左子树
        insertNode(node, current.left)
      } else {
        // 左边的树节点不存在时
        current.left = node
      }
    }
  }
  this.insert = function(data) {
    const node = new Node(data)
    if (this.root == null) {
      // 树为空的情况
      this.root = node
    } else {
      // 树不为空的话就要按照搜索二叉树的格式插入
      // 私有方法
      insertNode(node, this.root)
    }
  }
}

