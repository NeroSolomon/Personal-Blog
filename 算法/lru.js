class LRUCache {
    constructor(capacity) {
        this.capacity = capacity;
        this.cache = {};
        this.size = 0;
        this.head = null;
        this.tail = null;
    }
 
    get(key) {
        if (this.cache[key]) {
            this.moveToHead(key);
            return this.cache[key].value;
        }
        return -1; // 或者其他标识值，表示key不存在
    }
 
    put(key, value) {
        if (this.cache[key]) {
            this.cache[key].value = value;
            this.moveToHead(key);
        } else {
            this.addToHead(key, value);
            this.size++;
            if (this.size > this.capacity) {
                this.removeTail();
            }
        }
    }
 
    moveToHead(key) {
        const node = this.cache[key];
        this.removeNode(node);
        this.addToHead(key, node.value);
    }
 
    addToHead(key, value) {
        const node = { key, value, next: null, prev: null };
        if (!this.head) {
            this.head = node;
            this.tail = node;
        } else {
            node.next = this.head;
            this.head.prev = node;
            this.head = node;
        }
        this.cache[key] = node;
    }
 
    removeNode(node) {
        if (node.prev) {
            node.prev.next = node.next;
        } else {
            this.head = node.next;
        }
        if (node.next) {
            node.next.prev = node.prev;
        } else {
            this.tail = node.prev;
        }
    }
 
    removeTail() {
        const key = this.tail.key;
        this.removeNode(this.tail);
        delete this.cache[key];
        this.size--;
        if (!this.head) {
            this.tail = null;
        }
    }
}
 
// 使用示例
const cache = new LRUCache(2);
cache.put('key1', 'value1');
cache.put('key2', 'value2');
console.log(cache.get('key1')); // 返回 'value1'
cache.put('key3', 'value3'); // 这时 'key2' 被淘汰
console.log(cache.get('key2')); // 返回 -1 (因为 'key2' 已被淘汰)
console.log(cache.get('key3')); // 返回 'value3'

// 应用方向：
// 浏览器浏览记录
// vue中的内置组件keep-alive：Vue提供了一个内置组件keep-alive，用于缓存组件状态，避免重新渲染。通过LRU策略，keep-alive可以优先缓存最近使用的组件，从而提高页面切换的效率‌