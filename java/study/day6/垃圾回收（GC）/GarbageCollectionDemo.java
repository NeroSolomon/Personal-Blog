/*
 * ============================================================
 *  通俗理解 Java 垃圾回收（GC — Garbage Collection）
 * ============================================================
 *
 *  用生活场景打比方：
 *
 *  GC 就像办公室的 「自动清洁阿姨」
 *
 *  你(C++程序员)：用完东西得自己扔 → 忘记扔？内存泄漏！扔两次？程序崩溃！
 *  你(Java程序员)：东西用完随手一丢，清洁阿姨会定期来收走。
 *
 *  清洁阿姨的工作方式：
 *    她不会看到地上有纸屑就立刻扫，而是——
 *    等垃圾桶快满了(内存压力)，或者趁你午休(System.gc()提示)，
 *    进来转一圈：桌上还在用的不动，地上的垃圾全扫走。
 *
 *  ── 到底什么是「垃圾」？ ──
 *
 *  垃圾 = 堆里没有任何引用的对象。
 *
 *  Person p = new Person("张三");   // 张三活着(有引用)
 *  p = null;                       // 张三变成垃圾(没人引用了)
 *
 *  清洁阿姨怎么判断「还在用」？
 *    从 GC Roots 出发（栈上的变量、静态变量、JNI引用等），
 *    能顺着引用链走到 → 活着的
 *    走不到的 → 垃圾，收走！
 *
 *  ── 堆内存结构（分代假说）──
 *
 *  根据统计：大多数对象朝生夕死，少数活得久。
 *  所以 JVM 把堆分成两个区域：
 *
 *  ┌──────────────────────────────────────────┐
 *  │               Java 堆内存                 │
 *  ├────────────────────┬─────────────────────┤
 *  │   新生代 Young Gen  │   老年代 Old Gen     │
 *  │  (朝生夕死的对象)   │  (活得久的对象)      │
 *  ├────────────────────┼─────────────────────┤
 *  │   Eden 区 (新生)   │                     │
 *  │   From 区 (幸存1)  │   长期存活对象       │
 *  │   To   区 (幸存2)  │                     │
 *  └────────────────────┴─────────────────────┘
 *
 *  新对象出生在 Eden 区 → Eden 满了触发 Minor GC(轻量级)
 *  → 幸存的搬到 From/To 区（复制算法，非常快）
 *  → 搬了 N 次还活着 → 晋升到老年代
 *  → 老年代满了触发 Major/Full GC(重量级，时间长)
 *
 *  ── System.gc() 和 Runtime.gc() ──
 *
 *  两者本质上是一模一样的！
 *    Runtime.getRuntime().gc()  是真正调用 native 方法的地方
 *    System.gc()                只是对上面这行的包装
 *
 *  源码：
 *    System.gc() { Runtime.getRuntime().gc(); }
 *
 *  重要：两者都只是「建议」JVM 做 GC，不是「命令」！
 *    JVM 可能立刻做，也可能完全不理你（-XX:+DisableExplicitGC）。
 *    正因如此，生产环境绝对不能依赖手动 GC，JVM 比你更懂时机。
 *
 *  ── finalize() —— 已废弃，别用 ──
 *
 *  Java 9 起 finalize() 已标记 @Deprecated，Java 18 正式废弃。
 *  GC 前回调不保证及时执行、不保证一定执行、拖慢回收速度。
 *  替代方案：try-with-resources / AutoCloseable / Cleaner(Java 9)
 *
 *  一句话总结：
 *    GC = 自动扫垃圾，不用你操心；System.gc 只是建议不是命令；
 *    新生代烧得快回收也快，老年代积得深回收也慢。
 */

import java.lang.ref.*;
import java.util.*;

public class GarbageCollectionDemo {

    // ============ 一个专门造垃圾的类 ============
    static class BigObject {
        private byte[] data;
        private String name;

