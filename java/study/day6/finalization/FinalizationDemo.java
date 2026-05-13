/*
 * ============================================================
 *  通俗理解 finalize() — 析构函数 / 临终回调
 * ============================================================
 *
 *  用生活场景打比方：
 *
 *  你家请了个钟点工(finalize)，在你出门后帮忙关灯关水。
 *  但问题是：
 *    - 你不知道她哪天来（GC 时间不确定）
 *    - 她可能永远不来（GC 可能永远不回收这个对象）
 *    - 她来了也可能磨蹭半天才关（finalize 拖慢回收）
 *    - 更离谱的是，她关灯前喊了一声，你听到后又说「别关！」
 *      （对象在 finalize 里重新被引用 → 复活！）
 *
 *  正因为这么不靠谱，Java 9 标注 @Deprecated，Java 18 正式废弃。
 *
 *  新的「靠谱钟点工」：try-with-resources + AutoCloseable
 *    → 你出门时立刻关，不等阿姨来。确定、及时、不磨蹭。
 *
 *  ── finalize() 什么时候被调用？ ──
 *
 *  1. 对象变成垃圾（没有任何强引用指向它）
 *  2. GC 决定要回收这个对象时
 *  3. GC 调用 finalize() 方法
 *  4. 下一次 GC 才真正回收内存
 *
 *  关键问题：第 2 步「GC 决定回收」的时间完全不可预测！
 *    可能下一秒，可能一小时，可能程序结束时，也可能永远不回收。
 *
 *  ── 析构函数(finalization)的目的是什么？ ──
 *
 *  初衷：在对象被 GC 回收前，做最后的清理工作，比如：
 *    - 关闭文件句柄
 *    - 释放网络连接
 *    - 释放非 Java 的本地资源（堆外内存等）
 *
 *  现实：这些事根本不该交给 finalize()，因为它太不可靠了！
 *    - 资源释放必须及时，finalize 无法保证及时
 *    - 资源释放必须确定，finalize 无法保证一定执行
 *
 *  一句话总结：
 *    finalize 是「靠不住的临终关怀」——不知道什么时候来、来不来都难说。
 *    现代 Java 用 try-with-resources，资源用完立刻释放，不等 GC。
 */

public class FinalizationDemo {

    // ============ 一个有 finalize 的类 ============
    static class Resource {
        private String name;

        Resource(String name) {
            this.name = name;
            System.out.println("    [构造] " + name + " 被创建");
        }

        @Override
        protected void finalize() throws Throwable {
            System.out.println("    [finalize] " + name + " 正在被 GC 前清理...");
            // 这里本该关闭文件/释放资源，但根本不知道何时才执行！
            super.finalize();
        }
    }

    // ============ 演示「复活」的类 ============
    static class Resurrector {
        private String name;
        static Resurrector saved;  // 静态变量接住复活对象

        Resurrector(String name) {
            this.name = name;
            System.out.println("    [构造] " + name + " 诞生");
        }

        @Override
        protected void finalize() throws Throwable {
            System.out.println("    [finalize] " + name + " 即将死亡...但我可以复活！");
            saved = this;  // 重新被引用 → 复活！下次 GC 不会再调用 finalize
        }
    }

    // ============ 正确的写法：AutoCloseable ============
    static class ProperResource implements AutoCloseable {
        private String name;

        ProperResource(String name) {
            this.name = name;
            System.out.println("    [打开] " + name);
        }

