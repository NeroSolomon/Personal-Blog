// localStorage实现1兆缓存

/**
1. 在loaclStorage里存一个专门计算存储大小的key
2. 计算下一次缓存的体积，转blob后使用blob.size()得到大小，并用时间戳标记新鲜度
3. 假如加上这次缓存体积不超出限制，则存入
4. 超出，则删除新鲜度最低的缓存
 */