// 不是"必须完成前面的才能执行后面的"。synchronized 的核心是锁——同一把锁，同一时刻只能被一个线程持有。
// 关键区别：
// - 同一把锁上的代码 → 串行排队（你切菜时别人不能切）
// - 不同锁上的代码 → 可以并行（你切你菜，我炒我的锅）
// - 锁外面的代码 → 不受任何影响（随便进厨房，不需要排队）
// // 100 行代码
// synchronized (knifeLock) {
//     切菜();   // ← 只有这 3 行受保护
// }
// // 剩下 97 行代码，任何线程随时并发执行，完全不受限制
// 所以 synchronized 不是"等前面的代码执行完"，而是**"等前面的线程把锁还回来"**。释放锁之后，等锁的线程中 JVM 随机挑一个拿走锁继续跑

/*
 * ============================================================
 *  通俗理解 synchronized 方法和同步代码块
 * ============================================================
 *
 *  继续用"厨房做菜"来打比方：
 *
 *  synchronized 方法   = 把整间厨房门上锁
 *                        厨师 A 进去了，厨师 B 就只能在门口干等。
 *                        哪怕 A 只是在洗碗，B 想进去切个葱花都不行——
 *                        因为整间厨房都被 A 占了！
 *
 *  synchronized 代码块  = 只锁一把刀 / 一口锅
 *                        厨师 A 在用刀切菜，厨师 B 可以同时用锅炒菜。
 *                        互不干扰，只争同一把刀时才排队。
 *
 *  核心区别：
 *
 *  ┌──────────────────────────────────────────────────────┐
 *  │                                                      │
 *  │  同步方法（粗粒度锁）                                 │
 *  │    锁的是整个方法，即 this（当前对象）               │
 *  │    整个方法体都被保护，不能并行                       │
 *  │    简单省事，但效率低                                 │
 *  │                                                      │
 *  │  同步代码块（细粒度锁）                               │
 *  │    只锁花括号里的那几行                               │
 *  │    可指定锁任意对象（this / 某个 Object / Class）     │
 *  │    灵活高效，只锁该锁的部分                           │
 *  │                                                      │
 *  └──────────────────────────────────────────────────────┘
 */

import java.util.concurrent.atomic.AtomicInteger;

// =============================================================
//  方式一：synchronized 方法 —— 锁整个厨房
// =============================================================
class Kitchen_Method {

    // 同步方法：锁的是 this，整个方法串行
    public synchronized void cook(String dish) {
        System.out.println("[同步方法-厨房] " + dish + " 开始做...");
        System.out.println("  (整间厨房被锁，别人进不来)");

        // 模拟做菜
        try { Thread.sleep(1000); } catch (InterruptedException e) {}

        System.out.println("[同步方法-厨房] " + dish + " 做好了！解锁厨房");
        System.out.println();
    }
}

// =============================================================
//  方式二：synchronized 代码块 —— 只锁关键工具
// =============================================================
class Kitchen_Block {

    // 两把不同的锁：刀和锅，互不影响
    private final Object knifeLock = new Object();  // 菜刀锁
    private final Object potLock   = new Object();  // 炒锅锁

    // 非同步：两个人可以同时进厨房
    public void cut(String chef, String ingredient) {
        synchronized (knifeLock) {    // 只锁刀，不锁锅
            System.out.println("[" + chef + "] 拿到菜刀，开始切 " + ingredient + "...");
            try { Thread.sleep(800); } catch (InterruptedException e) {}
            System.out.println("[" + chef + "] 切完 " + ingredient + "，放下刀");
        }
    }

    public void stirFry(String chef, String dish) {
        synchronized (potLock) {      // 只锁锅，不锁刀
            System.out.println("[" + chef + "] 拿到炒锅，开始炒 " + dish + "...");
            try { Thread.sleep(1200); } catch (InterruptedException e) {}
            System.out.println("[" + chef + "] 炒完 " + dish + "，放下锅");
        }
    }
}