        BigObject(String name) {
            this.name = name;
            this.data = new byte[5 * 1024 * 1024];  // 5MB
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("========================================");
        System.out.println("  Java 垃圾回收（GC）—— 通俗讲解");
        System.out.println("========================================");

        // =============================================================
        //  一、什么是「垃圾」？—— 没人引用的对象就是垃圾
        // =============================================================
        System.out.println("\n========== 一、什么是「垃圾」？ ==========");

        BigObject obj = new BigObject("对象A");
        System.out.println("  创建: " + obj + "  ← 有引用，活着");
        obj = null;
        System.out.println("  置 null 后: 对象A 变成垃圾(无人引用了) ← 等 GC 来收");
        System.out.println("  → 垃圾 = 堆里没有任何引用指向的对象。");

        // =============================================================
        //  二、GC 判断存活的方式：可达性分析
        // =============================================================
        System.out.println("\n========== 二、怎么判断对象还活着？可达性分析 ==========");
        System.out.println();
        System.out.println("  GC 从「GC Roots」出发，沿着引用链往下找：");
        System.out.println();
        System.out.println("    GC Roots (源头)     引用链");
        System.out.println("    ─────────────       ──────────────────");
        System.out.println("    栈上的局部变量  ──→ 对象B ──→ 对象C");
        System.out.println("    静态变量         ──→ 对象D");
        System.out.println("    JNI 引用         ──→ 对象E");
        System.out.println();
        System.out.println("    沿着链能走到的 → 存活  走不到的 → 垃圾");
        System.out.println();
        System.out.println("  例子：");
        BigObject alive = new BigObject("存活的");
        BigObject garbage = new BigObject("垃圾");
        garbage = null;  // 断链了，变成垃圾
        System.out.println("    alive 有引用 → 可达 → 存活");
        System.out.println("    garbage 无引用 → 不可达 → 垃圾");

        // =============================================================
        //  三、堆的分代结构
        // =============================================================
        System.out.println("\n========== 三、堆内存分代结构：为什么分代？ ==========");
        System.out.println();
        System.out.println("  据统计，80% 的对象活不过一个 GC 周期（朝生夕死）。");
        System.out.println("  所以 JVM 把堆分成两个区，用不同策略处理：");
        System.out.println();
        System.out.println("  ┌──────────────────────────────────────────────┐");
        System.out.println("  │              Java 堆(Heap)                    │");
        System.out.println("  ├──────────────────────┬───────────────────────┤");
        System.out.println("  │ 新生代(Young) 占1/3  │ 老年代(Old) 占 2/3     │");
        System.out.println("  ├──────┬──────┬────────┼───────────────────────┤");
        System.out.println("  │ Eden │ S0   │ S1     │                       │");
        System.out.println("  │(8/10)│(1/10)│(1/10)  │  长期存活的大对象      │");
        System.out.println("  └──────┴──────┴────────┴───────────────────────┘");
        System.out.println();
        System.out.println("  新生代 GC 过程(Minor GC，也叫 Young GC)：");
        System.out.println("    1) 新对象分配在 Eden 区");
        System.out.println("    2) Eden 满了 → 触发 Minor GC");
        System.out.println("    3) 存活的复制到 S0（From区）");
        System.out.println("    4) 下次 Minor GC：Eden + S0 的存活对象 → 复制到 S1（To区）");
        System.out.println("    5) S0 和 S1 轮流当 From/To，每次存活年龄 +1");
        System.out.println("    6) 年龄到了(默认15) → 晋升到老年代");
        System.out.println();
        System.out.println("  老年代 GC(Full GC / Major GC)：");
        System.out.println("    老年代也满了 → 对整个堆做 GC(慢！) → 标记-清除-整理");
        System.out.println();
        System.out.println("  为什么新生代用「复制算法」？因为活下来的少，复制成本极低！");
        System.out.println("  为什么老年代用「标记-整理」？因为活下来的多，复制不划算。");

        // =============================================================
        //  四、System.gc() vs Runtime.gc() —— 一模一样的
        // =============================================================
        System.out.println("\n========== 四、System.gc() 和 Runtime.gc() 的区别 ==========");
        System.out.println();
        System.out.println("  结论：本质上完全一样！");
        System.out.println();
        System.out.println("  源码真相（JDK 源码）：");
        System.out.println("    public static void gc() {");
        System.out.println("        Runtime.getRuntime().gc();   ← System.gc 就是这一行");
        System.out.println("    }");
        System.out.println();
        System.out.println("  Runtime.gc() 是通过 JNI 调用底层 native 方法");
        System.out.println("  System.gc()  只是对 Runtime.gc() 的包装");
        System.out.println();
        System.out.println("  伪代码对照：");
        System.out.println("    System.gc()                           建议 GC");
        System.out.println("    Runtime.getRuntime().gc()              建议 GC（同上）");
        System.out.println("    Runtime.getRuntime().totalMemory()     堆总大小");
        System.out.println("    Runtime.getRuntime().freeMemory()      剩余空闲内存");
        System.out.println("    Runtime.getRuntime().maxMemory()       最大可用内存");
        System.out.println();

        // 打印当前内存
        printMemory("程序启动时");
        System.out.println("    注：两者都只是 建议/提示 JVM 做 GC，不是命令！");
        System.out.println("    可能被 JVM 完全忽略(-XX:+DisableExplicitGC)");

        // =============================================================
        //  五、实际演示：创建垃圾 → 查看内存 → 手动 GC
        // =============================================================
        System.out.println("\n========== 五、实际演示：创建垃圾 → 看内存变化 → GC ==========");

        printMemory("造垃圾前");

        // 造一批垃圾对象
        List<BigObject> keepList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            keepList.add(new BigObject("保留对象-" + i));
        }
        printMemory("创建3个对象后(约15MB)");

