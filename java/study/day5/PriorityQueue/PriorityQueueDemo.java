/*
 * ============================================================
 *  通俗理解 PriorityQueue（优先队列）
 * ============================================================
 *
 *  用生活场景打比方：
 *
 *  普通队列 Queue   =  奶茶店排队（先进先出 FIFO）
 *      先来的先买，后来的后面排着。管你多急，都按顺序。
 *
 *  优先队列 PriorityQueue  =  医院急诊室（谁病重优先级高，谁先看）
 *      一位心梗患者虽然比你晚来，但会插到你前面。
 *      不管排多久，病最重的永远排在最前面！
 *
 *  底层结构 — 二叉堆（小顶堆）：
 *
 *         [1分]          ← 堆顶永远是最小值
 *        /     \
 *     [3分]   [5分]      ← 左右没大小关系，
 *    /   \    /   \         只保证 父 ≤ 子
 *  [8]  [9] [6]  [10]
 *
 *  每次 poll() 把堆顶拿走，然后自动调整，新堆顶又是剩下里最小的。
 *
 *  ┌─────────────────────────────────────────────────┐
 *  │  特性              PriorityQueue                 │
 *  ├─────────────────────────────────────────────────┤
 *  │  出队顺序         按优先级（默认最小优先）        │
 *  │  底层             二叉堆（小顶堆/大顶堆）         │
 *  │  插入 add/offer   O(log n)                      │
 *  │  查看 peek        O(1)                          │
 *  │  取出 poll        O(log n)                      │
 *  │  删除指定元素      O(n)                          │
 *  │  允许重复         ✓                              │
 *  │  允许 null        ✗                              │
 *  │  线程安全         ✗                              │
 *  │  遍历            不保证顺序输出                  │
 *  └─────────────────────────────────────────────────┘
 *
 *  一句话总结：
 *    Queue 讲究先来后到，PriorityQueue 讲究谁更“要紧”谁先上。
 */

import java.util.*;

public class PriorityQueueDemo {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  PriorityQueue —— 优先队列演示");
        System.out.println("========================================");

        // =============================================================
        //  一、基础用法：默认小顶堆（数字越小越优先）
        // =============================================================
        System.out.println("\n========== 一、基础：默认小顶堆，数字越小越优先 ==========");

        PriorityQueue<Integer> pq = new PriorityQueue<>();
        pq.offer(5);
        pq.offer(1);
        pq.offer(8);
        pq.offer(3);
        pq.offer(2);
        System.out.println("  放入顺序: 5, 1, 8, 3, 2");
        System.out.println("  pq 内部: " + pq + "  ← 看上往不是排序的（堆不是有序数组）");

        System.out.print("  按优先级取出: ");
        while (!pq.isEmpty()) {
            System.out.print(pq.poll() + " ");   // 1, 2, 3, 5, 8 从小到大
        }
        System.out.println();
        System.out.println("  → 不管放入顺序，取出时始终是最小的先出来！");
        System.out.println("  → 就像急诊室：谁病最重谁先看，跟来没来早晚没关系。");

        // =============================================================
        //  1.5、图解：poll 后堆怎么变？
        // =============================================================
        System.out.println("\n========== 图解：poll() 拿走堆顶后，堆怎么变？ ==========");
        System.out.println();
        System.out.println("  假设堆里现在是: 5, 1, 8, 3, 2");
        System.out.println();
        System.out.println("  小白话流程：");
        System.out.println("    1) 往堆里塞完数据后，内部长这样（小顶堆，父 ≤ 子）：");
        System.out.println();
        System.out.println("              [1]           ← 堆顶永远最小");
        System.out.println("             /   \\");
        System.out.println("          [2]     [8]       ← 每层只保证父比子小");
        System.out.println("          /  \\               不保证同一层有序");
        System.out.println("        [5]  [3]");
        System.out.println();

        System.out.println("  ─── 第1次 poll()：拿走堆顶 1 ───");
        System.out.println();
        System.out.println("    步骤① 拿走 1（返回给调用者）");
        System.out.println("    步骤② 把最后一个元素 3 拎到堆顶，变成：");
        System.out.println();
        System.out.println("              [3]           ← 3 暂居堆顶（但它不配）");
        System.out.println("             /   \\");
        System.out.println("          [2]     [8]");
        System.out.println("          /");
        System.out.println("        [5]");
        System.out.println();
        System.out.println("    步骤③ 下沉(sink)：3 和两个儿子 [2, 8] 比，2 更小 → 交换！");
        System.out.println();
        System.out.println("              [2]           ← 下沉后 2 升上来到堆顶");
        System.out.println("             /   \\");
        System.out.println("          [3]     [8]");
        System.out.println("          /");
        System.out.println("        [5]");
        System.out.println();
        System.out.println("    步骤④ 3 再和儿子 [5] 比，3 < 5，不用换了 → 结束。新堆顶=2");

