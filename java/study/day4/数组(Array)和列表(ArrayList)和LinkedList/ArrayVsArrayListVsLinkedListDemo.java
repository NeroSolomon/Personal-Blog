/*
 * ============================================================
 *  通俗理解 数组(Array)、ArrayList、LinkedList 的区别
 * ============================================================
 *
 *  用生活场景打比方：
 *
 *  Array      =  电影院一排固定座椅（大小不可变）
 *      - 一排10个座位，建好就10个，不能加座位
 *      - 第5个座位，走几步就到 → 查找超快 O(1)
 *      - 想在中间加个座位？抱歉，整个排都得重新装修！
 *
 *  ArrayList  =  可伸缩的一排储物柜（自动扩容）
 *      - 默认10个柜子，满了自动在后面扩建1.5倍
 *      - 第3个柜子→直接打开，查得快 O(1)
 *      - 中间塞个柜子→后面所有柜子都得往右推 → 慢 O(n)
 *      - 删中间一个柜子→后面都得往左挪 → 慢 O(n)
 *
 *  LinkedList =  手拉手的一队小朋友（双向链表）
 *      - 每个人只记得自己的前面是谁、后面是谁
 *      - 找第7个小朋友→得从第1个开始数 → 慢 O(n)
 *      - 在第3和第4个之间插一个人→只要两个人松手重新牵 → 快 O(1)
 *      - 删掉某个小朋友→前后两个直接牵手 → 快 O(1)
 *
 *  一图看懂底层结构：
 *
 *  Array:      [□][□][□][□][□]  ← 一块连续内存，大小固定
 *  ArrayList:  [□][□][□][□][□]...[□][□]  ← 连续内存，自动扩容
 *  LinkedList: [□]→[□]→[□]→[□]   ← 每个节点存数据和前后指针
 *               ↑ ← ↑ ← ↑ ← ↑
 *
 *  ┌───────────────────────────────────────────────────────────┐
 *  │ 操作            Array        ArrayList     LinkedList      │
 *  ├───────────────────────────────────────────────────────────┤
 *  │ 按索引查        O(1) 最快    O(1)          O(n) 慢        │
 *  │ 尾部增删        N/A (固定)   O(1)*         O(1)           │
 *  │ 中间增删        N/A 不行     O(n) 慢       O(1) 快        │
 *  │ 内存           连续/省      连续/浪费空间  非连续/多指针   │
 *  │ 大小可变        ✗            ✓             ✓              │
 *  │ 存基本类型      ✓            ✗ (需包装)    ✗ (需包装)     │
 *  └───────────────────────────────────────────────────────────┘
 *
 *  一句话总结：
 *    要改大小 → 别用 Array
 *    要频繁按索引查 → ArrayList
 *    要频繁中间增删 → LinkedList
 */

import java.util.*;

public class ArrayVsArrayListVsLinkedListDemo {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  Array vs ArrayList vs LinkedList");
        System.out.println("========================================");

        // =============================================================
        //  一、Array：电影院固定座椅 —— 大小不可变
        // =============================================================
        System.out.println("\n========== 一、Array：固定座椅（大小不可变） ==========");

        // 创建：必须指定大小，定死！
        String[] seats = new String[5];
        seats[0] = "张三";
        seats[1] = "李四";
        seats[2] = "王五";
        seats[3] = "赵六";
        seats[4] = "孙七";

        System.out.println("  一排 " + seats.length + " 个固定座位:");
        for (int i = 0; i < seats.length; i++) {
            System.out.println("    第" + (i + 1) + "个座位: " + seats[i]);
        }

        System.out.println("  第3个座位上是: " + seats[2]);   // O(1)，直接定位！
        System.out.println("  → 按编号找人，一步到位 O(1)，最快！");
        System.out.println("  → 但想加第6个人？抱歉，座位数是死的，没法加！");

        // 想要扩容？只能重建一个大数组，把旧数据拷过去
        String[] newSeats = new String[10];           // 建个大的
        System.arraycopy(seats, 0, newSeats, 0, seats.length); // 全部拷过去
        newSeats[5] = "周八";
        System.out.println("  扩容后（笨办法）：第6个座位: " + newSeats[5]);
        System.out.println("  → 扩容要手动建新数组+拷贝，麻烦！");

        // =============================================================
        //  二、ArrayList：可伸缩储物柜 —— 自动扩容
        // =============================================================
        System.out.println("\n========== 二、ArrayList：可伸缩储物柜（自动扩容） ==========");