        // 创建并立刻丢弃
        for (int i = 0; i < 10; i++) {
            BigObject trash = new BigObject("垃圾-" + i);  // 50MB 没人引用
        }
        printMemory("创建10个垃圾后(约50MB无人引用)");

        // 建议 GC
        System.out.println("\n  调用 System.gc()（建议 GC）...");
        System.gc();
        Thread.sleep(500);   // 给 GC 一点时间
        printMemory("System.gc() 后");
        System.out.println("  → 垃圾被回收，只剩 keepList 里的 3 个对象。");

        System.out.println("\n  再调用 Runtime.getRuntime().gc()（也是建议）...");
        Runtime.getRuntime().gc();
        Thread.sleep(500);
        printMemory("Runtime.gc() 后");
        System.out.println("  → 效果一样，因为 System.gc() 内部就是调 Runtime.gc()。");

        // =============================================================
        //  六、什么时候触发 GC？
        // =============================================================
        System.out.println("\n========== 六、GC 什么时候会触发？ ==========");
        System.out.println();
        System.out.println("  ┌──────────────────────────────────────────────────────┐");
        System.out.println("  │ 触发时机                 触发哪种 GC    可控性        │");
        System.out.println("  ├──────────────────────────────────────────────────────┤");
        System.out.println("  │ Eden 区满了              Minor GC      自动(JVM)    │");
        System.out.println("  │ 老年代满了               Full GC        自动(JVM)    │");
        System.out.println("  │ System.gc()              Full GC        建议(可忽略) │");
        System.out.println("  │ 担保失败(晋升放不下)     Full GC        自动(JVM)    │");
        System.out.println("  │ Metaspace 满了           Full GC        自动(JVM)    │");
        System.out.println("  └──────────────────────────────────────────────────────┘");
        System.out.println();
        System.out.println("  日常开发中，GC 几乎全是 JVM 自动触发的，你基本不用管。");
        System.out.println("  只有当出现 Full GC 太频繁、单次太慢时，才需要调优。");

        // =============================================================
        //  七、各种引用类型
        // =============================================================
        System.out.println("\n========== 七、扩展：四种引用类型（影响 GC 的判断） ==========");
        System.out.println();
        System.out.println("  ┌──────────────────────────────────────────────────────┐");
        System.out.println("  │ 引用类型         GC 行为               典型用途       │");
        System.out.println("  ├──────────────────────────────────────────────────────┤");
        System.out.println("  │ 强引用(默认)    永不回收               普通对象       │");
        System.out.println("  │ 软引用          内存不够才回收         缓存           │");
        System.out.println("  │ 弱引用          下次 GC 就回收          WeakHashMap   │");
        System.out.println("  │ 虚引用          回收时可收到通知       堆外内存管理   │");
        System.out.println("  └──────────────────────────────────────────────────────┘");

        // 强引用
        Object strong = new Object();
        System.out.println("    强引用: 只要 strong 还指着，GC 绝不回收 → 默认情况");

        // 软引用：内存紧张时回收
        SoftReference<BigObject> soft = new SoftReference<>(new BigObject("软引用对象"));
        System.out.println("    软引用: 创建了 " + soft.get() + "，内存不够时 GC 才会回收");

