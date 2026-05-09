/*
 * ============================================================
 *  通俗理解 死锁 (Deadlock)
 * ============================================================
 *
 *  继续用"厨房做菜"来打比方：
 *
 *  ===== 什么是锁？ =====
 *  锁 = 一个"独占使用权"的令牌。
 *
 *  厨房里有一把菜刀和一口炒锅，每样东西一次只能一个人用。
 *  厨师拿到刀，别人就不能切；拿到锅，别人就不能炒。
 *  这就是锁——保护共享资源，防止同时操作搞乱事情。
 *
 *
 *  ===== 什么是死锁？ =====
 *  两个厨师，都要先用刀切菜，再用锅炒菜。
 *
 *  厨师 A 先抢到了刀（锁刀成功）；
 *  厨师 B 先抢到了锅（锁锅成功）。
 *
 *  接下来：
 *    厨师 A 需要锅才能继续，但锅在 B 手里 —— A 死等
 *    厨师 B 需要刀才能继续，但刀在 A 手里 —— B 死等
 *
 *  结果：A 等 B，B 等 A，谁也不放手，永远卡死。
 *  这就是死锁 —— 互相等着对方手里的东西。
 *
 *  ┌──────────────────────────────────────────────────────┐
 *  │                                                      │
 *  │  死锁四个必要条件（缺了哪个，死锁就不会发生）        │
 *  │                                                      │
 *  │  1. 互斥：资源一次只能一个人用（刀不能同时切）       │
 *  │  2. 持有并等待：拿着刀，还等着要锅                   │
 *  │  3. 不可剥夺：别人不能从你手里把刀抢走               │
 *  │  4. 环路等待：A 等 B 的锅，B 等 A 的刀，形成环       │
 *  │                                                      │
 *  │  破解：打破任意一条即可。最容易的是打破第 4 条——     │
 *  │        让所有人都「先拿刀再拿锅」，统一顺序，        │
 *  │        就不会出现你拿刀我等锅的交叉局面。            │
 *  │                                                      │
 *  └──────────────────────────────────────────────────────┘
 */


// =============================================================
//  演示：死锁场景
// =============================================================
class DeadlockKitchen {

    // 两个共享资源，各自一把锁
    private final Object knife = new Object();   // 刀锁
    private final Object pot   = new Object();   // 锅锁

    // 厨师 A 的做菜方式：先拿刀，再拿锅
    public void chefA_Cook() {
        synchronized (knife) {
            System.out.println("[厨师A] 拿到刀了，开始切菜...");
            pause(500);   // 切菜中...故意等一下让 B 有时间为非作歹

            System.out.println("[厨师A] 切完了，现在需要锅来炒菜...");
            synchronized (pot) {   // 试图拿锅 — 但锅在 B 手里！
                System.out.println("[厨师A] 拿到锅了，开始炒菜！");  // 永远走不到这里
            }
        }
    }

    // 厨师 B 的做菜方式：先拿锅，再拿刀（顺序与 A 相反！）
    public void chefB_Cook() {
        synchronized (pot) {
            System.out.println("[厨师B] 拿到锅了，开始热锅...");
            pause(500);   // 热锅中...故意等一下

            System.out.println("[厨师B] 锅热好了，现在需要刀来切菜...");
            synchronized (knife) {   // 试图拿刀 — 但刀在 A 手里！
                System.out.println("[厨师B] 拿到刀了，开始切菜！");  // 永远走不到这里
            }
        }
    }

    private void pause(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) {}
    }
}


// =============================================================
//  正确做法：统一锁的顺序
// =============================================================
class SafeKitchen {

    private final Object knife = new Object();
    private final Object pot   = new Object();

    // 厨师 C、D 都遵循「先拿刀，再拿锅」的顺序
    public void chefC_Cook() {
        synchronized (knife) {
            System.out.println("[厨师C] 拿到刀了，切菜中...");
            pause(300);
            synchronized (pot) {
                System.out.println("[厨师C] 拿到锅了，开始炒菜！");
                pause(500);
            }
        }
        System.out.println("[厨师C] 菜做好啦！");
    }