        // 创建：不用指定大小，想加就加！
        ArrayList<String> lockers = new ArrayList<>();
        lockers.add("张三");    // 加到末尾
        lockers.add("李四");
        lockers.add("王五");
        lockers.add("赵六");
        lockers.add("孙七");
        System.out.println("  储物柜: " + lockers);
        System.out.println("  当前 " + lockers.size() + " 个");

        // 按索引查 → O(1)，底层是数组，和 Array 一样快
        System.out.println("  第3个柜子: " + lockers.get(2));
        System.out.println("  → 和 Array 一样，按索引查 O(1)，超快！");

        // 扩容自动完成
        for (int i = 6; i <= 15; i++) {
            lockers.add("用户" + i);
        }
        System.out.println("  自动加了10个人后，一共: " + lockers.size() + " 人");
        System.out.println("  → Array 才做不到自动扩容，ArrayList 内部帮你干了！");

        // 中间插入 → O(n)，后面的人全得挪
        System.out.println("\n  [演示] 在第2个位置（索引1）插入「插队者」:");
        System.out.println("    插入前: " + lockers.subList(0, 6));
        lockers.add(1, "插队者");
        System.out.println("    插入后: " + lockers.subList(0, 7));
        System.out.println("  → 虽然用起来方便，但底层是「后面全往后挪一格」，慢 O(n)");

        // 中间删除 → O(n)，后面的人全得挪
        System.out.println("\n  [演示] 删除第2个「插队者」:");
        lockers.remove(1);
        System.out.println("    删除后: " + lockers.subList(0, 6));
        System.out.println("  → 删掉中间一个，后面全部要往前挪，也是 O(n)");

        // =============================================================
        //  三、LinkedList：手拉手小朋友 —— 增删快，查找慢
        // =============================================================
        System.out.println("\n========== 三、LinkedList：手拉手小朋友（增删快、查找慢） ==========");

        // 创建
        LinkedList<String> kids = new LinkedList<>();
        kids.add("小红");
        kids.add("小明");
        kids.add("小刚");
        kids.add("小芳");
        kids.add("小华");
        System.out.println("  一队小朋友: " + kids);

        // 按索引查 → O(n)，得从第一个开始数
        System.out.println("  找第3个小朋友...得从第1个开始数: " + kids.get(2));
        System.out.println("  → 没有索引直通车，得一个一个数过去，O(n) 慢！");

        // 头尾操作 → O(1)，LinkedList 的强项
        System.out.println("\n  LinkedList 的头尾操作是 O(1)：");
        kids.addFirst("新来的");    // 加在队伍最前面
        kids.addLast("插队的");   // 加在队伍最后面
        System.out.println("    addFirst + addLast 后: " + kids);
        System.out.println("    队伍最前面: " + kids.getFirst());
        System.out.println("    队伍最后面: " + kids.getLast());
        System.out.println("  → addFirst/getFirst/addLast/getLast 全是 O(1)！");

        // 中间插入 → O(1)，只需改两个指针
        System.out.println("\n  [演示] 在小明和小刚之间插一个人:");
        System.out.println("    插入前: " + kids);
        ListIterator<String> lit = kids.listIterator();
        while (lit.hasNext()) {
            if (lit.next().equals("小明")) {
                lit.add("插班生");    // 在当前位置插入，O(1)！
                break;
            }
        }
        System.out.println("    插入后: " + kids);
        System.out.println("  → LinkedList 中间插入只需改两个指针，O(1)！ArrayList 做不到！");

        // 中间删除 → O(1)，只需断链重连
        System.out.println("\n  [演示] 把「插班生」删掉:");
        kids.remove("插班生");     // 删掉一个，只需前后两个重新牵手
        System.out.println("    删除后: " + kids);
        System.out.println("  → LinkedList 中间删除也是 O(1)！");

        // =============================================================
        //  四、性能实测对比
        // =============================================================
        System.out.println("\n========== 四、性能实测：数据说话 ==========");

        final int TEST_SIZE = 100000;

        // === 按尾追加 ===
        System.out.println("\n  [测试1] 尾部追加 " + TEST_SIZE + " 个元素");
        long start = System.currentTimeMillis();
        List<Integer> arrayList = new ArrayList<>();
        for (int i = 0; i < TEST_SIZE; i++) arrayList.add(i);
        long arrayListTail = System.currentTimeMillis() - start;

        start = System.currentTimeMillis();
        List<Integer> linkedList = new LinkedList<>();
        for (int i = 0; i < TEST_SIZE; i++) linkedList.add(i);
        long linkedListTail = System.currentTimeMillis() - start;

