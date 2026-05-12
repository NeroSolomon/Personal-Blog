/*
 * ============================================================
 *  通俗理解大 O 符号 —— 算法效率的尺子
 * ============================================================
 *
 *  用生活场景打比方：
 *
 *  大 O 不是精确计时器，而是 "数据量翻倍时，时间怎么涨" 的增速曲线。
 *  就像你问 "去北京要多久？" 大 O 不告诉你具体几小时，
 *  而是告诉你 "走路去是 O(距离)、坐飞机是 O(1)"。
 *
 *  不要被数学吓到，大 O 只回答一个问题：
 *    "当数据量 n 变得很大很大时，这个算法撑得住吗？"
 *
 *  一张图记住所有复杂度（n 变大时的时间增长趋势）：
 *
 *  O(1)        ████████████████████  → 不管 n 多大，恒定
 *  O(log n)    ████████████████████  → 增长极慢，n 翻一倍，只多一步
 *  O(n)        ████████████████████  → 线性增长
 *  O(n log n)  ████████████████████  → 比 n 稍快一点
 *  O(n²)       ████████████████████  → n 翻倍，时间翻 4 倍！
 *  O(2ⁿ)       ████████████████████  → 指数爆炸，n=50 宇宙都算不完
 *
 *  ┌──────────────────────────────────────────────────────────┐
 *  │  n=10     n=100    n=1000   n=10000   名称               │
 *  ├──────────────────────────────────────────────────────────┤
 *  │  1        1        1        1         O(1)    常数时间   │
 *  │  3.3      6.6      10       13        O(log n) 对数     │
 *  │  10       100      1000     10000     O(n)    线性      │
 *  │  33       664      9966     132877    O(n log n) 线性对数│
 *  │  100      10000    百万     亿         O(n²)   平方     │
 *  │  2^10     2^100    天文数字  宇宙爆炸  O(2ⁿ)   指数     │
 *  └──────────────────────────────────────────────────────────┘
 *
 *  计算规则（核心！）：
 *    1. 只保留增长最快的项（n² + n → n²）
 *    2. 忽略常数系数（3n → n,  2n² → n²）
 *    3. 循环嵌套 → 复杂度相乘
 *    4. 循环并列 → 取最大
 *    5. 递归看分支数（二分递归=2ⁿ，每次减半=log n）
 *
 *  一句话总结：
 *    大 O 描述的是 "最坏情况下，随着 n 增大，时间的增长快慢"。
 */

import java.util.*;

public class BigODemo {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  大O符号 —— 算法效率的尺子");
        System.out.println("========================================");

        // =============================================================
        //  0、核心思想：大 O 测什么？
        // =============================================================
        System.out.println("\n========== 0、核心思想：大 O 到底在量什么？ ==========");
        System.out.println();
        System.out.println("  假设你写了个算法，在 1000 条数据上跑，用时 2 秒。");
        System.out.println("  老板问：那 100万条数据要多久？1000万呢？");
        System.out.println();
        System.out.println("  你不能靠「测一次」来回答，大 O 就是回答这个问题的数学工具：");
        System.out.println("    O(1)   → 1000万条也瞬间搞定");
        System.out.println("    O(n)   → 数据翻10倍，时间也翻10倍");
        System.out.println("    O(n²)  → 数据翻10倍，时间翻100倍！要炸了");
        System.out.println();
        System.out.println("  一句话：大 O 不看具体秒数，看「数据翻倍时时间怎么涨」。");

        // =============================================================
        //  一、O(1) 常数时间 —— 一步到位
        // =============================================================
        System.out.println("\n========== 一、O(1) 常数时间 —— 一步到位 ==========");
        System.out.println();
        System.out.println("  比喻：你有一本通讯录，直接翻到「张三」那一页 → 不管");
        System.out.println("        通讯录有 100 人还是 100 万人，找张三就翻一次。");
        System.out.println();

