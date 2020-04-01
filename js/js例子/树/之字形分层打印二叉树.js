function printFromTopToBottom(root) {
  if (!root) return;
  const arr = [];
  const data = [];
  arr.push(root);
  let index = 1;
  while(arr.length) {
    const len = arr.length;
    const tempArr = [];
    for (let i = 0; i < index; i++) {
      const node = arr.shift();
      if (index % 2 == 0) {
        node.left && arr.push(node.left);
        node.right && arr.push(node.right);
      } else {
        node.right && arr.push(node.right);
        node.left && arr.push(node.left);
      }
      data.push(node.val)
    }
    arr.push(tempArr);
    index++;
  }
  return data;
}

function Print(pRoot)
{
    if(!pRoot){
        return [];
    }
    var queue = [],
        result = [],
        flag=true;
    queue.push(pRoot);
    while(queue.length){
        var len = queue.length;
        var tempArr = [];
        for(var i = 0;i<len;i++){
            var temp = queue.shift();
            tempArr.push(temp.val);
            if(temp.left){
                queue.push(temp.left);
            }
            if(temp.right){
                queue.push(temp.right);
            }
        }
        if(!flag){
            tempArr.reverse();
        }
        flag = !flag;
        result.push(tempArr);
    }
    return result;
}