    public void chefD_Cook() {
        synchronized (knife) {   // 也是先拿刀！（和 C 顺序一致）
            System.out.println("[厨师D] 拿到刀了，切菜中...");
            pause(300);
            synchronized (pot) {
                System.out.println("[厨师D] 拿到锅了，开始炒菜！");
                pause(500);
            }
        }
        System.out.println("[厨师D] 菜做好啦！");
    }

    private void pause(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) {}
    }
}


// =============================================================
//  主程序
// =============================================================
public class DeadlockDemo {
    public static void main(String[] args) throws Exception {
        System.out.println("========================================");
        System.out.println("  死锁 —— 演示与破解");
        System.out.println("========================================");

        // ========== 一、制造死锁 ==========
        System.out.println("\n========== 一、死锁演示 ==========");
        System.out.println("厨师A: 先拿刀，再拿锅");
        System.out.println("厨师B: 先拿锅，再拿刀  ← 顺序反了！");
        System.out.println("结果: A 等锅，B 等刀，互相卡死\n");

        DeadlockKitchen dk = new DeadlockKitchen();

        Thread a = new Thread(() -> dk.chefA_Cook(), "厨师A");
        Thread b = new Thread(() -> dk.chefB_Cook(), "厨师B");

        a.start();
        Thread.sleep(100);   // 确保 A 先拿到刀
        b.start();

        // 等 3 秒，如果线程还没结束，就是死锁了
        Thread.sleep(3000);

        if (a.isAlive() && b.isAlive()) {
            System.out.println("\n!!! 检测到死锁！两个厨师还在互相等，程序卡死了 !!!");
            System.out.println("厨师A 状态: " + a.getState() + " (在等锅)");
            System.out.println("厨师B 状态: " + b.getState() + " (在等刀)");
            System.out.println("A 拿着刀等锅，B 拿着锅等刀 → 死循环\n");

            // 强制中断，否则程序永远不会结束
            a.interrupt();
            b.interrupt();
        }

        // ========== 二、正确做法：统一锁的顺序 ==========
        System.out.println("========== 二、破解死锁：统一锁的顺序 ==========");
        System.out.println("厨师C: 先拿刀，再拿锅");
        System.out.println("厨师D: 先拿刀，再拿锅  ← 顺序一样！");
        System.out.println("结果: 谁先拿到刀谁先跑完，不会卡死\n");

        SafeKitchen sk = new SafeKitchen();

        Thread c = new Thread(() -> sk.chefC_Cook(), "厨师C");
        Thread d = new Thread(() -> sk.chefD_Cook(), "厨师D");

        c.start();
        Thread.sleep(100);
        d.start();

        c.join();
        d.join();

        System.out.println("=> 两个厨师都顺利做完了！没有死锁\n");

        // ========== 总结 ==========
        System.out.println("========================================");
        System.out.println("  总结");
        System.out.println("========================================");
        System.out.println("什么是锁？ 保护共享资源的"使用权令牌"，一次只能一人拿");
        System.out.println();
        System.out.println("死锁四条件：");
        System.out.println("  1. 互斥        资源一次只能一个人用");
        System.out.println("  2. 持有并等待   拿着一个，还等着另一个");
        System.out.println("  3. 不可剥夺     别人不能抢你手里的");
        System.out.println("  4. 环路等待     A等B的，B等A的");
        System.out.println();
        System.out.println("如何避免？");
        System.out.println("  - 统一加锁顺序（最常用）：所有人都先拿刀再拿锅");
        System.out.println("  - 设置超时时间：等太久就放弃，释放已持有的锁");
        System.out.println("  - 尽量减少锁的数量：能不锁就不锁");
        System.out.println("  - 使用 tryLock() (ReentrantLock)：拿不到就撤，不死等");
        System.out.println("========================================");
        System.out.println("速记口诀：");
        System.out.println("  你拿刀来我等锅，你等刀来我拿锅，");
        System.out.println("  环环相扣解不开，一起卡死就死锁。");
        System.out.println("  要想避开这悲剧，锁的顺序得统一。");
    }
}
