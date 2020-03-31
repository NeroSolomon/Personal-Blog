function printFromTopToBottom(root) {
  if (!root) return;
  const arr = [];
  const data = [];
  arr.push(root);
  while(arr.length) {
    const len = arr.length;
    const tempArr = [];
    for (let i = 0; i < len; i++) {
      const node = arr.shift();
      if (node.left != null) {
        arr.push(node.left)
      }
      if (node.right != null) {
        arr.push(node.right)
      }
      tempArr.push(node.val)
    }
    data.push(tempArr);
  }
  return data;
}