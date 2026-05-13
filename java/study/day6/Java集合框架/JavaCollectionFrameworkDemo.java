/*
 * ============================================================
 *  Java 集合框架 — 高频面试题全解（通俗版）
 * ============================================================
 */

import java.util.*;
import java.util.concurrent.*;

public class JavaCollectionFrameworkDemo {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  Java 集合框架 —— 8 大面试题全解");
        System.out.println("========================================");

        // ============================================================
        //  Q1: List、Set、Map 三者的区别？底层数据结构？
        // ============================================================
        System.out.println("\n┌─────────────────────────────────────────────────────────┐");
        System.out.println("│ Q1: List、Set、Map 三者的区别？底层数据结构？           │");
        System.out.println("└─────────────────────────────────────────────────────────┘");

        System.out.println("\n  ┌──────────┬────────────┬──────────┬──────────────────┐");
        System.out.println("  │          │ List       │ Set      │ Map              │");
        System.out.println("  ├──────────┼────────────┼──────────┼──────────────────┤");
        System.out.println("  │ 是否有序 │ 有序       │ 不一定   │ 不保证           │");
        System.out.println("  │ 是否重复 │ 可重复     │ 不可重复 │ Key不可,Val可    │");
        System.out.println("  │ 有无索引 │ 有(get(i)) │ 无       │ 无(用Key取)      │");
        System.out.println("  │ 存几个值 │ 单值       │ 单值     │ 键值对(双值)     │");
        System.out.println("  └──────────┴────────────┴──────────┴──────────────────┘");

        System.out.println("\n  底层数据结构：");
        System.out.println("    ArrayList     → Object[] 数组（查快 O(1)）");
        System.out.println("    LinkedList    → 双向链表（增删快 O(1)）");
        System.out.println("    HashSet       → HashMap（值是 dummy 的 Object）");
        System.out.println("    LinkedHashSet → LinkedHashMap（双向链表保顺序）");
        System.out.println("    TreeSet       → TreeMap（红黑树，自动排序）");
        System.out.println("    HashMap       → 数组 + 链表/红黑树（JDK 8+）");
        System.out.println("    LinkedHashMap → HashMap + 双向链表（保插入顺序）");
        System.out.println("    TreeMap       → 红黑树（按 Key 排序）");
        System.out.println("    Hashtable     → 数组 + 链表（线程安全，全表锁）");

        // ============================================================
        //  Q2: 哪些集合线程不安全？怎么解决？
        // ============================================================
        System.out.println("\n┌─────────────────────────────────────────────────────────┐");
        System.out.println("│ Q2: 哪些集合线程不安全？怎么解决？                      │");
        System.out.println("└─────────────────────────────────────────────────────────┘");

        System.out.println("\n  线程不安全的集合（绝大部分）：");
        System.out.println("    ArrayList, LinkedList, HashMap, HashSet,");
        System.out.println("    TreeMap, TreeSet, LinkedHashMap, PriorityQueue...");

        System.out.println("\n  怎么解决？三种方案：");

        System.out.println("\n  方案① Collections.synchronizedXxx() 包装：");
        List<String> safeList = Collections.synchronizedList(new ArrayList<>());
        Map<String, String> safeMap = Collections.synchronizedMap(new HashMap<>());
        Set<String> safeSet = Collections.synchronizedSet(new HashSet<>());
        safeList.add("线程安全");
        System.out.println("    safeList: " + safeList);
        System.out.println("    → 原理：给每个方法加 synchronized，简单但性能一般");

        System.out.println("\n  方案② 用 JUC 并发容器（推荐！）：");
        CopyOnWriteArrayList<String> cowList = new CopyOnWriteArrayList<>();
        ConcurrentHashMap<String, String> chm = new ConcurrentHashMap<>();
        cowList.add("写时复制，读不锁");
        chm.put("k", "v");
        System.out.println("    CopyOnWriteArrayList  → 写时复制，读不加锁");
        System.out.println("    ConcurrentHashMap     → 分段/桶级锁，高并发");

