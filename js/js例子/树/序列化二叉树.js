function TreeNode(x) {
  this.val = x;
  this.left = null;
  this.right = null;
}

var arr = [];

function serialize(root) {
  if (root == null) {
    arr.push('$');
  } else {
    arr.push(root.val);
    serialize(root.left);
    serialize(root.right);
  }
}

function deserialize() {
  var node = null;
  if(arr.length<1) {
    return null;
  }
  var number = arr.shift();
  if(typeof number == 'number') {
    node = new TreeNode(number);
    node.left = deserialize(arr);
    node.right = deserialize(arr);
  }
}