        @Override
        public void close() {
            System.out.println("    [关闭] " + name + "  ← 资源被及时释放！");
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("========================================");
        System.out.println("  finalize() —— 靠不住的临终关怀");
        System.out.println("========================================");

        // =============================================================
        //  一、finalize 什么时候被调用？—— 完全不可预测！
        // =============================================================
        System.out.println("\n========== 一、finalize() 什么时候被调用？ ==========");
        System.out.println();
        System.out.println("  调用时机：对象变成垃圾 且 GC 决定回收它的时候。");
        System.out.println("  但「GC 决定回收」的时间没人说得准——");
        System.out.println("  可能下一秒，可能程序结束，可能永远不来。");
        System.out.println();

        System.out.println("  演示 1：创建对象 → 丢弃 → 等 GC");
        System.out.println("  ──────────────────────────────────");
        Resource r1 = new Resource("文件句柄A");
        System.out.println("  → 弃用 r1");
        r1 = null;

        System.out.println("  → 创建一堆垃圾，逼 GC 干活...");
        for (int i = 0; i < 5; i++) {
            new Resource("临时对象-" + i);
        }
        System.gc();
        Thread.sleep(1000);
        System.out.println("  → finalize 终于被调用了！但根本没法控制它何时跑。");

        System.out.println();
        System.out.println("  演示 2：程序结束前，没被 GC 的对象不会调 finalize");
        Resource r2 = new Resource("文件句柄B");
        System.out.println("  → r2 还活着，没有被 GC，finalize 不会被调用");
        System.out.println("  → 如果 r2 拿着文件锁没关，资源就泄漏了！");

        // =============================================================
        //  二、finalize 的「复活」黑魔法（面试考点）
        // =============================================================
        System.out.println("\n========== 二、finalize 的「复活」机制 ==========");
        System.out.println();
        System.out.println("  对象在 finalize() 里可以让自己重新被引用 → 死而复生！");
        System.out.println("  但复活只有一次机会——下次再变成垃圾,GC 直接跳过 finalize。");
        System.out.println();

        Resurrector ghost = new Resurrector("幽灵");
        System.out.println("  → 丢弃幽灵");
        ghost = null;
        System.gc();
        Thread.sleep(500);

        System.out.println("  幽灵:" + (Resurrector.saved != null ? "复活了！" : "死了"));
        System.out.println("  → 在 finalize 里把自己赋值给了静态变量，活过来了！");

        // 再次丢弃，不会再调 finalize
        Resurrector.saved = null;
        System.gc();
        Thread.sleep(500);
        System.out.println("  → 再次丢弃后，finalize 不会再被调用（每个对象只调一次）");

        // =============================================================
        //  三、finalize 拖慢 GC 的原因
        // =============================================================
        System.out.println("\n========== 三、finalize 为什么拖慢 GC？ ==========");
        System.out.println();
        System.out.println("  没有 finalize 的对象：GC 一口气扫完 → 完事。");
        System.out.println("  有 finalize 的对象：需要两步回收：");
        System.out.println();
        System.out.println("    第 1 步：GC 发现垃圾 → 不直接清，而是加入 Finalizer 队列");
        System.out.println("    第 2 步：Finalizer 线程一个个调 finalize()");
        System.out.println("    第 3 步：等下一次 GC 才真正回收内存");
        System.out.println();
        System.out.println("  → Finalizer 线程优先级低，如果队列堆积，内存就迟迟不释放！");
        System.out.println("  → 这就是为什么有 finalize 的对象要多熬至少一轮 GC。");

        // =============================================================
        //  四、正确做法：try-with-resources (AutoCloseable)
        // =============================================================
        System.out.println("\n========== 四、正确做法：try-with-resources ==========");
        System.out.println();
        System.out.println("  Java 的答案：别等 GC 来收拾，用完立刻自己关！");
        System.out.println();

        System.out.println("  [对比] finalize 方式（不靠谱）：");
        Resource bad = new Resource("文件C");
        bad = null;
        System.out.println("    → 丢了就不管了，等 GC 来处理...可能永远不来");
        System.out.println("    → 如果文件C占着文件锁，别人就打不开了！");

        System.out.println();
        System.out.println("  [对比] try-with-resources 方式（靠谱）：");
        try (ProperResource good = new ProperResource("文件D")) {
            System.out.println("    使用文件中...");
        } // 离开 try 块，自动调 close()，确定及时！
        System.out.println("    → 文件 D 在 try 块结束的瞬间就关闭了，不等 GC！");

        // =============================================================
        //  五、Java 9+ 的 Cleaner（finalize 的替代品）
        // =============================================================
        System.out.println("\n========== 五、Java 9+ 的 Cleaner（比 finalize 好一点） ==========");
        System.out.println();
        System.out.println("  Cleaner 类似于 finalize 的升级版，区别：");
        System.out.println("    1. Cleaner 的清理动作和对象引用分开（不拖慢回收）");
        System.out.println("    2. 不能复活（没有 this 引用）");
        System.out.println("    3. 但还是不保证及时性");
        System.out.println("    → 堆外内存管理可以用 Cleaner，普通资源还是用 try-with-resources");
        System.out.println();

        // 演示 Cleaner
        java.lang.ref.Cleaner cleaner = java.lang.ref.Cleaner.create();
        class CleanableObj {
            String name;
            CleanableObj(String name) { this.name = name; }
        }
        CleanableObj obj = new CleanableObj("Cleaner对象");
        cleaner.register(obj, () -> System.out.println("    [Cleaner] 清理 Cleaner对象"));

        System.out.println("  注册了 Cleaner，丢弃对象...");
        obj = null;
        System.gc();
        Thread.sleep(500);
        System.out.println("  → Cleaner 在 GC 后执行了清理（但时机依旧不保证）。");

        // =============================================================
        //  六、生命周期对比
        // =============================================================
        System.out.println("\n========== 六、对象从生到死的完整流程 ==========");
        System.out.println();
        System.out.println("  有 finalize() 的对象：");
        System.out.println("  ┌─────────────────────────────────────────────────────┐");
        System.out.println("  │  new → 活着 → 不可达 → Finalizer队列 → finalize()  │");
        System.out.println("  │                        ↑               ↓           │");
        System.out.println("  │                        └─ 复活 ←──────┘(可复活一次) │");
        System.out.println("  │                                        ↓           │");
        System.out.println("  │                           再次不可达 → 真正回收     │");
        System.out.println("  └─────────────────────────────────────────────────────┘");
        System.out.println();
        System.out.println("  没有 finalize() 的对象：");
        System.out.println("  ┌─────────────────────────────────────────────────────┐");
        System.out.println("  │  new → 活着 → 不可达 → 一轮 GC 直接回收 → 内存释放  │");
        System.out.println("  └─────────────────────────────────────────────────────┘");

        // =============================================================
        //  总结
        // =============================================================
        System.out.println("\n========================================");
        System.out.println("  总结");
        System.out.println("========================================");
        System.out.println();
        System.out.println("  1. finalize() 在 GC 回收对象前被 JVM 调用");
        System.out.println("  2. 调用时机完全不可预测，可能永远不被调用");
        System.out.println("  3. 初衷是做「临终清理」，但极不可靠");
        System.out.println("  4. 对象只能复活一次（finalize 只调一次）");
        System.out.println("  5. 有 finalize 的对象至少多熬一轮 GC，拖慢回收");
        System.out.println("  6. Java 9 起 @Deprecated，Java 18 正式废弃");
        System.out.println("  7. 替代方案：try-with-resources (推荐) / Cleaner (堆外)");
        System.out.println();
        System.out.println("  考你一下：finalize 能当 C++ 析构函数用吗？");
        System.out.println("  → 不能！C++ 析构函数是确定的/及时的，finalize 是随缘的。");
        System.out.println("========================================");
        System.out.println("速记口诀：");
        System.out.println("  finalize 临终交代，何时执行看 JVM 安排；");
        System.out.println("  可能复活一次怪，资源释放根本靠不来；");
        System.out.println("  现代 Java 早废弃它，try-with-resource 当替代。");
    }
}