// =============================================================
//  主程序
// =============================================================
public class SynchronizedDemo {
    public static void main(String[] args) throws Exception {
        System.out.println("========================================");
        System.out.println("  synchronized 方法 vs 同步代码块");
        System.out.println("========================================");

        // ========== 一、同步方法（粗粒度，效率低） ==========
        System.out.println("\n========== 一、同步方法：锁整个厨房 ==========");
        System.out.println("两位厨师同时想做菜，但厨房只能进一个人\n");

        Kitchen_Method km = new Kitchen_Method();

        Thread chefA = new Thread(() -> km.cook("红烧排骨"), "厨师A");
        Thread chefB = new Thread(() -> km.cook("清蒸鲈鱼"), "厨师B");

        chefA.start();
        // 两行都是 Thread.sleep(100)，作用是*让第一个线程先"抢到锁"*，确保演示结果可靠。
        // chefA.start();        // A 启动，去抢厨房锁
        // Thread.sleep(100);    // 主线程等 100ms，确保 A 已经进去了
        // chefB.start();        // B 再启动 → 此时厨房被 A 占着，B 只能排队等
        // 如果不加这 100ms，A 和 B 几乎同时启动，谁先拿到锁是随机的——可能 B 先进去，A 在门口等，演示结果就不稳定了。它只是一个人为制造时序的技巧，让现象更直观。
        Thread.sleep(100);
        chefB.start();

        chefA.join();
        chefB.join();

        System.out.println("=> 厨师 B 眼睁睁等厨师 A 做完才能进厨房，");
        System.out.println("   哪怕两个人用的工具完全不冲突。\n");

        // ========== 二、同步代码块（细粒度，效率高） ==========
        System.out.println("========== 二、同步代码块：只锁工具不锁厨房 ==========");
        System.out.println("两位厨师同时进厨房，一个切菜一个炒菜，互不耽误\n");

        Kitchen_Block kb = new Kitchen_Block();

        Thread chefC = new Thread(() -> {
            kb.cut("厨师C", "胡萝卜");
            kb.stirFry("厨师C", "鱼香肉丝");
        }, "厨师C");

        Thread chefD = new Thread(() -> {
            kb.stirFry("厨师D", "宫保鸡丁");      // 先炒菜
            kb.cut("厨师D", "青椒");              // 再切菜
        }, "厨师D");

        chefC.start();
        Thread.sleep(100);
        chefD.start();

        chefC.join();
        chefD.join();

        System.out.println("=> 厨师 C 切胡萝卜时，厨师 D 同时在炒宫保鸡丁！");
        System.out.println("   两个人共用厨房，只争刀和锅，效率翻倍！\n");

        // ========== 三、对比代码写法 ==========
        System.out.println("========== 三、代码写法对比 ==========");

        System.out.println("同步方法写法:");
        System.out.println("  public synchronized void method() { ... }");
        System.out.println("  等价于: public void method() { synchronized(this) { ... } }");
        System.out.println("  锁对象 = this（当前实例）\n");

        System.out.println("同步代码块写法:");
        System.out.println("  synchronized (任意对象) { 需要同步的代码 }");
        System.out.println("  可以指定锁 this / 某个 Object / 类名.class\n");

        System.out.println("常见锁对象:");
        System.out.println("  synchronized (this)           -> 锁当前实例");
        System.out.println("  synchronized (obj)            -> 锁指定对象");
        System.out.println("  synchronized (ClassName.class) -> 锁类的 Class 对象（全局唯一）");

        // ========== 总结 ==========
        System.out.println("\n========================================");
        System.out.println("  总结");
        System.out.println("========================================");
        System.out.println("对比维度      同步方法            同步代码块");
        System.out.println("粒度          粗（整个方法）      细（指定几行）");
        System.out.println("锁对象        固定 this           可指定任意对象");
        System.out.println("并发度        低                  高");
        System.out.println("代码简洁度    高                  需手动写花括号");
        System.out.println("适用场景      方法简单、耗时短    只需保护部分代码");
        System.out.println("========================================");
        System.out.println("原则：锁的范围越小越好！");
        System.out.println("  把 synchronized 圈在真正需要保护的代码上，");
        System.out.println("  不需要同步的部分放锁外面，让其他人也能同时跑。");
        System.out.println("========================================");
        System.out.println("速记口诀：");
        System.out.println("  方法同步锁全家，进门排队就傻等；");
        System.out.println("  代码块锁只锁物，你用刀来我炒锅，");
        System.out.println("  互不干扰真高效。");
    }
}
