function printFromTopToBottom(root) {
  const arr = [];
  const data = [];
  if (root != null) {
    arr.push(root);
  }
  while(arr.length != 0) {
    const node = arr.shift();
    if (node.left != null) {
      arr.push(node.left);
    }
    if (node.right != null) {
      arr.push(node.right);
    }
    data.push(node.val);
  }
  return data;
}