/*
 * ============================================================
 *  通俗理解 Java 创建线程的五种方式
 * ============================================================
 *
 *  用"厨房做饭"来打比方：
 *
 *  你是一家餐厅老板，要同时做三道菜。
 *  一个人做 = 单线程，一个一个来，太慢。
 *  请三个厨师 = 多线程，同时开工。
 *
 *  创建线程 = 请厨师的方式：
 *
 *  方式一：继承 Thread          = 每个厨师都是 Thread 的亲生儿子
 *  方式二：实现 Runnable        = 厨师拿了份"任务单"，交给 Thread 去执行
 *  方式三：实现 Callable        = 同上，但任务做完了能把"菜"端回来（有返回值）
 *  方式四：Lambda 表达式        = 连任务单都不写，口头交代一句
 *  方式五：线程池 ExecutorService = 请了个厨师团队，统一调度，高效复用 ★推荐
 *
 * ============================================================
 */

import java.util.concurrent.*;

// =============================================================
//  方式一：继承 Thread 类
// =============================================================
class ChefThread extends Thread {
    private String dishName;

    public ChefThread(String dishName) {
        this.dishName = dishName;
    }

    @Override
    public void run() {   // 把任务写在这里
        System.out.println("[Thread] " + dishName + " 开始做...");
        try {
            Thread.sleep(1000);   // 模拟做菜耗时
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("[Thread] " + dishName + " 做好了！");
    }
}

// =============================================================
//  方式二：实现 Runnable 接口
// =============================================================
class ChefTask implements Runnable {
    private String dishName;

    public ChefTask(String dishName) {
        this.dishName = dishName;
    }

    @Override
    public void run() {
        System.out.println("[Runnable] " + dishName + " 开始做...");
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("[Runnable] " + dishName + " 做好了！");
    }
}

// =============================================================
//  方式三：实现 Callable 接口（有返回值）
// =============================================================
class ChefCallable implements Callable<String> {
    private String dishName;

    public ChefCallable(String dishName) {
        this.dishName = dishName;
    }

