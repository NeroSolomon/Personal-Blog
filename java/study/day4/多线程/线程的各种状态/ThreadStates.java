/*
 * ============================================================
 *  通俗理解 Java 线程的六种状态
 * ============================================================
 *
 *  继续用"厨师做菜"来打比方：
 *
 *  ┌─────────────────────────────────────────────────────────────┐
 *  │                                                             │
 *  │  NEW         厨师刚入职，还没开始干活                        │
 *  │  RUNNABLE    厨师正在做菜 / 或者准备好了等老板分配活          │
 *  │  BLOCKED     厨师想用焗炉，但焗炉被另一个厨师占着，排队等    │
 *  │  WAITING     厨师干完手头的活，等别人通知他再做下一道        │
 *  │  TIMED_WAITING 厨师说"我眯 3 分钟"，到点自己醒（sleep 等）  │
 *  │  TERMINATED  厨师下班了，任务完成                            │
 *  │                                                             │
 *  └─────────────────────────────────────────────────────────────┘
 *
 *  状态切换流程：
 *
 *         NEW  --start()-->  RUNNABLE  --执行完-->  TERMINATED
 *                              |   |                    ^
 *                              |   |                    |
 *        synchronized 抢不到锁  |   |   wait/join/park   |
 *         ┌──────────────────┘   └──────────────────┐   |
 *         v                                          v   |
 *      BLOCKED                                   WAITING |
 *                                                     |   |
 *                           sleep(t)/wait(t)          |   |
 *                            ┌──────────────────────┘   |
 *                            v                          |
 *                     TIMED_WAITING --时间到--> RUNNABLE |
 *                                                        |
 *     BLOCKED --抢到锁--> RUNNABLE                      |
 *     WAITING --被 notify/notifyAll--> RUNNABLE          |
 *                                                        |
 *  注：RUNNABLE 包含"正在运行"和"等待CPU调度"两种子状态
 *      这两种在 JVM 层面无法区分，操作系统自己管
 */

import java.util.concurrent.TimeUnit;

// =============================================================
//  演示用类：一把公共的菜刀（synchronized 锁）
// =============================================================
class SharedKnife {

    public synchronized void cut(String chefName) {
        System.out.println("[" + chefName + "] 拿到刀了，开始切菜...");
        try {
            Thread.sleep(2000);   // 切菜需要 2 秒
        } catch (InterruptedException e) {
            // 打印异常的*完整"案发现场"*到控制台：
            // - 异常类型和错误信息（如 NullPointerException: ...）
            // - 调用链路（从哪个方法开始 → 到哪一行代码炸了），精确到行号
            // try {
            //     int[] arr = new int[1];
            //     arr[2] = 0;           // 这里炸了
            // } catch (Exception e) {
            //     e.printStackTrace();  // 输出行号指向 arr[2] = 0
            // }
            e.printStackTrace();
        }
        System.out.println("[" + chefName + "] 切完菜了，放下刀");
    }
}

