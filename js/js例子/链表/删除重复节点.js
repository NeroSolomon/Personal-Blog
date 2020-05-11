// 只要重复，就在链表中删除
function deleteDuplicateNode(head) {
  let newHead = {}
  newHead.next = head
  let current = newHead
  let nextNode = current.next

  while(nextNode) {
    while (nextNode.next && nextNode.val == nextNode.next.val) {
      nextNode = nextNode.next
    }

    if (current.next.val == nextNode.val) {
      current = nextNode
      nextNode = nextNode.next
    } else {
      nextNode = nextNode.next
      current.next = nextNode
    }
  }

  return newHead.next
}