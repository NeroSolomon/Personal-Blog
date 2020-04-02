function RandomListNode(params) {
  this.label = x;
  this.next = null;
  this.random = null;
}

function clone(pHead) {
  if (pHead == null) return null;
  let cur = pHead;
  let tmp, pNewHead=null;
  // 在N后复制一个N‘
  while(cur){
    tmp = new RandomListNode(cur.label);
    tmp.next = cur.next;
    cur.next = tmp;
    cur = cur.next.next;
  }
  cur = pHead;
  // 复制random
  while(cur){
    if (cur.random) {
      cur.next.random = cur.random.next;
    }
    cur=cur.next.next;
  };
  pNewHead = pHead.next;
  cur = pHead;
  let clone;
  while(cur) {
    clone = cur.next;
    cur.next = clone.next;
    clone.next = (clone.next == null ? null : clone.next.next);
    cur = cur.next;
  }
  return pNewHead;
}