// =============================================================
//  主程序
// =============================================================
public class ThreadStates {
    public static void main(String[] args) throws Exception {
        System.out.println("========================================");
        System.out.println("  Java 线程的六种状态 —— 完整演示");
        System.out.println("========================================");

        // ========== 一、NEW 状态 ==========
        System.out.println("\n========== 一、NEW（新建）：刚创建，还没启动 ==========");
        Thread chef1 = new Thread(() -> {
            System.out.println("我开工了！");
        }, "Chef-张三");
        System.out.println("状态: " + chef1.getState());   // NEW

        // ========== 二、RUNNABLE 和 TERMINATED 状态 ==========
        System.out.println("\n========== 二、RUNNABLE -> TERMINATED ==========");
        chef1.start();
        System.out.println("start() 后状态: " + chef1.getState()); // RUNNABLE
        Thread.sleep(100);  // 等它跑完
        System.out.println("执行完后状态: " + chef1.getState());   // TERMINATED
        System.out.println("  -> 任务做完，线程就终止了");

        // ========== 三、TIMED_WAITING（sleep） ==========
        System.out.println("\n========== 三、TIMED_WAITING（有时限等待） ==========");
        Thread chef2 = new Thread(() -> {
            try {
                System.out.println("[Chef-李四] 累了，睡 3 秒...");
                Thread.sleep(3000);      // sleep 进入 TIMED_WAITING
                System.out.println("[Chef-李四] 醒了，继续干活！");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "Chef-李四");
        chef2.start();
        Thread.sleep(500);  // 确保它已经进入 sleep
        System.out.println("sleep 中状态: " + chef2.getState());  // TIMED_WAITING
        System.out.println("  -> sleep(3000) 就是「我眯 3 分钟，到点自己醒」");
        chef2.join(); // 等他醒
        System.out.println("醒后状态: " + chef2.getState() + "\n");

        // ========== 四、BLOCKED（抢锁失败，阻塞等待） ==========
        System.out.println("========== 四、BLOCKED（抢锁失败，排队等） ==========");
        SharedKnife knife = new SharedKnife();

        // 厨师王五先拿到刀
        Thread chef3 = new Thread(() -> {
            knife.cut("Chef-王五");
        }, "Chef-王五");
        chef3.start();
        Thread.sleep(300);  // 确保王五先进 synchronized 拿刀

        // 厨师赵六也要用同一把刀，但王五在用 -> BLOCKED
        Thread chef4 = new Thread(() -> {
            System.out.println("[Chef-赵六] 也想用刀...");
            knife.cut("Chef-赵六");
        }, "Chef-赵六");
        chef4.start();
        Thread.sleep(500);
        System.out.println("赵六状态（抢不到刀）: " + chef4.getState());  // BLOCKED
        System.out.println("  -> 同一个 synchronized 锁同时只能一个人拿到");
        System.out.println("  -> 赵六在门口排队等刀，这就是 BLOCKED");

        chef3.join();
        chef4.join();
        System.out.println("赵六最终状态: " + chef4.getState() + "\n");

        // ========== 五、WAITING（无限等待，等别人通知） ==========
        System.out.println("========== 五、WAITING（无限等待，等 notify） ==========");

        final Object bell = new Object();  // 一个铃铛，用于 wait/notify

        Thread chef5 = new Thread(() -> {
            synchronized (bell) {
                try {
                    System.out.println("[Chef-钱七] 干完活了，等老板按铃叫下一道...");
                    bell.wait();   // 释放锁，进入 WAITING，等 notify
                    System.out.println("[Chef-钱七] 铃响了！继续做下一道菜");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "Chef-钱七");
        chef5.start();
        Thread.sleep(500);
        System.out.println("钱七状态: " + chef5.getState());  // WAITING
        System.out.println("  -> wait() 无限等，不设超时，必须有人 notify 才醒");

        // 3 秒后，主线程按铃（notify）
        Thread.sleep(2000);
        System.out.println("[主线程] 老板按铃：下一道菜开始做！");
        synchronized (bell) {
            bell.notify();   // 叫醒等待的线程
        }
        chef5.join();
        System.out.println("notify 后状态: " + chef5.getState() + "\n");

        // ========== 总结 ==========
        System.out.println("========================================");
        System.out.println("  六种状态总结");
        System.out.println("========================================");
        System.out.println("状态           厨师比喻             如何进入");
        System.out.println("NEW            刚入职，没开工        new Thread()");
        System.out.println("RUNNABLE       正在干活/待命          start()");
        System.out.println("BLOCKED        等别人用完刀           抢 synchronized 锁失败");
        System.out.println("WAITING        等老板按铃（无限等）   wait()/join()/park()");
        System.out.println("TIMED_WAITING  先眯 3 分钟（有时限）  sleep(t)/wait(t)");
        System.out.println("TERMINATED     下班了/干完了           run() 执行完毕");
        System.out.println("========================================");
        System.out.println("关键区别：");
        System.out.println("  BLOCKED   = 等资源（锁），资源一到自动醒");
        System.out.println("  WAITING   = 等人通知，不通知永远不醒");
        System.out.println("  TIMED     = 耗时间，到点自己醒");
        System.out.println("========================================");
        System.out.println("速记口诀：");
        System.out.println("  新生是 NEW，开工就 RUN，");
        System.out.println("  抢锁 BLOCKED，等人 WAITING，");
        System.out.println("  有时限 TIME，干完就死 TERMINATED。");
    }
}