        System.out.println();
        System.out.println("  ─── 第2次 poll()：拿走堆顶 2 ───");
        System.out.println();
        System.out.println("    步骤① 拿走 2");
        System.out.println("    步骤② 把最后一个元素 5 拎到堆顶：");
        System.out.println();
        System.out.println("              [5]           ← 5 上到堆顶（不配）");
        System.out.println("             /   \\");
        System.out.println("          [3]     [8]");
        System.out.println();
        System.out.println("    步骤③ 下沉：5 和儿子 [3, 8] 比，3 更小 → 交换！");
        System.out.println();
        System.out.println("              [3]           ← 3 上到堆顶");
        System.out.println("             /   \\");
        System.out.println("          [5]     [8]");
        System.out.println();
        System.out.println("    步骤④ 5 没有儿子了 → 结束。新堆顶=3");

        System.out.println();
        System.out.println("  ─── 第3次 poll()：拿走堆顶 3 ───");
        System.out.println();
        System.out.println("    步骤① 拿走 3");
        System.out.println("    步骤② 把最后一个元素 8 拎到堆顶：");
        System.out.println();
        System.out.println("              [8]           ← 8 在堆顶");
        System.out.println("             /");
        System.out.println("          [5]");
        System.out.println();
        System.out.println("    步骤③ 下沉：8 和儿子 [5] 比，5 更小 → 交换！");
        System.out.println();
        System.out.println("              [5]");
        System.out.println("             /");
        System.out.println("          [8]");
        System.out.println();
        System.out.println("    步骤④ 结束 → 新堆顶=5。后面同理，直到堆空。");
        System.out.println();
        System.out.println("  核心规则：");
        System.out.println("    poll = 拿走堆顶 → 末尾补位 → 不停和最小儿子换 → 直到满足「父 ≤ 子」");
        System.out.println("    整个过程就是「末尾元素的下沉之旅」！");

        System.out.println();
        System.out.println("  ─── 性能会不会很低？不用全部重排！ ───");
        System.out.println();
        System.out.println("    很多人以为：每次 poll 后剩下的元素要重新排序 → O(n log n) 那还得了？");
        System.out.println();
        System.out.println("    真相：只动了「一条路径」，其他分支纹丝不动！");
        System.out.println();
        System.out.println("    以刚才的堆为例，100 个元素的堆，poll 一次只比了 2 次：");
        System.out.println();
        System.out.println("            [3]  ← 下沉起点");
        System.out.println("           /   \\");
        System.out.println("      [2]       [8]     ← 第1层比较：3 vs (2,8)，和 2 换");
        System.out.println("      /  \\       /  \\");
        System.out.println("   [5]  [9]   [6]  [10] ← 第2层比较：3 vs 5，不用换了 ↓");
        System.out.println();
        System.out.println("    下沉路径上每层只比 1 次，树高 = log₂n = 7（1亿数据也只要比 27 次）");
        System.out.println();
        System.out.println("    对比：");
        System.out.println("      假设堆里有 100 万个元素：");
        System.out.println("      全排序 O(n log n)   ≈ 100万 × 20 = 2000万次比较");
        System.out.println("      堆 poll O(log n)    ≈ 20 次比较           ↓ 差 100 万倍！");
        System.out.println();
        System.out.println("    一句话：");
        System.out.println("      完全二叉树的高度就是对数级，下沉路径只有一条，");
        System.out.println("      所以 poll 是 O(log n) 而不是 O(n log n)，完全不需要全重排。");

        // =============================================================
        //  二、医院急诊室：自定义优先级
        // =============================================================
        System.out.println("\n========== 二、医院急诊室：按病情严重度排队 ==========");

        // 患者类
        class Patient {
            String name;
            int severity;   // 病情严重度 1~10，数字越大越严重

            Patient(String name, int severity) {
                this.name = name;
                this.severity = severity;
            }

            @Override
            public String toString() {
                return name + "(严重度" + severity + ")";
            }
        }