        System.out.println("\n  方案③ 用旧的线程安全类（不推荐）：");
        System.out.println("    Vector   → 所有方法 synchronized，锁太粗");
        System.out.println("    Hashtable→ 所有方法 synchronized，锁太粗");

        // ============================================================
        //  Q2.5: 为什么某些集合线程不安全？（根本原因 + 实测）
        // ============================================================
        System.out.println("\n┌─────────────────────────────────────────────────────────┐");
        System.out.println("│ Q2.5: 为什么某些集合线程不安全？（根本原因）            │");
        System.out.println("└─────────────────────────────────────────────────────────┘");

        System.out.println("\n  总原则：线程不安全 = 没有同步保护，多线程并发操作时出现数据错乱。");
        System.out.println("  本质就三类问题：数据覆盖丢失 / 死循环 / 读到脏数据。");
        System.out.println();

        // --- ArrayList 为什么不安全 ---
        System.out.println("  ─── ① ArrayList 为什么线程不安全？ ───");
        System.out.println();
        System.out.println("    ArrayList 底层是 Object[] elementData，add 的源码：");
        System.out.println("      public boolean add(E e) {");
        System.out.println("          elementData[size++] = e;   ← 这一行不是原子的！");
        System.out.println("          return true;");
        System.out.println("      }");
        System.out.println();
        System.out.println("    「elementData[size++] = e」分三步：");
        System.out.println("      ① 读取 size 的当前值");
        System.out.println("      ② 把 e 放到 elementData[size]");
        System.out.println("      ③ size + 1");
        System.out.println();
        System.out.println("    如果两个线程同时 add，可能出现：");
        System.out.println("      线程A 读到 size=0 → 把数据放到 [0] → 还没来得及加1");
        System.out.println("      线程B 也读到 size=0 → 把数据也放到 [0]！→ A 的数据被覆盖！");
        System.out.println("      size 只加了 1，但丢了 1 个元素。");
        System.out.println();

