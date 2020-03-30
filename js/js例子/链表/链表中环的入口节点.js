function entryNodeOfLoop(head) {
  if (head == null || head.next == null || head.next.next == null) return null;
  let fast = head.next.next;
  let slow = head.next;
  while(fast&&slow) {
    if (fast!=slow) {
      fast = fast.next.next;
      slow = slow.next;
    } else {
      break;
    }
  }

  if (fast == null || slow == null) return null
  fast = head;
  while (fast != slow) {
    fast = fast.next;
    slow = slow.next
  }
}