        System.out.println("    ArrayList.add  : " + arrayListTail + " ms  (尾部 O(1))");
        System.out.println("    LinkedList.add : " + linkedListTail + " ms  (尾部 O(1))");

        // === 按索引随机查 ===
        System.out.println("\n  [测试2] 随机按索引查找 10000 次");
        Random rand = new Random(42);
        start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            arrayList.get(rand.nextInt(TEST_SIZE));
        }
        long arrayListRandom = System.currentTimeMillis() - start;

        rand = new Random(42);
        start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            linkedList.get(rand.nextInt(TEST_SIZE));
        }
        long linkedListRandom = System.currentTimeMillis() - start;

        System.out.println("    ArrayList.get  : " + arrayListRandom + " ms  (O(1)) ← 快得多！");
        System.out.println("    LinkedList.get : " + linkedListRandom + " ms  (O(n)) ← 慢得多！");

        // === 头部插入 ===
        System.out.println("\n  [测试3] 头部插入 10000 个元素");
        start = System.currentTimeMillis();
        List<Integer> al2 = new ArrayList<>();
        for (int i = 0; i < 10000; i++) al2.add(0, i);
        long arrayListHead = System.currentTimeMillis() - start;

        start = System.currentTimeMillis();
        List<Integer> ll2 = new LinkedList<>();
        for (int i = 0; i < 10000; i++) ll2.add(0, i);
        long linkedListHead = System.currentTimeMillis() - start;

        System.out.println("    ArrayList.add(0, v)  : " + arrayListHead + " ms  (O(n)) ← 慢！");
        System.out.println("    LinkedList.add(0, v) : " + linkedListHead + " ms  (O(1)) ← 快！");

        // =============================================================
        //  五、LinkedList 也能当队列和栈
        // =============================================================
        System.out.println("\n========== 五、LinkedList 的特殊技能：当队列和栈 ==========");

        // 当队列用：FIFO 先进先出（排队）
        System.out.println("\n  [队列模式] FIFO 先进先出:");
        Queue<String> queue = new LinkedList<>();
        queue.offer("顾客1");   // 入队
        queue.offer("顾客2");
        queue.offer("顾客3");
        System.out.println("    队伍: " + queue);
        System.out.println("    服务: " + queue.poll());   // 出队，第一个进来先走
        System.out.println("    服务: " + queue.poll());
        System.out.println("    剩余: " + queue);

        // 当栈用：LIFO 后进先出（叠盘子）
        System.out.println("\n  [栈模式] LIFO 后进先出:");
        Deque<String> stack = new LinkedList<>();
        stack.push("盘子1");   // 压栈
        stack.push("盘子2");
        stack.push("盘子3");
        System.out.println("    栈: " + stack);
        System.out.println("    拿最上面的: " + stack.pop());  // 弹栈，最后一个放的最先拿
        System.out.println("    拿最上面的: " + stack.pop());
        System.out.println("    剩余: " + stack);

        System.out.println("  → LinkedList 实现了 Queue 和 Deque 接口，");
        System.out.println("    既能当队列(FIFO)又能当栈(LIFO)！ArrayList 没这本事。");

        // =============================================================
        //  总结
        // =============================================================
        System.out.println("\n========================================");
        System.out.println("  总结");
        System.out.println("========================================");
        System.out.println("               Array        ArrayList    LinkedList");
        System.out.println("  底层         固定数组      动态数组     双向链表");
        System.out.println("  大小可变       ✗            ✓            ✓");
        System.out.println("  按索引查      O(1) 最快    O(1) 最快    O(n) 慢");
        System.out.println("  尾部增删      ✗ 不可        O(1)*       O(1)");
        System.out.println("  中间增删      ✗ 不可        O(n) 慢     O(1) 快");
        System.out.println("  存基本类型     ✓            ✗ (包装)     ✗ (包装)");
        System.out.println("  当队列/栈     ✗            ✗            ✓");
        System.out.println("========================================");
        System.out.println("选哪个？");
        System.out.println("  大小固定+基本类型          → Array");
        System.out.println("  要频繁按索引查，偶尔增删    → ArrayList（最常用）");
        System.out.println("  要频繁头尾/中间增删，不按索引查 → LinkedList");
        System.out.println("  要当队列或栈用              → LinkedList");
        System.out.println("========================================");
        System.out.println("速记口诀：");
        System.out.println("  Array 固定不能变，查得快但不能添；");
        System.out.println("  ArrayList 自动长，查快插慢最常用到；");
        System.out.println("  LinkedList 手拉手，插删飞快查找愁；");
        System.out.println("  还能变身队列和栈，头尾操作它最强。");
    }
}