        // 弱引用：下次 GC 就回收
        WeakReference<BigObject> weak = new WeakReference<>(new BigObject("弱引用对象"));
        System.out.println("    弱引用: gc 前=" + (weak.get() != null ? "存活" : "已死"));
        System.gc();
        Thread.sleep(200);
        System.out.println("            gc 后弱引用对象=" + (weak.get() != null ? "存活" : "已死"));
        System.out.println("    → 弱引用对象在 GC 后立刻被回收了！");

        // =============================================================
        //  八、常用 GC 算法
        // =============================================================
        System.out.println("\n========== 八、常用 GC 算法（选学） ==========");
        System.out.println();
        System.out.println("  ┌──────────────────────────────────────────────────────────┐");
        System.out.println("  │ 算法              思路              用于           优点   │");
        System.out.println("  ├──────────────────────────────────────────────────────────┤");
        System.out.println("  │ 标记-清除         标记垃圾→清除     老年代        简单   │");
        System.out.println("  │ 复制算法          存活复制到新空间   新生代        快     │");
        System.out.println("  │ 标记-整理         标记→存活前移     老年代        无碎片 │");
        System.out.println("  │ 分代收集          年轻+年老分开      全堆          均衡   │");
        System.out.println("  └──────────────────────────────────────────────────────────┘");
        System.out.println();
        System.out.println("  现代 JVM 常用 GC 实现：");
        System.out.println("    Serial GC      单线程，客户端小应用");
        System.out.println("    Parallel GC   多线程，吞吐量优先(JDK8 默认)");
        System.out.println("    CMS           低延迟，老年代并发标记清除(JDK14 移除)");
        System.out.println("    G1            平衡吞吐和延迟(JDK9+ 默认)");
        System.out.println("    ZGC/Shenandoah 超低延迟(<10ms)，大堆(JDK11/15+)");

        // =============================================================
        //  九、GC 调优入门
        // =============================================================
        System.out.println("\n========== 九、GC 调优入门（需要怎么看 GC 日志） ==========");
        System.out.println();
        System.out.println("  JVM 参数速查：");
        System.out.println("    -XX:+PrintGCDetails       打印 GC 详情");
        System.out.println("    -XX:+PrintGCDateStamps    加上时间戳");
        System.out.println("    -Xlog:gc*                 新版统一日志(JDK9+)");
        System.out.println("    -Xms256m  -Xmx1024m      最小/最大堆大小");
        System.out.println("    -Xmn256m                  新生代大小");
        System.out.println();
        System.out.println("  调优口诀：");
        System.out.println("    频繁 Minor GC → 加大新生代");
        System.out.println("    频繁 Full GC  → 检查是否内存泄漏、加大老年代");
        System.out.println("    单次 GC 太慢  → 换 GC 算法或减小堆");

        // =============================================================
        //  总结
        // =============================================================
        System.out.println("\n========================================");
        System.out.println("  总结");
        System.out.println("========================================");
        System.out.println();
        System.out.println("  1. GC 目的：自动回收没人用的对象，程序员不用管 free");
        System.out.println("  2. 垃圾 = 从 GC Roots 出发走不到的堆对象");
        System.out.println("  3. 分代：新生代(复制，快) + 老年代(标记整理，慢)");
        System.out.println("  4. Minor GC 频繁但快，Full GC 慢但少见");
        System.out.println("  5. System.gc() == Runtime.gc()，只是建议，不是命令");
        System.out.println("  6. 绝对不能在生产环境依赖手动 GC");
        System.out.println("  7. finalize() 已废弃，用 try-with-resources 代替");
        System.out.println("========================================");
        System.out.println("速记口诀：");
        System.out.println("  GC 是个清洁工，没人用的就扫空；");
        System.out.println("  新生快死复制快，老不死的是祖宗；");
        System.out.println("  System.gc 是建议，听不听得看 JVM 心情；");
        System.out.println("  分代收集讲策略，调优要看 GC 日志。");
    }

    // ============ 辅助方法：打印内存信息 ============
    private static void printMemory(String tag) {
        Runtime rt = Runtime.getRuntime();
        long total = rt.totalMemory() / 1024 / 1024;
        long free = rt.freeMemory() / 1024 / 1024;
        long used = total - free;
        long max = rt.maxMemory() / 1024 / 1024;
        System.out.printf("  [%s] 已用: %d MB / 总量: %d MB / 最大: %d MB%n",
                          tag, used, total, max);
    }
}