    @Override
    public String call() throws Exception {   // 注意：是 call() 不是 run()！
        System.out.println("[Callable] " + dishName + " 开始做...");
        Thread.sleep(1200);
        System.out.println("[Callable] " + dishName + " 做好了！");
        return "「" + dishName + "」已完成，请慢用";  // 有返回值！
    }
}


// =============================================================
//  主程序
// =============================================================
public class ThreadDemo {
    public static void main(String[] args) throws Exception {
        System.out.println("========================================");
        System.out.println("  Java 创建线程的五种方式 —— 完整演示");
        System.out.println("========================================");

        // ========== 一、继承 Thread ==========
        System.out.println("\n========== 一、继承 Thread ==========");
        System.out.println("缺点：单继承限制，占了 extends 就不能继承别的了");
        ChefThread chef1 = new ChefThread("宫保鸡丁");
        chef1.start();   // 启动线程，会自动调用 run()
        chef1.join();    // 等它做完，方便演示
        System.out.println("  -> 方式一完成\n");

        // ========== 二、实现 Runnable ==========
        System.out.println("========== 二、实现 Runnable ==========");
        System.out.println("优点：不影响类继承，任务和线程分离，更灵活");
        ChefTask task = new ChefTask("鱼香肉丝");
        Thread chef2 = new Thread(task);   // Runnable 交给 Thread 执行
        chef2.start();
        chef2.join();
        System.out.println("  -> 方式二完成\n");

        // ========== 三、实现 Callable（有返回值） ==========
        System.out.println("========== 三、实现 Callable（有返回值） ==========");
        System.out.println("优点：任务做完能返回结果，Runnable 做不到这点");
        ChefCallable callableTask = new ChefCallable("东坡肉");
        FutureTask<String> futureTask = new FutureTask<>(callableTask);
        Thread chef3 = new Thread(futureTask);
        chef3.start();
        String result = futureTask.get();   // 阻塞等待，拿到返回值
        System.out.println("  返回值: " + result);
        System.out.println("  -> 方式三完成\n");

        // ========== 四、Lambda 表达式（Java 8+） ==========
        System.out.println("========== 四、Lambda 表达式 ==========");
        System.out.println("优点：代码最简洁，适合简单任务");
        Thread chef4 = new Thread(() -> {
            System.out.println("[Lambda] 麻婆豆腐 开始做...");
            try { Thread.sleep(600); } catch (InterruptedException e) {}
            System.out.println("[Lambda] 麻婆豆腐 做好了！");
        });
        chef4.start();
        chef4.join();
        System.out.println("  -> 方式四完成\n");

        // ========== 五、线程池 ExecutorService ★推荐 ==========
        System.out.println("========== 五、线程池 ★推荐方式 ==========");
        System.out.println("优点：复用线程、控制并发数、统一管理、有返回值");
        System.out.println("      生产环境都用这个，前面四种基本不用");

        ExecutorService pool = Executors.newFixedThreadPool(2);  // 2个厨师的团队

        // 提交 Runnable 任务（无返回值）
        pool.submit(() -> {
            System.out.println("[线程池] 蚝油生菜 开始做...");
            try { Thread.sleep(700); } catch (InterruptedException e) {}
            System.out.println("[线程池] 蚝油生菜 做好了！");
        });

        // 提交 Callable 任务（有返回值）
        Future<String> future = pool.submit(() -> {
            System.out.println("[线程池] 佛跳墙 开始做...");
            try { Thread.sleep(1500); } catch (InterruptedException e) {}
            System.out.println("[线程池] 佛跳墙 做好了！");
            return "「佛跳墙」大功告成！";
        });

        System.out.println("  等佛跳墙出锅...");
        String poolResult = future.get();
        System.out.println("  返回值: " + poolResult);

        pool.shutdown();   // 优雅关闭，不再接新单，等现有任务做完
        System.out.println("  -> 方式五完成\n");

        // ========== 线程池大小评估 ==========
        System.out.println("========== 线程池放多少个线程？ ==========");
        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println("当前机器 CPU 核心数: " + cores);
        System.out.println();
        System.out.println("评估公式: 线程数 = CPU核心数 × (1 + 等待时间/计算时间)");
        System.out.println();
        System.out.println("  原理：让 CPU 别闲着。假设一个任务计算 10ms + 等数据库 90ms，");
        System.out.println("       一共 100ms 中 CPU 只干了 10ms，利用率才 10%。");
        System.out.println("       CPU 干等的 90ms 里，完全可以再塞 9 个线程进来干活。");
        System.out.println("       等待/计算 = 90/10 = 9，所以 核心数 × (1+9) = 10 个线程。");
        // 但这个公式是理论上限，实际还要考虑：
        // - 线程本身有切换开销，太多反而拖慢
        // - 内存有限，每个线程占约 1MB 栈空间
        // - 数据库、API 有连接数限制，线程多了也排队
        // 所以实际生产环境一般是 核心数 × 2 吃一波，不够再微调。
        System.out.println();
        System.out.println("  实际经验值：");
        System.out.println("    CPU 密集型（加密/排序/纯计算，等待≈0）：       核心数 + 1");
        System.out.println("      -> +1 是兜底冗余，防止偶发的缺页/GC/调度抖动让 CPU 空转");
        System.out.println("    IO 密集型（网络/磁盘/数据库，大量等待）：      核心数 × 2");
        System.out.println();
        System.out.println("  注意：这是理论上限，实际还要留余量——");
        System.out.println("        线程本身有切换开销、每个占 ~1MB 栈内存、");
        System.out.println("        数据库/API 有连接数上限，线程多了也排队。");
        System.out.println();

        // ========== 总结 ==========
        System.out.println("========================================");
        System.out.println("  五种方式对比");
        System.out.println("========================================");
        System.out.println("方式          有返回值   单继承问题   推荐度");
        System.out.println("继承 Thread   无         受限         不推荐");
        System.out.println("Runnable      无         不受限       一般");
        System.out.println("Callable      有         不受限       一般");
        System.out.println("Lambda        无/有      不受限       简单场景可用");
        System.out.println("线程池        无/有      不受限       ★★★ 强烈推荐");
        System.out.println("========================================");
        System.out.println("为什么推荐线程池？");
        System.out.println("  1. 复用线程：不用反复创建/销毁，省资源");
        System.out.println("  2. 控制并发：newFixedThreadPool(2) 最多就 2 个并发");
        System.out.println("  3. 统一管理：shutdown、超时、排队、拒绝策略全能搞定");
        System.out.println("  4. 有返回值：submit(Callable) 返回 Future，拿到结果");
        System.out.println("  5. 灵活度：Runnable 和 Callable 两种任务都支持");
        System.out.println("========================================");
        System.out.println("速记口诀：");
        System.out.println("  Thread 太独占，Runnable 能共享，");
        System.out.println("  Callable 能带返回值，Lambda 简洁爽，");
        System.out.println("  线程池是王道，生产环境都用它。");
    }
}
