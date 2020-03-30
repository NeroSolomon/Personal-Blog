function Node(value) {
  this.value = value
  this.next = null
}

function LinkedList() {
  this.head = null
}

LinkedList.prototype.push = function(node) {
  if (head) {
    const current = head
    while(current.next) {
      current = current.next
    }
    current.next = node
  } else {
    head = node
  }
}

// 栈方法
LinkedList.prototype.reverse1 = function () {
  const current = head
  const nodes = []
  while(current.next) {
    nodes.push(current.next.value)
  }
  while(nodes.length) {
    console.log(nodes.pop())
  }
}

// 递归方法，会有栈溢出的风险
LinkedList.prototype.reverse1 = function(node) {
  const current = node
  if (current.next) {
    arguments.callee(current.next)
  }
  console.log(current.value)
}