        // 大顶堆：严重度越高的越优先
        PriorityQueue<Patient> er = new PriorityQueue<>(
            (p1, p2) -> p2.severity - p1.severity  // 降序，严重的排前面
        );

        er.offer(new Patient("普通感冒",  2));
        er.offer(new Patient("骨折",      7));
        er.offer(new Patient("轻微擦伤",  1));
        er.offer(new Patient("心梗",      10));
        er.offer(new Patient("高烧",      5));
        System.out.println("  患者挂号的先后: 感冒(2) → 骨折(7) → 擦伤(1) → 心梗(10) → 高烧(5)");
        System.out.println("  但医生看的顺序是:");

        int order = 1;
        while (!er.isEmpty()) {
            System.out.println("    第" + order + "个看诊: " + er.poll());
            order++;
        }
        System.out.println("  → 心梗虽然晚来，但优先级最高，第一个看！");
        System.out.println("  → 这就是优先队列的核心：按紧急程度安排顺序。");

        // =============================================================
        //  三、大顶堆 vs 小顶堆
        // =============================================================
        System.out.println("\n========== 三、大顶堆 vs 小顶堆 ==========");

        // 小顶堆（默认）：最小的先出
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();
        // 大顶堆：最大的先出，用 Collections.reverseOrder()
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());

        for (int n : new int[]{3, 1, 4, 1, 5, 9, 2, 6}) {
            minHeap.offer(n);
            maxHeap.offer(n);
        }

        System.out.print("  小顶堆出队（从小到大）: ");
        while (!minHeap.isEmpty()) System.out.print(minHeap.poll() + " ");
        System.out.println();
        System.out.print("  大顶堆出队（从大到小）: ");
        while (!maxHeap.isEmpty()) System.out.print(maxHeap.poll() + " ");
        System.out.println();

        System.out.println("\n  偷懒写法：");
        System.out.println("    小顶堆 = new PriorityQueue<>()");
        System.out.println("    大顶堆 = new PriorityQueue<>(Collections.reverseOrder())");
        System.out.println("    或者  = new PriorityQueue<>((a, b) -> b - a)");

        // =============================================================
        //  四、实际应用场景
        // =============================================================
        System.out.println("\n========== 四、实际应用场景 ==========");

        // 场景1：Top K 问题 —— 找第 K 大 / 前 K 大
        System.out.println("\n  [场景1] Top K：找出成绩最高的 3 个学生");
        int[] scores = {78, 92, 85, 66, 97, 55, 88, 73, 91, 60};
        System.out.println("    成绩列表: " + Arrays.toString(scores));
        System.out.println("    思路：用小顶堆维护前3大，堆顶就是第3大的门槛");

        // 小顶堆，只留最大的 3 个
        PriorityQueue<Integer> top3 = new PriorityQueue<>();
        for (int s : scores) {
            top3.offer(s);
            if (top3.size() > 3) {
                top3.poll();  // 把最小的那个挤掉
            }
        }
        System.out.println("    前3大成绩: " + top3);
        System.out.println("  → 堆大小始终保持 K=3，新来的比堆顶大就替换，最终得到前K大");

        // 场景2：任务调度 —— 按优先级执行
        System.out.println("\n  [场景2] 任务调度：每个任务有优先级，高的先执行");
        class Task {
            String name;
            int priority;  // 数字越大越优先

            Task(String name, int priority) {
                this.name = name;
                this.priority = priority;
            }

            @Override
            public String toString() {
                return name + "(优先级" + priority + ")";
            }
        }

        PriorityQueue<Task> scheduler = new PriorityQueue<>(
            (a, b) -> b.priority - a.priority
        );
        scheduler.offer(new Task("发工资",      10));
        scheduler.offer(new Task("修打印机",    2));
        scheduler.offer(new Task("回复邮件",    5));
        scheduler.offer(new Task("服务器宕机",  9));
        scheduler.offer(new Task("倒垃圾",      1));

        System.out.println("    执行顺序:");
        int seq = 1;
        while (!scheduler.isEmpty()) {
            System.out.println("      " + seq + "." + scheduler.poll());
            seq++;
        }
        System.out.println("  → 服务器宕机紧急性仅次于发工资，比垃圾邮件优先执行！");

        // 场景3：合并 K 个有序链表的思想
        System.out.println("\n  [场景3] 合并多个有序数据流");
        int[] stream1 = {1, 4, 7, 10};
        int[] stream2 = {2, 5, 8, 11};
        int[] stream3 = {3, 6, 9, 12};
        System.out.println("    流1: " + Arrays.toString(stream1));
        System.out.println("    流2: " + Arrays.toString(stream2));
        System.out.println("    流3: " + Arrays.toString(stream3));

        // 把每个流的第一个元素放入堆
        PriorityQueue<Integer> merge = new PriorityQueue<>();
        for (int v : stream1) merge.offer(v);
        for (int v : stream2) merge.offer(v);
        for (int v : stream3) merge.offer(v);

        System.out.print("    合并后: ");
        while (!merge.isEmpty()) System.out.print(merge.poll() + " ");
        System.out.println();
        System.out.println("  → 把多条有序数据全扔进堆，poll 出来就是全局有序！");

        // =============================================================
        //  五、注意事项
        // =============================================================
        System.out.println("\n========== 五、注意事项（踩坑点） ==========");

        // 注意1：遍历不保证顺序
        System.out.println("\n  [注意1] 遍历 ≠ 有序取出！");
        PriorityQueue<Integer> pq2 = new PriorityQueue<>();
        pq2.offer(3);
        pq2.offer(1);
        pq2.offer(4);
        pq2.offer(2);
        System.out.println("    for-each 遍历: ");
        for (int n : pq2) {
            System.out.println("      " + n + "  ← 顺序不确定！");
        }
        System.out.print("    poll() 取: ");
        while (!pq2.isEmpty()) System.out.print(pq2.poll() + " ");
        System.out.println();
        System.out.println("  → 只有 poll() 才保证优先级顺序，for-each 不保证！");

        // 注意2：不能存 null
        System.out.println("\n  [注意2] PriorityQueue 不能存 null");
        try {
            PriorityQueue<String> pq3 = new PriorityQueue<>();
            pq3.offer(null);
        } catch (NullPointerException e) {
            System.out.println("    ❌ pq.offer(null) → NullPointerException！");
        }

        // 注意3：存的对象必须可比较（实现 Comparable），或提供 Comparator
        System.out.println("\n  [注意3] 存入的对象必须可比较");
        class Dog {
            String name;

            Dog(String name) {
                this.name = name;
            }
        }
        try {
            PriorityQueue<Dog> dogs = new PriorityQueue<>();
            dogs.offer(new Dog("旺财"));
            dogs.offer(new Dog("来福"));
            // 到这里还不报错...因为第一个元素不需要比较
            dogs.offer(new Dog("大黄"));
            // 第二个元素需要比较 → 炸了！
        } catch (ClassCastException e) {
            System.out.println("    ❌ ClassCastException！Dog 没有实现 Comparable");
            System.out.println("    → 解决办法：给 PriorityQueue 传一个 Comparator");
        }
        System.out.println("    正确写法: new PriorityQueue<>((d1, d2) -> d1.name.compareTo(d2.name))");

        // =============================================================
        //  总结
        // =============================================================
        System.out.println("\n========================================");
        System.out.println("  总结");
        System.out.println("========================================");
        System.out.println("                                        PriorityQueue     普通 Queue");
        System.out.println("  出队规则     按优先级               先进先出 FIFO");
        System.out.println("  默认顺序     最小先出（小顶堆）      谁先来谁先出");
        System.out.println("  底层结构     二叉堆                 链表/数组");
        System.out.println("  peek         看堆顶 O(1)           看队头 O(1)");
        System.out.println("  add/offer    入堆 O(log n)         入队 O(1)");
        System.out.println("  poll         出堆 O(log n)         出队 O(1)");
        System.out.println("========================================");
        System.out.println("什么场景用？");
        System.out.println("  Top K 问题                    → PriorityQueue 小顶堆");
        System.out.println("  任务调度/事件处理              → PriorityQueue 自定义比较器");
        System.out.println("  合并有序数据流                → PriorityQueue");
        System.out.println("  求中位数/数据流中位数          → 两个堆（最大堆+最小堆）");
        System.out.println("  迪杰斯特拉算法                → PriorityQueue 小顶堆");
        System.out.println("========================================");
        System.out.println("速记口诀：");
        System.out.println("  普通队列先来先走，优先队列病重先救；");
        System.out.println("  二叉堆顶是答案，取走调整找下一位；");
        System.out.println("  Top K 问题用它解，任务调度它最溜。");
    }
}