        int[] arr = {10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
        System.out.println("  例子① 数组按下标取元素: arr[" + 3 + "] = " + arr[3]);
        System.out.println("    → 不管数组多大，arr[i] 都是 O(1)");
        System.out.println();
        System.out.println("  例子② HashMap 取值: map.get(key)");
        HashMap<String, String> map = new HashMap<>();
        map.put("张三", "138xxxx");
        System.out.println("    张三的电话: " + map.get("张三") + "  → O(1)");
        System.out.println();
        System.out.println("  例子③ 简单运算: 1+2, a==b, 都是 O(1)");
        System.out.println();
        System.out.println("  识别口诀：没有循环、没有递归 → O(1)");

        // =============================================================
        //  二、O(log n) 对数时间 —— 每次砍一半
        // =============================================================
        System.out.println("\n========== 二、O(log n) 对数时间 —— 每次砍一半 ==========");
        System.out.println();
        System.out.println("  比喻：在一本 1000 页的字典里查一个单词。你不从第 1 页翻，");
        System.out.println("        而是翻到中间：靠前就在左半找，靠后就在右半找。");
        System.out.println("        每次砍掉一半 → 1000 页最多翻 10 次。");
        System.out.println();

        System.out.println("  例子① 二分查找：猜数字游戏");
        int[] sorted = {1, 3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23, 25, 27, 29};
        int target = 19;
        int left = 0, right = sorted.length - 1, steps = 0;
        System.out.println("    在 " + Arrays.toString(sorted) + " 中找 " + target);
        while (left <= right) {
            steps++;
            int mid = (left + right) / 2;
            if (sorted[mid] == target) {
                System.out.println("    找到了！位置=" + mid + "，用了 " + steps + " 步");
                break;
            } else if (sorted[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        System.out.println("    → 15 个元素，log₂15 ≈ 4，果然 4 步找到。O(log n)！");
        System.out.println("      如果是 10 亿个元素，log₂(10亿) ≈ 30 步，也是秒杀！");

        System.out.println();
        System.out.println("  例子② 二叉堆的 offer/poll：PriorityQueue");
        System.out.println("    堆是二叉树，高 log₂n，上浮/下沉每层只比一次 → O(log n)");
        System.out.println();
        System.out.println("  识别口诀：每步能把问题规模缩小一半 → O(log n)");

        // =============================================================
        //  三、O(n) 线性时间 —— 一个一个过
        // =============================================================
        System.out.println("\n========== 三、O(n) 线性时间 —— 一个一个过 ==========");
        System.out.println();
        System.out.println("  比喻：超市收银员扫码，100 件商品扫 100 下，");
        System.out.println("        商品翻倍时间就翻倍。");
        System.out.println();

        int[] nums = {8, 3, 6, 1, 9, 2, 7, 4, 5};

        System.out.println("  例子① 求和/找最大/遍历数组");
        int sum = 0;
        for (int n : nums) sum += n;
        System.out.println("    数组求和 = " + sum + "  → 一个 for → O(n)");

        System.out.println();
        System.out.println("  例子② 找最大值");
        int max = nums[0];
        for (int n : nums) if (n > max) max = n;
        System.out.println("    最大值 = " + max + "  → O(n)");

        System.out.println();
        System.out.println("  例子③ 并列循环 → 还是 O(n)，不是 2n！");
        for (int i = 0; i < nums.length; i++) { /* 第一遍 */ }
        for (int i = 0; i < nums.length; i++) { /* 第二遍 */ }
        System.out.println("    两个独立的 for，每个 O(n) → O(n) + O(n) = O(2n) → O(n)");
        System.out.println("    → 原则：忽略常数系数，2n, 3n, 100n 统统写成 O(n)");
        System.out.println();
        System.out.println("  识别口诀：一个 for 从头到尾 → O(n)");

        // =============================================================
        //  四、O(n log n) 线性对数 — 每次砍半 + 每层都遍历
        // =============================================================
        System.out.println("\n========== 四、O(n log n) 线性对数 —— 每次砍半 x 每层全遍历 ==========");
        System.out.println();
        System.out.println("  比喻：学校要按成绩排名。先把全校分成两半各自排，再合并。");
        System.out.println("        分 log n 层，每层都要过一遍所有人 → 总工作量 n × log n。");
        System.out.println();

        System.out.println("  经典代表：归并排序、快速排序(平均)、堆排序");
        System.out.println();
        System.out.println("  例子：用 Arrays.sort() 排序");
        int[] unsorted = {42, 23, 74, 11, 65, 58, 94, 36, 99, 87};
        System.out.println("    排前: " + Arrays.toString(unsorted));
        Arrays.sort(unsorted);
        System.out.println("    排后: " + Arrays.toString(unsorted));
        System.out.println("    → 底层 DualPivotQuickSort，平均 O(n log n)");
        System.out.println();
        System.out.println("  为什么是 n × log n？把数组当成二叉树来想：");
        System.out.println("    树高 = log n 层（每次分两半）");
        System.out.println("    每层合并时把 n 个元素各处理一次 → n");
        System.out.println("    总工作量 = n × log n");
        System.out.println();
        System.out.println("  n=10       → 10  log10  ≈ 33");
        System.out.println("  n=1000     → 1000 log1000 ≈ 9966     ← 比 O(n²) 好太多了");
        System.out.println("  n=1000000  → 100万×20 ≈ 2000万        ← 排序百万数据也很轻松");

        // =============================================================
        //  五、O(n²) 平方时间 —— 循环套循环
        // =============================================================
        System.out.println("\n========== 五、O(n²) 平方时间 —— 循环套循环 ==========");
        System.out.println();
        System.out.println("  比喻：全班 50 人，每个人和其他所有人握一次手 → 50×50=2500 次。");
        System.out.println("        人翻倍到 100，握手变成 10000 次（翻了 4 倍）。");
        System.out.println();

        System.out.println("  例子① 冒泡排序（两层 for）");
        int[] bubble = {5, 1, 3, 2, 4};
        System.out.println("    排前: " + Arrays.toString(bubble));
        int compares = 0;
        for (int i = 0; i < bubble.length; i++) {
            for (int j = 0; j < bubble.length - 1 - i; j++) {
                compares++;
                if (bubble[j] > bubble[j + 1]) {
                    int t = bubble[j];
                    bubble[j] = bubble[j + 1];
                    bubble[j + 1] = t;
                }
            }
        }
        System.out.println("    排后: " + Arrays.toString(bubble) + "，比较了 " + compares + " 次");
        System.out.println("    → O(n²)，5 个元素就比了 10 次，1000 个元素要 50 万次！");

        System.out.println();
        System.out.println("  例子② 双层 for 找重复（暴力法）");
        int[] dupArr = {1, 4, 3, 2, 5, 3};
        System.out.println("    数组: " + Arrays.toString(dupArr));
        boolean found = false;
        int dupCount = 0;
        for (int i = 0; i < dupArr.length; i++) {
            for (int j = i + 1; j < dupArr.length; j++) {
                dupCount++;
                if (dupArr[i] == dupArr[j]) {
                    found = true;
                }
            }
        }
        System.out.println("    找重复比较了 " + dupCount + " 次 → O(n²)");
        System.out.println("    用 HashSet 只要 O(n)！这就是优化。");
        System.out.println();
        System.out.println("  识别口诀：for 里面套 for，且都和 n 有关 → O(n²)");

        // =============================================================
        //  六、O(2ⁿ) 指数时间 —— 爆炸级
        // =============================================================
        System.out.println("\n========== 六、O(2ⁿ) 指数时间 —— 爆炸级！ ==========");
        System.out.println();
        System.out.println("  比喻：一张纸对折 1 次 2 层，对折 10 次 1024 层，");
        System.out.println("        对折 50 次 → 比地球到太阳还厚。这就是指数爆炸。");
        System.out.println();

        System.out.println("  经典代表：递归求斐波那契（每个分两支）");
        System.out.println("    fib(5)");
        System.out.println("    ├── fib(4)");
        System.out.println("    │   ├── fib(3)");
        System.out.println("    │   │   ├── fib(2)");
        System.out.println("    │   │   │   ├── fib(1)");
        System.out.println("    │   │   │   └── fib(0)");
        System.out.println("    │   │   └── fib(1)         ← 大量重复计算！");
        System.out.println("    │   └── fib(2)");
        System.out.println("    │       ├── fib(1)");
        System.out.println("    │       └── fib(0)");
        System.out.println("    └── fib(3)                 ← 又开始重复...");
        System.out.println("        ...");
        System.out.println();
        System.out.println("    → fib(50) 要算 2^50 ≈ 1千万亿次，宇宙毁灭都算不完。");
        System.out.println("    → 优化：用动态规划/记忆化，从 O(2ⁿ) 降到 O(n)");
        System.out.println();
        System.out.println("  识别口诀：递归每个分两支，没有缓存 → O(2ⁿ)");

        // =============================================================
        //  七、O(n!) 阶乘时间 —— 宇宙级
        // =============================================================
        System.out.println("\n========== 七、O(n!) 阶乘时间 —— 宇宙级（仅科普） ==========");
        System.out.println();
        System.out.println("  比喻：旅行商问题——走遍 20 个城市，找最短路线。");
        System.out.println("        20! ≈ 2.43 × 10^18 种路线，枚举完要 77000 年。");
        System.out.println("    → 算上不用，一般用贪心/动态规划近似解。");
        System.out.println();
        System.out.println("  识别口诀：全排列枚举 → O(n!)");

        // =============================================================
        //  八、性能实测对比
        // =============================================================
        System.out.println("\n========== 八、实测对比：同一个问题，不同复杂度差多远 ==========");
        System.out.println();

        int testN = 30000;
        int[] testArr = new int[testN];
        for (int i = 0; i < testN; i++) testArr[i] = testN - i;

        // O(n)：求和
        long start = System.nanoTime();
        long total = 0;
        for (int v : testArr) total += v;
        long oNTime = System.nanoTime() - start;

        // O(n log n)：快排
        int[] copy = testArr.clone();
        start = System.nanoTime();
        Arrays.sort(copy);
        long oNlogNTime = System.nanoTime() - start;

        // O(n²)：冒泡排序
        int[] copy2 = testArr.clone();
        start = System.nanoTime();
        for (int i = 0; i < copy2.length; i++) {
            for (int j = 0; j < copy2.length - 1 - i; j++) {
                if (copy2[j] > copy2[j + 1]) {
                    int t = copy2[j];
                    copy2[j] = copy2[j + 1];
                    copy2[j + 1] = t;
                }
            }
        }
        long oN2Time = System.nanoTime() - start;

        System.out.println("  n = " + testN + " 时的实测时间：");
        System.out.println("    O(n) 求和         : " + oNTime / 1000000.0 + " ms");
        System.out.println("    O(n log n) 排序   : " + oNlogNTime / 1000000.0 + " ms");
        System.out.println("    O(n²) 冒泡排序    : " + oN2Time / 1000000.0 + " ms  ← 差了几百倍！");
        System.out.println("    → n 越大，差距越离谱。O(n²) 根本撑不住大数据。");

        // =============================================================
        //  九、计算规则速查表
        // =============================================================
        System.out.println("\n========== 九、大 O 计算规则（速查表） ==========");
        System.out.println();
        System.out.println("  ┌──────────────────────────────────────────────────────┐");
        System.out.println("  │ 代码特征                         大 O      典型算法   │");
        System.out.println("  ├──────────────────────────────────────────────────────┤");
        System.out.println("  │ 无循环/数组按下标/HashMap 取值     O(1)    哈希查找   │");
        System.out.println("  │ 循环每次砍半/二叉堆/平衡树        O(log n) 二分查找   │");
        System.out.println("  │ 一层 for                          O(n)    遍历数组   │");
        System.out.println("  │ 砍半递归+每层遍历                 O(nlogn) 归并排序   │");
        System.out.println("  │ 两层 for 嵌套                     O(n²)   冒泡排序   │");
        System.out.println("  │ 三重 for 嵌套                     O(n³)   矩阵乘法   │");
        System.out.println("  │ 双分支递归(无缓存)                O(2ⁿ)   斐波那契   │");
        System.out.println("  │ 全排列枚举                        O(n!)   旅行商     │");
        System.out.println("  └──────────────────────────────────────────────────────┘");
        System.out.println();
        System.out.println("  判断步骤：");
        System.out.println("    1. 找最内层代码——它执行了多少次？");
        System.out.println("    2. 嵌套相乘，并列取最大");
        System.out.println("    3. 去掉常数和低阶项 → 就是大 O");

        // =============================================================
        //  十、实战练习：自己算
        // =============================================================
        System.out.println("\n========== 十、实战练习：你能算出这些代码的大 O 吗？ ==========");
        System.out.println();

        System.out.println("  Q1:  for(int i=0; i<n; i=i*2)     → O(______)");
        System.out.println("       答案：O(log n)，i 每次乘 2");
        System.out.println();
        System.out.println("  Q2:  for(int i=0; i<n; i++)        → O(______)");
        System.out.println("         for(int j=0; j<n; j++)");
        System.out.println("       答案：O(n²)，两层嵌套");
        System.out.println();
        System.out.println("  Q3:  for(int i=0; i<n; i++)        → O(______)");
        System.out.println("         for(int j=i; j<n; j++)");
        System.out.println("       答案：还是 O(n²)！执行 n+(n-1)+...+1 = n(n+1)/2 → O(n²)");
        System.out.println();
        System.out.println("  Q4:  for(int i=0; i<n; i++)    // O(n)    → O(______)");
        System.out.println("       for(int j=0; j<100; j++)  // O(1)");
        System.out.println("       答案：O(n)，内层是常数 100，不影响，100n → O(n)");
        System.out.println();
        System.out.println("  Q5:  for(int i=0; i<n; i++)        // O(n)");
        System.out.println("       for(int j=0; j<m; j++)        // O(m)   → O(______)");
        System.out.println("       答案：O(n × m)，两个独立变量不能合并");

        // =============================================================
        //  总结
        // =============================================================
        System.out.println("\n========================================");
        System.out.println("  总结");
        System.out.println("========================================");
        System.out.println("  复杂度      代码标志                 n=1000万 能跑吗？");
        System.out.println("  O(1)        无循环                  ✓ 瞬间");
        System.out.println("  O(log n)    每次砍半                ✓ 瞬间");
        System.out.println("  O(n)        一层 for                ✓ 可行");
        System.out.println("  O(n log n)  好排序                  ✓ 可行");
        System.out.println("  O(n²)       两层 for 嵌套           ✗ 太慢");
        System.out.println("  O(2ⁿ)       递归双分支              ✗ 不可行");
        System.out.println("  O(n!)       全排列                  ✗ 宇宙爆炸");
        System.out.println("========================================");
        System.out.println("面试官最爱问：这段代码的时间复杂度是多少？");
        System.out.println("  答：找到最内层循环/递归 → 看它执行了多少次 → 得出大 O。");
        System.out.println("========================================");
        System.out.println("速记口诀：");
        System.out.println("  无循环一操作 O(1) 恒定，");
        System.out.println("  每次砍一半 O(log n) 神速，");
        System.out.println("  一层 for 就 O(n) 线性，");
        System.out.println("  排个序 O(n log n) 实用，");
        System.out.println("  两层 for 爆炸 O(n²)，");
        System.out.println("  递归分叉 O(2ⁿ) 要命。");
    }
}
