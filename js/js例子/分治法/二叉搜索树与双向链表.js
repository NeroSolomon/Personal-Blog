function convert(pRootOfTree) {
  if (!pRootOfTree) return null;
  var arr=[], len=0;
  sub(pRootOfTree, arr);
  len=arr.length;
  arr[0].left=null;
  arr[0].right=arr[1];
  for(let i=1; i<len-1; i++) {
    arr[i].left=arr[i-1];
    arr[i].right=arr[i+1];
  }
  arr[len-1].left=arr[len-2];
  arr[len-1].right=null;
  return arr[0];
}

function sub(node, arr) {
  if (!node) return;
  sub(node.left, arr);
  arr.push(node);
  sub(node.right, arr);
}