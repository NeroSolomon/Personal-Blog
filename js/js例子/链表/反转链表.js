function reverseLinkList(head) {
  if (head == null) return null;
  const result = [];
  let node = head;
  while(node != null) {
    result.push(node.val);
    node = node.next;
  }

  node = head
  while (node != null) {
    node.val = arr.pop();
    node = node.next;
  }

  return result[0];
}