        // 实测演示
        System.out.println("    [实测] 10 个线程各向同一个 ArrayList add 1000 次：");
        List<String> unsafeList = new ArrayList<>();
        CountDownLatch latch1 = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    unsafeList.add("x");
                }
                latch1.countDown();
            }).start();
        }
        try { latch1.await(); } catch (Exception e) {}
        System.out.println("    期望 10000 个，实际: " + unsafeList.size()
                           + " → 丢数据了！这就是线程不安全！");
        System.out.println("    严重时甚至抛 ArrayIndexOutOfBoundsException（size 错乱）。");
        System.out.println();

        // --- HashMap 为什么不安全 ---
        System.out.println("  ─── ② HashMap 为什么线程不安全？ ───");
        System.out.println();
        System.out.println("    JDK 7：扩容时采用头插法，多线程扩容可能形成循环链表 →");
        System.out.println("            get() 时 CPU 100% 死循环（经典 bug）。");
        System.out.println();
        System.out.println("    JDK 8：改为尾插法，解决了死循环。但数据覆盖问题依然存在：");
        System.out.println("      put 方法里没有同步，两个线程同时 put 同一个桶：");
        System.out.println("        线程A 计算桶下标 → 找到桶 → 刚要写，CPU 切换走");
        System.out.println("        线程B 也找到同一个桶 → 写入 → 走人");
        System.out.println("        线程A 切回来 → 把 B 的数据覆盖了！→ 丢数据");
        System.out.println("      同时，size++ 也是非原子的，可能计数不准确。");
        System.out.println();

        // 实测演示
        System.out.println("    [实测] 10 个线程各向 HashMap put 1000 次：");
        HashMap<String, Integer> unsafeMap = new HashMap<>();
        CountDownLatch latch2 = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            final int threadId = i;
            new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    unsafeMap.put(threadId + "-" + j, j);
                }
                latch2.countDown();
            }).start();
        }
        try { latch2.await(); } catch (Exception e) {}
        System.out.println("    期望 10000 个，实际: " + unsafeMap.size()
                           + " → 丢数据了！这就是线程不安全！");
        System.out.println();

        // --- LinkedList 为什么不安全 ---
        System.out.println("  ─── ③ LinkedList 为什么线程不安全？ ───");
        System.out.println();
        System.out.println("    LinkedList 是双向链表，add 时需要同时改前驱的 next 和后继的 prev。");
        System.out.println("    两个线程同时 add，一个刚改了前驱的 next，另一个也改了前驱的 next");
        System.out.println("    → 链表断裂，后续遍历可能 null.f() 抛异常。");
        System.out.println();

        // --- HashSet 为什么不安全 ---
        System.out.println("  ─── ④ HashSet 为什么线程不安全？ ───");
        System.out.println();
        System.out.println("    HashSet 底层就是 HashMap（add ≡ map.put(e, PRESENT)），");
        System.out.println("    HashMap 不安全 → HashSet 也不安全，继承性危险。");
        System.out.println();

        // --- 一句话总结 ---
        System.out.println("  ═══════════════════════════════════════════");
        System.out.println("  总结：线程不安全的根因");
        System.out.println("  ═══════════════════════════════════════════");
        System.out.println("    ① 方法没有 synchronized / CAS 保护（如 HashMap.put）");
        System.out.println("    ② 复合操作不是原子的（如 ArrayList 的 size++）");
        System.out.println("    ③ 读-改-写 不是原子的（先读 size 再改 size）");
        System.out.println("    ④ 扩容期间数据结构不一致（链表被别的线程打断）");
        System.out.println("    ⑤ 线程间不可见（一个线程改了 size，另一个看不见）");

        // ============================================================
        //  Q2.6: 什么是 JUC 并发容器？
        // ============================================================
        System.out.println("\n┌─────────────────────────────────────────────────────────┐");
        System.out.println("│ Q2.6: 什么是 JUC 并发容器？                              │");
        System.out.println("└─────────────────────────────────────────────────────────┘");

        System.out.println();
        System.out.println("  JUC = java.util.concurrent 包，是 JDK 1.5 引入的并发工具包。");
        System.out.println("  JUC 并发容器 = 这个包里所有线程安全的集合类。");
        System.out.println();
        System.out.println("  ─── 为什么要单独搞一套？ ───");
        System.out.println("    老方案 Vector/Hashtable → 所有方法加 synchronized(全表锁)");
        System.out.println("    → 读也锁、写也锁，高并发下性能太差，不如重新设计。");
        System.out.println("    → JUC 用 CAS、分段锁、写时复制等更高级的技术来搞。");
        System.out.println();
        System.out.println("  ─── JUC 并发容器全家福 ───");
        System.out.println();
        System.out.println("  ┌────────────────────────────────────────────────────────┐");
        System.out.println("  │ Map 类                                                 │");
        System.out.println("  ├────────────────────────────────────────────────────────┤");
        System.out.println("  │ ConcurrentHashMap  桶级锁(CAS+synchronized)，读无锁    │");
        System.out.println("  │ ConcurrentSkipListMap  跳表，高并发下 key 有序存储     │");
        System.out.println("  ├────────────────────────────────────────────────────────┤");
        System.out.println("  │ List 类                                                │");
        System.out.println("  ├────────────────────────────────────────────────────────┤");
        System.out.println("  │ CopyOnWriteArrayList  写时复制，读全无锁，适合读多写少 │");
        System.out.println("  ├────────────────────────────────────────────────────────┤");
        System.out.println("  │ Set 类                                                 │");
        System.out.println("  ├────────────────────────────────────────────────────────┤");
        System.out.println("  │ CopyOnWriteArraySet   底层就是 CopyOnWriteArrayList    │");
        System.out.println("  │ ConcurrentSkipListSet 底层就是 ConcurrentSkipListMap   │");
        System.out.println("  ├────────────────────────────────────────────────────────┤");
        System.out.println("  │ Queue 类                                               │");
        System.out.println("  ├────────────────────────────────────────────────────────┤");
        System.out.println("  │ ArrayBlockingQueue    有界数组阻塞队列                 │");
        System.out.println("  │ LinkedBlockingQueue   可选有界链表阻塞队列             │");
        System.out.println("  │ PriorityBlockingQueue 无界优先级阻塞队列               │");
        System.out.println("  │ SynchronousQueue      不存任务，直接交接               │");
        System.out.println("  │ DelayQueue           延迟队列（定时任务）              │");
        System.out.println("  │ ConcurrentLinkedQueue 非阻塞的无锁队列（CAS）          │");
        System.out.println("  ├────────────────────────────────────────────────────────┤");
        System.out.println("  │ Deque 类                                               │");
        System.out.println("  ├────────────────────────────────────────────────────────┤");
        System.out.println("  │ LinkedBlockingDeque   双端阻塞队列                     │");
        System.out.println("  │ ConcurrentLinkedDeque 非阻塞双端无锁队列               │");
        System.out.println("  └────────────────────────────────────────────────────────┘");

        System.out.println();
        System.out.println("  ─── 选型口诀 ───");
        System.out.println("    Map 要线程安全       → ConcurrentHashMap（别用 Hashtable）");
        System.out.println("    List 读多写极少       → CopyOnWriteArrayList（读零开销）");
        System.out.println("    List 读写都频繁       → Collections.synchronizedList");
        System.out.println("    Queue 要阻塞         → BlockingQueue（生产者消费者）");
        System.out.println("    Queue 高性能无锁      → ConcurrentLinkedQueue");
        System.out.println();

        // --- 核心原理对比 ---
        System.out.println("  ─── 核心技术对比 ───");
        System.out.println();
        System.out.println("  ┌──────────────────────┬──────────────┬──────────────────┐");
        System.out.println("  │ 技术                 │ 代表容器     │ 一句话            │");
        System.out.println("  ├──────────────────────┼──────────────┼──────────────────┤");
        System.out.println("  │ 全表 synchronized    │ Vector       │ 读写全锁，性能最差│");
        System.out.println("  │ 分段锁 Segment       │ CHM(JDK7)    │ 16 把锁各管一片   │");
        System.out.println("  │ 桶锁 CAS+synchronized│ CHM(JDK8)    │ 锁到桶级，读无锁  │");
        System.out.println("  │ 写时复制 CopyOnWrite │ COWList      │ 写时复制全数组    │");
        System.out.println("  │ 无锁 CAS            │ ConcurrentLQ │ 纯 CAS，零锁      │");
        System.out.println("  └──────────────────────┴──────────────┴──────────────────┘");

        // ============================================================
        //  Q3: HashSet vs LinkedHashSet vs TreeSet
        // ============================================================
        System.out.println("\n┌─────────────────────────────────────────────────────────┐");
        System.out.println("│ Q3: 比较 HashSet、LinkedHashSet、TreeSet 三者的异同     │");
        System.out.println("└─────────────────────────────────────────────────────────┘");

        Set<String> hashSet = new HashSet<>();
        Set<String> linkedSet = new LinkedHashSet<>();
        Set<String> treeSet = new TreeSet<>();

        String[] data = {"banana", "apple", "cherry", "apple"};
        for (String s : data) {
            hashSet.add(s);
            linkedSet.add(s);
            treeSet.add(s);
        }

        System.out.println("\n  放入顺序: banana, apple, cherry, apple (重复)");
        System.out.println("    HashSet       : " + hashSet + "    ← 无序，去重");
        System.out.println("    LinkedHashSet : " + linkedSet + " ← 按插入顺序");
        System.out.println("    TreeSet       : " + treeSet + "      ← 自然排序(字典序)");

        System.out.println("\n  ┌─────────────┬──────────┬───────────────┬──────────┐");
        System.out.println("  │             │ HashSet  │ LinkedHashSet │ TreeSet  │");
        System.out.println("  ├─────────────┼──────────┼───────────────┼──────────┤");
        System.out.println("  │ 底层         │ HashMap  │ LinkedHashMap │ TreeMap  │");
        System.out.println("  │ 有序吗       │ 无序     │ 插入顺序      │ 排序顺序 │");
        System.out.println("  │ null         │ 允许1个  │ 允许1个       │ 不允许   │");
        System.out.println("  │ add/remove   │ O(1)     │ O(1)          │ O(log n) │");
        System.out.println("  │ 适用场景     │ 最快去重 │ 去重+保顺序   │ 去重+排序│");
        System.out.println("  └─────────────┴──────────┴───────────────┴──────────┘");

        // ============================================================
        //  Q4: HashMap vs Hashtable vs HashSet vs TreeMap
        // ============================================================
        System.out.println("\n┌─────────────────────────────────────────────────────────┐");
        System.out.println("│ Q4: HashMap vs Hashtable vs HashSet vs TreeMap          │");
        System.out.println("└─────────────────────────────────────────────────────────┘");

        System.out.println("\n  [4-1] HashMap vs Hashtable:");
        System.out.println("  ┌──────────────┬────────────────┬──────────────────┐");
        System.out.println("  │              │ HashMap        │ Hashtable        │");
        System.out.println("  ├──────────────┼────────────────┼──────────────────┤");
        System.out.println("  │ 诞生时间     │ Java 1.2       │ Java 1.0         │");
        System.out.println("  │ 线程安全     │ ✗ 不安全        │ ✓ (全表锁)      │");
        System.out.println("  │ null Key     │ ✓ 允许1个      │ ✗ 不允许         │");
        System.out.println("  │ null Value   │ ✓ 允许         │ ✗ 不允许         │");
        System.out.println("  │ 效率         │ 高             │ 低(锁开销)       │");
        System.out.println("  │ 底层         │ 数组+链表/红黑 │ 数组+链表        │");
        System.out.println("  │ 是否推荐     │ ✓ 推荐          │ ✗ 不推荐         │");
        System.out.println("  │ 替代方案     │ ConcurrentHashMap 替代多线程场景 │");
        System.out.println("  └──────────────┴────────────────┴──────────────────┘");

        // 演示 Hashtable 不允许 null
        try {
            Hashtable<String, String> ht = new Hashtable<>();
            ht.put(null, "v");
        } catch (NullPointerException e) {
            System.out.println("    实测: Hashtable.put(null, v) → NullPointerException ✓");
        }

        System.out.println("\n  [4-2] HashMap vs HashSet:");
        System.out.println("    HashSet 底层就是一个 HashMap！");
        System.out.println("    你 add(e) → 内部 map.put(e, PRESENT)");
        System.out.println("    PRESENT 是一个固定的 dummy Object，没有意义。");
        System.out.println("    → HashSet 就是只有 Key 没有 Value 的 HashMap。");

        // 验证
        HashSet<String> hs = new HashSet<>();
        hs.add("A");
        System.out.println("    HashSet.add(\"A\") ≡ HashMap.put(\"A\", dummyObject)");

        System.out.println("\n  [4-3] HashMap vs TreeMap:");
        System.out.println("  ┌──────────────┬────────────────┬──────────────────┐");
        System.out.println("  │              │ HashMap        │ TreeMap          │");
        System.out.println("  ├──────────────┼────────────────┼──────────────────┤");
        System.out.println("  │ 底层         │ 数组+链表/红黑 │ 红黑树           │");
        System.out.println("  │ 有序吗       │ 无序           │ 按 Key 排序       │");
        System.out.println("  │ null Key     │ ✓ 允许1个      │ ✗ 不允许         │");
        System.out.println("  │ get/put      │ O(1)~O(log n)  │ O(log n)严格保证 │");
        System.out.println("  │ 怎么比较     │ hashCode+equals│ Comparable/Comp  │");
        System.out.println("  │ 适用场景     │ 快速存取       │ 需要有序遍历     │");
        System.out.println("  └──────────────┴────────────────┴──────────────────┘");

        // 演示 TreeMap 自动排序
        TreeMap<String, Integer> tm = new TreeMap<>();
        tm.put("C", 3);
        tm.put("A", 1);
        tm.put("B", 2);
        System.out.println("    实测: TreeMap 按 Key 自动排序 → " + tm);

        // ============================================================
        //  Q5: HashMap 的底层实现
        // ============================================================
        System.out.println("\n┌─────────────────────────────────────────────────────────┐");
        System.out.println("│ Q5: HashMap 的底层实现                                  │");
        System.out.println("└─────────────────────────────────────────────────────────┘");

        System.out.println("\n  核心数据结构：数组 + 链表 + 红黑树 (JDK 1.8+)");
        System.out.println();
        System.out.println("  HashMap 内部是一个 Node<K,V>[] table 数组，称为「桶数组」：");
        System.out.println();
        System.out.println("  table = [ 桶0 ] → [ 桶1 ] → [ 桶2 ] → ... → [ 桶15 ]");
        System.out.println("              |                    |");
        System.out.println("            Node                 Node");
        System.out.println("              ↓                    ↓");
        System.out.println("            Node(红黑树)          Node → Node (链表)");
        System.out.println();
        System.out.println("  put(key, value) 的完整流程：");
        System.out.println("    1. 对 key 求 hashCode()");
        System.out.println("    2. hashCode 高16位与低16位异或（扰动，减少碰撞）");
        System.out.println("    3. (n-1) & hash 计算桶下标（等价于 hash % n 但更快）");
        System.out.println("    4. 该桶为空 → 直接放入");
        System.out.println("    5. 该桶有值 → 遍历链表/红黑树用 equals 比较");
        System.out.println("       相等就覆盖，不等就追加到尾部");
        System.out.println("    6. 链表长度 ≥ 8 且数组长度 ≥ 64 → 链表转红黑树");
        System.out.println("    7. 红黑树节点数 ≤ 6 → 红黑树退化为链表");
        System.out.println();
        System.out.println("  get(key) 的流程：");
        System.out.println("    同样算 hashCode → 找桶下标 → 链表/树里 equals 找");
        System.out.println();
        System.out.println("  扩容机制：");
        System.out.println("    负载因子默认 0.75，超过时扩容为原来 2 倍");
        System.out.println("    扩容不是简单拷贝，每个节点要重新计算在新数组中的位置");
        System.out.println("    （因为下标 = hash & (n-1)，n 变了，下标可能变）");

        // 演示扩容
        HashMap<Integer, String> demo = new HashMap<>(4, 0.75f); // 初始4个桶
        demo.put(1, "A");
        demo.put(2, "B");
        demo.put(3, "C");
        demo.put(4, "D");   // 4/4=1.0 > 0.75 → 扩容！

        // ============================================================
        //  Q6: HashMap 的长度为什么是 2 的幂次方？
        // ============================================================
        System.out.println("\n┌─────────────────────────────────────────────────────────┐");
        System.out.println("│ Q6: HashMap 的长度为什么是 2 的幂次方？                 │");
        System.out.println("└─────────────────────────────────────────────────────────┘");

        System.out.println("\n  核心原因：为了用 位运算 代替 取模运算，速度快！");
        System.out.println();
        System.out.println("  计算桶下标的公式： index = (n - 1) & hash");
        System.out.println();
        System.out.println("  只有当 n 是 2 的幂次方时，(n-1) & hash 才等价于 hash % n。");
        System.out.println();
        System.out.println("  ┌──────────────────────────────────────────────────────┐");
        System.out.println("  │  n=16 (2^4)   n-1 = 15 = 0000 1111                  │");
        System.out.println("  │  hash = 18          = 0001 0010                      │");
        System.out.println("  │  18 & 15 = 0000 0010 = 2                             │");
        System.out.println("  │  18 % 16 = 2                     ✓ 结果相同！        │");
        System.out.println("  ├──────────────────────────────────────────────────────┤");
        System.out.println("  │  如果 n=10（不是 2 的幂）：                           │");
        System.out.println("  │  n-1 = 9 = 0000 1001                                 │");
        System.out.println("  │  18 & 9 = 0000 1000 = 8                               │");
        System.out.println("  │  18 % 10 = 8                     ✗ 碰巧一样          │");
        System.out.println("  │  20 & 9 = 0000 0000 = 0                               │");
        System.out.println("  │  20 % 10 = 0                     = 碰巧一样           │");
        System.out.println("  │  但很多值对不上，且分布不均匀！                       │");
        System.out.println("  └──────────────────────────────────────────────────────┘");
        System.out.println();
        System.out.println("  三个好处：");
        System.out.println("    1. 位运算比取模快（CPU 直接算，不除除法器）");
        System.out.println("    2. (n-1) 的二进制全是 1，分布均匀，减少碰撞");
        System.out.println("    3. 扩容时重新计算下标很方便（要么在原位，要么原位+旧容量）");
        System.out.println();
        System.out.println("  HashMap 怎么保证容量是 2 的幂？");
        System.out.println("    tableSizeFor(int cap) → 不管你传什么，都给你算成");
        System.out.println("    大于 cap 的最小 2 的幂。 传 10 → 给你 16。");

        // ============================================================
        //  Q7: ConcurrentHashMap vs Hashtable
        // ============================================================
        System.out.println("\n┌─────────────────────────────────────────────────────────┐");
        System.out.println("│ Q7: ConcurrentHashMap 和 Hashtable 的区别？             │");
        System.out.println("└─────────────────────────────────────────────────────────┘");

        System.out.println("\n  ┌──────────────────┬──────────────────────┬────────────┐");
        System.out.println("  │                  │ Hashtable            │ Concurrent │");
        System.out.println("  │                  │                      │ HashMap    │");
        System.out.println("  ├──────────────────┼──────────────────────┼────────────┤");
        System.out.println("  │ 实现方式         │ synchronized 方法锁  │ CAS+synchron│");
        System.out.println("  │                  │                      │ ized桶锁   │");
        System.out.println("  │ 锁粒度           │ 全表锁（粗）         │ 桶级锁(细) │");
        System.out.println("  │ 读是否锁         │ 锁（get 也锁！）     │ 不锁！     │");
        System.out.println("  │ 并发度           │ 低（串行化）         │ 高         │");
        System.out.println("  │ null Key/Value   │ 不允许               │ 不允许     │");
        System.out.println("  │ 迭代器           │ fail-fast            │ fail-safe  │");
        System.out.println("  │ 扩容时           │ 锁全表               │ 多线程协作 │");
        System.out.println("  │ 推荐度           │ 不推荐               │ 推荐       │");
        System.out.println("  └──────────────────┴──────────────────────┴────────────┘");

        // 演示 ConcurrentHashMap 读不加锁
        ConcurrentHashMap<String, String> chmDemo = new ConcurrentHashMap<>();
        chmDemo.put("key", "value");
        System.out.println("    chm.get(\"key\") → 不会阻塞，其他线程可以同时读！");

        // ============================================================
        //  Q8: ConcurrentHashMap 线程安全的具体实现
        // ============================================================
        System.out.println("\n┌─────────────────────────────────────────────────────────┐");
        System.out.println("│ Q8: ConcurrentHashMap 底层线程安全实现                  │");
        System.out.println("└─────────────────────────────────────────────────────────┘");

        System.out.println("\n  JDK 1.7：分段锁 Segment（每个 Segment 独立锁）");
        System.out.println("  ┌──────────────────────────────────────────────────┐");
        System.out.println("  │ ConcurrentHashMap                                │");
        System.out.println("  │  ┌────────┬────────┬────────┬────────┐           │");
        System.out.println("  │  │Segment0│Segment1│Segment2│Segment3│  (默认16) │");
        System.out.println("  │  │ 锁     │ 锁     │ 锁     │ 锁     │           │");
        System.out.println("  │  │ ┌───┐  │ ┌───┐  │ ┌───┐  │ ┌───┐  │           │");
        System.out.println("  │  │ │桶 │  │ │桶 │  │ │桶 │  │ │桶 │  │           │");
        System.out.println("  │  │ └───┘  │ └───┘  │ └───┘  │ └───┘  │           │");
        System.out.println("  │  └────────┴────────┴────────┴────────┘           │");
        System.out.println("  │  写 Segment0 时，Segment1~3 不被锁，可以并发操作  │");
        System.out.println("  └──────────────────────────────────────────────────┘");
        System.out.println("  → 16 个锁，理想情况 16 个线程同时写不同 Segment");

        System.out.println("\n  JDK 1.8：CAS + synchronized 桶锁（去掉了 Segment）");
        System.out.println("  ┌──────────────────────────────────────────────────┐");
        System.out.println("  │ ConcurrentHashMap (JDK 8)                        │");
        System.out.println("  │  数组 table[]                                    │");
        System.out.println("  │  [桶0] [桶1] [桶2] ... [桶n]                     │");
        System.out.println("  │    ↑     ↑     ↑          ↑                      │");
        System.out.println("  │   sync  sync  sync       sync   ← 只锁操作的桶！ │");
        System.out.println("  └──────────────────────────────────────────────────┘");

        System.out.println("\n  [initTable] 初始化表：CAS 抢初始化权 + volatile 保证可见");
        System.out.println("    只有一个线程 CAS 成功 → 负责初始化数组");
        System.out.println("    其他线程 Thread.yield 让出 CPU，等初始化完成");
        System.out.println();
        System.out.println("  [put] 写操作：三步走");
        System.out.println("    1. 算 hash → 定位桶下标。桶为空 → CAS 直接写入（无锁快路径）");
        System.out.println("    2. 桶不为空 → synchronized(桶头节点) 加锁（只锁这一个桶！）");
        System.out.println("    3. 往链表/红黑树插入。链表 ≥ 8 → 转红黑树");
        System.out.println();
        System.out.println("  [get] 读操作：全程无锁！");
        System.out.println("    直接用 volatile 读 table[i]，保证可见性");
        System.out.println("    Node 的 val/next 也是 volatile，保证读到最新值");
        System.out.println("    → 这就是它比 Hashtable 快得多的核心原因！");
        System.out.println();
        System.out.println("  [扩容] transfer：多线程协同扩容");
        System.out.println("    每个线程认领一段桶区间帮忙搬数据");
        System.out.println("    不搬完的别的线程也能 put/get（读写不阻塞！）");
        System.out.println("    → Hashtable 扩容时全表冻结，ConcurrentHashMap 不停机！");

        System.out.println("\n  [size] 计数：CounterCell 机制（类似 LongAdder）");
        System.out.println("    多个线程写 baseCount（CAS）");
        System.out.println("    竞争激烈时每个线程往自己的 CounterCell 里加");
        System.out.println("    size() = 累加所有 CounterCell + baseCount");
        System.out.println("    → 避免了所有线程抢一个变量的瓶颈！");

        // ============================================================
        //  总结
        // ============================================================
        System.out.println("\n========================================");
        System.out.println("  总结");
        System.out.println("========================================");
        System.out.println();
        System.out.println("  1. List 有序可重复有索引, Set 不重复, Map 键值对");
        System.out.println("  2. 多数集合线程不安全，用 ConcurrentHashMap/CopyOnWriteArrayList");
        System.out.println("  3. HashSet=无序, LinkedHashSet=插入顺序, TreeSet=排序");
        System.out.println("  4. HashMap 快但非安全, Hashtable 老且慢, ConcurrentHashMap 最佳");
        System.out.println("  5. HashMap = 数组+链表+红黑树, 负载0.75, 链表>8转树");
        System.out.println("  6. 容量2的幂 = 位运算替代取模, 速度快分布匀");
        System.out.println("  7. ConcurrentHashMap(JDK8) = CAS + 桶锁, 读无锁, 扩容并发");
        System.out.println("  8. Hashtable 全表锁读也锁 → 不推荐");
        System.out.println("========================================");
    }
}
