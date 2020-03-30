function findKth(head, k) {
  let result = [];
  const node = head;
  while (node != null) {
    result.push(head);
    node = head.next;
  }

  result.reverse();
  return result[k];
}
