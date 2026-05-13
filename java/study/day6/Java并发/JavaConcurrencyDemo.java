/*
 * ============================================================
 *  Java 并发 —— 26 道高频面试题全解（通俗版）
 * ============================================================
 */

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;

public class JavaConcurrencyDemo {

    static List<String> buildLog = Collections.synchronizedList(new ArrayList<>());
    static void log(String msg) { buildLog.add(msg); System.out.println(msg); }

    public static void main(String[] args) throws Exception {
        printHR("Java 并发 —— 26 道高频面试题全解");

        Q1_ThreadVsProcess();
        Q2_WhyMultithread();
        Q3_ContextSwitch();
        Q4_Deadlock();
        Q5_OptimisticPessimisticLock();
        Q6_SleepVsWait();
        Q7_JMM_and_Volatile();
        Q8_MemoryRegionVsJMM();
        Q9_HappensBefore();
        Q10_Synchronized();
        Q11_SynchronizedVsReentrantLock();
        Q12_SynchronizedVsVolatile();
        Q13_SynchronizedUnderlying();
        Q14_ThreadLocal();
        Q15_ThreadPool();
        Q16_ThreadPoolParams();
        Q17_ThreadPoolFlow();
        Q18_RunnableVsCallable();
        Q19_ThreadPoolNaming();
        Q20_DynamicThreadPool();
        Q21_AQS();
        Q22_AQSComponents();
        Q23_AQSPrinciple();
        Q24_Semaphore_CountDownLatch();
        Q25_CyclicBarrier();
        Q26_CompletableFuture();

        printHR("全部讲完！");
    }

    // ================================================================
    //  Q1: 什么是线程和进程? 线程与进程的关系、区别及优缺点?
    // ================================================================
    static void Q1_ThreadVsProcess() {
        printH2("Q1: 什么是线程和进程？关系、区别及优缺点？");
        log("  进程 = 一个正在运行的程序实例（工厂）");
        log("  线程 = 进程里的一个执行路径（工厂里的工人）");
        log("");
        log("  关系：一个进程至少有一个线程（主线程），可以有多个。");
        log("  进程间独立，线程间共享进程的堆和方法区，但各自有独立的栈和PC寄存器。");
        log("");
        log("  ┌────────────┬──────────────┬──────────────┐");
        log("  │            │ 进程         │ 线程         │");
        log("  ├────────────┼──────────────┼──────────────┤");
        log("  │ 切换成本   │ 高(保存整个上下文) │ 低(只切栈)  │");
        log("  │ 通信方式   │ IPC(管道/共享内存)│ 共享内存     │");
        log("  │ 内存隔离   │ 完全隔离      │ 共享堆        │");
        log("  │ 创建销毁   │ 重            │ 轻            │");
        log("  └────────────┴──────────────┴──────────────┘");
    }

    // ================================================================
    //  Q2: 为什么要使用多线程?
    // ================================================================
    static void Q2_WhyMultithread() {
        printH2("Q2: 为什么要使用多线程？");
        log("  三个核心价值：");
        log("    1. 充分利用多核 CPU（单线程只用一核，多线程用满）");
        log("    2. 提高响应速度（如 Web 服务器，每请求一线程，不等堵车）");
        log("    3. 编程模型更自然（有些问题本身就是并行的，如多个下载任务）");
        log("");
        log("  打比方：");
        log("    单线程 = 一个收银员，顾客排长队；");
        log("    多线程 = 开 8 个收银窗口，同时结账。");
    }

    // ================================================================
    //  Q3: 什么是上下文切换?
    // ================================================================
    static void Q3_ContextSwitch() {
        printH2("Q3: 什么是上下文切换？");
        log("  CPU 从一个线程切换到另一个线程时，需要：");
        log("    保存当前线程的状态（PC、寄存器、栈指针等）");
        log("    加载新线程的状态");
        log("    → 这个过程就叫「上下文切换」。");
        log("");
        log("  就像你同时打游戏+写代码，切来切去要花时间回忆「刚打到哪了」。");
        log("  → 线程不是越多越好，太多切换成本反而拖慢。");
    }

    // ================================================================
    //  Q4: 什么是线程死锁? 如何避免?
    // ================================================================
    static void Q4_Deadlock() {
        printH2("Q4: 什么是线程死锁？如何避免？");
        log("  死锁 = 两个线程互相等对方手里的锁，永远等下去。");
        log("");
        log("  比喻：A 拿着筷子等碗，B 拿着碗等筷子，两人都不撒手。");
        log("");
        log("  4 个必要条件（缺一不可）：");
        log("    1. 互斥：同一资源同时只能一个线程用");
        log("    2. 持有并等待：拿着锁 A，又去等锁 B");
        log("    3. 不可剥夺：不能强行抢走别人的锁");
        log("    4. 循环等待：A 等 B，B 等 A");
        log("");
        log("  避免方法：");
        log("    ① 统一加锁顺序（都先锁 A 再锁 B，消灭循环等待）");
        log("    ② 使用 tryLock() 加超时（拿不到就走，不死等）");
        log("    ③ 减少锁粒度 / 用无锁并发类");
        log("    ④ 避免嵌套锁");
    }

    // ================================================================
    //  Q5: 乐观锁和悲观锁? 如何实现乐观锁?
    // ================================================================
    static void Q5_OptimisticPessimisticLock() {
        printH2("Q5: 乐观锁和悲观锁？如何实现乐观锁？");
        log("");
        log("  悲观锁 = 默认别人会跟我抢，上来就锁（synchronized, ReentrantLock）");
        log("  乐观锁 = 默认没人抢，改的时候再看有没有冲突（CAS, 版本号）");
        log("");
        log("  比喻：");
        log("    悲观锁 = 进公共厕所反锁门（别人进不来）");
        log("    乐观锁 = 改文件前记录版本号，提交时检查版本号是否变了");
        log("");
        log("  乐观锁实现——CAS (Compare And Swap)：");
        log("    「比较并交换」——无锁化更新。");
        log("    如果内存值 == 期望值 → 换成新值 → 成功");
        log("    如果内存值 != 期望值 → 有人改过 → 失败，重试");

        // 演示 CAS
        AtomicInteger atomic = new AtomicInteger(0);
        boolean success = atomic.compareAndSet(0, 1);
        log("    AtomicInteger(0).compareAndSet(0,1) → " + success + " " + atomic.get());
        log("    → CAS + 自旋 是乐观锁的 Java 实现方式。");
        log("    缺点：ABA 问题（用 AtomicStampedReference 解决，加版本号）。");
    }

    // ================================================================
    //  Q6: sleep() 和 wait() 区别
    // ================================================================
    static void Q6_SleepVsWait() {
        printH2("Q6: sleep() 和 wait() 的区别和共同点？");
        log("");
        log("  ┌──────────┬────────────────┬──────────────────┐");
        log("  │          │ Thread.sleep() │ Object.wait()   │");
        log("  ├──────────┼────────────────┼──────────────────┤");
        log("  │ 所属类   │ Thread 静态方法 │ Object 实例方法  │");
        log("  │ 释放锁   │ 不释放锁！     │ 释放锁！         │");
        log("  │ 唤醒方式 │ 时间到自动醒   │ notify/notifyAll │");
        log("  │ 使用位置 │ 任意位置       │ 只能在同步块内   │");
        log("  │ 用途     │ 暂停执行       │ 线程间等待/通知   │");
        log("  └──────────┴────────────────┴──────────────────┘");
        log("");
        log("  共同点：都会让线程进入 WAITING/TIMED_WAITING 状态。");
        log("  核心区别：sleep 拿着锁睡，wait 放下锁等！");

        // 演示
        Object lock = new Object();
        new Thread(() -> {
            synchronized (lock) {
                log("    线程1 拿到锁, sleep 2 秒（不释放锁）");
                try { Thread.sleep(2000); } catch (Exception e) {}
                log("    线程1 睡醒，释放锁");
            }
        }).start();

        try { Thread.sleep(300); } catch (Exception e) {}
        new Thread(() -> {
            synchronized (lock) {
                log("    线程2 拿到锁了 ← 证明 sleep 不释放锁！");
                log("    线程2: 调用 lock.wait() 释放锁");
                try { lock.wait(500); } catch (Exception e) {}
                log("    线程2: 被唤醒");
            }
        }).start();
        try { Thread.sleep(3500); } catch (Exception e) {}
    }

    // ================================================================
    //  Q7: JMM 内存模型 + volatile
    // ================================================================
    static void Q7_JMM_and_Volatile() {
        printH2("Q7: JMM（Java 内存模型）和 volatile 关键字");
        log("");
        log("  JMM（Java Memory Model）是个抽象规范，定义：");
        log("    - 主内存（堆）：所有线程共享，变量真身在这里");
        log("    - 工作内存（线程栈）：线程私有，存变量的拷贝副本");
        log("");
        log("  线程修改变量流程：");
        log("    读：主内存 → copy 到工作内存 → 使用");
        log("    写：工作内存修改 → 写回主内存（时间不定！）");
        log("    → 这就是「可见性问题」：线程 A 改了，线程 B 看不到！");
        log("");
        log("  volatile 解决了什么？");
        log("    1. 可见性：volatile 变量每次必须从主内存读，写完立刻写回");
        log("    2. 禁止指令重排：volatile 前后插入内存屏障，阻止重排");
        log("    → 不保证原子性！（i++ 还是不行，要用 AtomicInteger / synchronized）");
        log("");
        log("  volatile 经典用法：状态标志位");
        log("    volatile boolean running = true;");
        log("    // 一个线程改 running=false，另一个线程立刻能看到");
    }

    // ================================================================
    //  Q8: Java 内存区域和 JMM 区别
    // ================================================================
    static void Q8_MemoryRegionVsJMM() {
        printH2("Q8: Java 内存区域和 JMM 有何区别？");
        log("  这是两个完全不同层面的概念：");
        log("");
        log("  Java 内存区域（JVM 运行时数据区）：");
        log("    堆、栈、方法区、程序计数器... → 是「物理」存在的内存划分");
        log("    → JVM 规范定义的，真实存在的内存结构。");
        log("");
        log("  JMM（Java 内存模型）：");
        log("    主内存/工作内存、可见性、有序性、原子性 → 是「逻辑」规范");
        log("    → 抽象模型，规定了多线程如何访问内存的规则。");
        log("");
        log("  ┌────────────────┬────────────────────────────┐");
        log("  │ 内存区域       │ 讲的是：数据放在哪          │");
        log("  │ JMM            │ 讲的是：多线程怎么看到数据  │");
        log("  └────────────────┴────────────────────────────┘");
    }

    // ================================================================
    //  Q9: happens-before 原则
    // ================================================================
    static void Q9_HappensBefore() {
        printH2("Q9: happens-before 原则");
        log("  如果操作 A happens-before 操作 B，则：");
        log("    A 的结果对 B 可见，且 A 的执行顺序在 B 之前。");
        log("    → JMM 靠这个判断两个操作是否存在「数据竞争」。");
        log("");
        log("  8 条规则：");
        log("    1. 程序次序：同一线程内，写在前面的先于后面的");
        log("    2. 锁规则：unlock 先于后面的 lock");
        log("    3. volatile：写 volatile 先于后面的读 volatile");
        log("    4. 传递性：A hb B, B hb C → A hb C");
        log("    5. 线程启动：线程 start() 先于 run() 里的操作");
        log("    6. 线程中断：interrupt() 先于检测到中断");
        log("    7. 线程终结：线程内所有操作先于 join() 返回");
        log("    8. 对象终结：构造完成先于 finalize()");
    }

    // ================================================================
    //  Q10: synchronized 关键字的作用
    // ================================================================
    static void Q10_Synchronized() {
        printH2("Q10: synchronized 关键字的作用");
        log("  一句话：保证同一时刻只有一个线程执行被它保护的代码块。");
        log("  三种用法：");
        log("    1. 修饰实例方法 → 锁是 this（当前对象）");
        log("    2. 修饰静态方法 → 锁是 Class 对象");
        log("    3. 修饰代码块   → 锁是括号里的对象");
        log("");
        log("  三个保证：原子性 + 可见性 + 有序性");
    }

    // ================================================================
    //  Q11: synchronized 和 ReentrantLock 的区别
    // ================================================================
    static void Q11_SynchronizedVsReentrantLock() {
        printH2("Q11: synchronized 和 ReentrantLock 的区别");
        log("");
        log("  ┌─────────────────┬────────────────┬──────────────┐");
        log("  │                 │ synchronized   │ ReentrantLock│");
        log("  ├─────────────────┼────────────────┼──────────────┤");
        log("  │ 层面            │ JVM 内置关键字 │ API 层面     │");
        log("  │ 释放锁          │ 自动释放       │ 必须手动 unlock│");
        log("  │ 中断响应        │ 不支持(死等)   │ lockInterruptibly│");
        log("  │ 超时获取        │ 不支持         │ tryLock(time) │");
        log("  │ 公平锁          │ 非公平         │ 公平/非公平可选│");
        log("  │ 条件变量        │ 一个(隐式)     │ 多个 Condition│");
        log("  │ 性能(Jdk6+)     │ 几乎一样       │ 几乎一样       │");
        log("  │ 什么时候用       │ 简单场景       │ 需要高级功能  │");
        log("  └─────────────────┴────────────────┴──────────────┘");
        log("");
        log("  一句话：需要 超时/中断/公平锁/多条件 → ReentrantLock");
        log("          简单同步 → synchronized（自动释放，更安全）");
    }

    // ================================================================
    //  Q12: synchronized 和 volatile 的区别
    // ================================================================
    static void Q12_SynchronizedVsVolatile() {
        printH2("Q12: synchronized 和 volatile 的区别");
        log("");
        log("  ┌──────────┬──────────────────┬──────────────────┐");
        log("  │          │ volatile         │ synchronized     │");
        log("  ├──────────┼──────────────────┼──────────────────┤");
        log("  │ 原子性   │ ✗ 不保证        │ ✓ 保证           │");
        log("  │ 可见性   │ ✓                │ ✓                │");
        log("  │ 有序性   │ ✓(禁止指令重排)  │ ✓                │");
        log("  │ 性能     │ 高(无锁)         │ 有锁开销         │");
        log("  │ 适用场景 │ 状态标记         │ 复合操作         │");
        log("  └──────────┴──────────────────┴──────────────────┘");
        log("");
        log("  核心区别：volatile 不保证原子性，synchronized 保证。");
        log("    volatile int i=0; i++;  ← 不行！读-改-写,volatile 管不了");
        log("    volatile boolean flag;  ← 对！标志位赋值是原子的");
    }

    // ================================================================
    //  Q13: synchronized 底层原理
    // ================================================================
    static void Q13_SynchronizedUnderlying() {
        printH2("Q13: synchronized 关键字的底层原理");
        log("");
        log("  每个 Java 对象头里都有一个 Mark Word，存锁信息：");
        log("");
        log("  锁升级过程（JDK 6+ 优化，无锁 → 偏向 → 轻量 → 重量）：");
        log("");
        log("  ① 无锁：没线程竞争，对象头就是普通 Mark Word");
        log("");
        log("  ② 偏向锁：第一个线程获得锁后，把线程 ID 写进对象头。");
        log("     同线程再来 → 检查 ID 是自己 → 直接进，不加锁！");
        log("     → 适合「基本单线程」场景，几乎零开销。");
        log("");
        log("  ③ 轻量级锁（CAS自旋）：有另一线程竞争时，撤销偏向锁。");
        log("     线程在用户态用 CAS 抢锁 → 抢不到的短暂自旋等待。");
        log("     → 适合「锁持有时间短」的场景，不自旋太久。");
        log("");
        log("  ④ 重量级锁（mutex）：自旋太久还抢不到 → 升级重量级锁。");
        log("     没抢到的线程进入阻塞(内核态)，等操作系统唤醒。");
        log("     → JVM 用 C++ 的 monitorenter/monitorexit 指令实现");
        log("");
        log("  只能升级不能降级！优化思路：让锁多停留在偏向/轻量级阶段。");
    }

    // ================================================================
    //  Q14: ThreadLocal 关键字 + 内存泄露
    // ================================================================
    static void Q14_ThreadLocal() {
        printH2("Q14: ThreadLocal 关键字的作用，内存泄露问题");
        log("");
        log("  ThreadLocal = 每个线程自己的「私人抽屉」");
        log("    同一个 ThreadLocal 对象，不同线程 get/set 互不影响。");
        log("");
        log("  原理：");
        log("    Thread 内部有个 ThreadLocalMap（本质是个 Entry[] 数组）");
        log("    key = ThreadLocal 的弱引用，value = 你存的值");
        log("    get() → 拿到当前线程 → 查自己的 ThreadLocalMap → 返回 value");
        log("");

        // 演示
        ThreadLocal<String> tl = new ThreadLocal<>();
        tl.set("主线程的私有数据");
        new Thread(() -> {
            tl.set("子线程的私有数据");
            log("    子线程 get: " + tl.get());  // 子线程的数据
        }).start();
        try { Thread.sleep(200); } catch (Exception e) {}
        log("    主线程 get: " + tl.get());   // 主线程自己的数据
        log("    → 同一个 tl，不同线程拿到不同的值！");
        log("");
        log("  内存泄露问题：");
        log("    ThreadLocalMap 的 key 是弱引用，GC 可能回收 key");
        log("    但 value 是强引用，GC 回收不了 → 内存泄露！");
        log("    → 解决办法：用完后必须调用 tl.remove()！");
        log("    → 也正因为如此，阿里规约要求 ThreadLocal 必须 try-finally remove。");
        tl.remove(); // 清理
    }

    // ================================================================
    //  Q15: 线程池有什么用？为什么不推荐内置线程池？
    // ================================================================
    static void Q15_ThreadPool() {
        printH2("Q15: 线程池有什么用？为什么不推荐内置线程池？");
        log("");
        log("  线程池 = 一个装线程的池子，用完不销毁，放回去复用。");
        log("");
        log("  好处：");
        log("    1. 降低创建/销毁开销（不用 new 完就扔）");
        log("    2. 控制并发数（不会 1000 个请求开 1000 个线程把 CPU 打爆）");
        log("    3. 便于管理（统一监控、调优）");
        log("");
        log("  不推荐 Executors 内置线程池的原因(《阿里规约》)：");
        log("    newFixedThreadPool → 无界队列(LinkedBlockingQueue)，OOM");
        log("    newCachedThreadPool → 最大线程数 Integer.MAX_VALUE，OOM");
        log("    newSingleThreadExecutor → 同 Fixed，无界队列 OOM");
        log("    newScheduledThreadPool → 最大线程数 Integer.MAX_VALUE");
        log("");
        log("  → 必须用 ThreadPoolExecutor 手动指定参数！");
    }

    // ================================================================
    //  Q16: 线程池有哪些参数？阻塞队列有几种？拒绝策略有几种？
    // ================================================================
    static void Q16_ThreadPoolParams() {
        printH2("Q16: 线程池参数、阻塞队列、拒绝策略");
        log("");
        log("  7 个参数（ThreadPoolExecutor 构造器）：");
        log("    ① corePoolSize    核心线程数（常驻）");
        log("    ② maximumPoolSize 最大线程数");
        log("    ③ keepAliveTime   非核心线程空闲存活时间");
        log("    ④ unit            时间单位");
        log("    ⑤ workQueue       阻塞队列（放等待的任务）");
        log("    ⑥ threadFactory   线程工厂（命名用）");
        log("    ⑦ handler         拒绝策略");
        log("");
        log("  阻塞队列 4 种：");
        log("    ArrayBlockingQueue    有界数组队列");
        log("    LinkedBlockingQueue   可选有界链表队列(默认无界→危险)");
        log("    SynchronousQueue      不存任务，直接交给线程（Cached池用）");
        log("    PriorityBlockingQueue 优先级无界队列");
        log("");
        log("  拒绝策略 4 种：");
        log("    AbortPolicy         抛异常(默认)");
        log("    CallerRunsPolicy    让提交任务的线程自己执行");
        log("    DiscardPolicy       直接丢弃，不报错");
        log("    DiscardOldestPolicy 丢弃队列头(最早的的任务)");
    }

    // ================================================================
    //  Q17: 线程池处理任务的流程
    // ================================================================
    static void Q17_ThreadPoolFlow() {
        printH2("Q17: 线程池处理任务的流程");
        log("");
        log("  任务进来 →");
        log("    Step1: 核心线程有空吗？ → 有 → 核心线程执行");
        log("                                 ↓ 没有");
        log("    Step2: 队列满了吗？     → 没满 → 扔进队列等着");
        log("                                 ↓ 满了");
        log("    Step3: 线程数 < 最大吗？ → 是 → 创建新线程执行");
        log("                                 ↓ 否（已达最大）");
        log("    Step4: 执行拒绝策略");
    }

    // ================================================================
    //  Q18: Runnable 和 Callable 的区别
    // ================================================================
    static void Q18_RunnableVsCallable() {
        printH2("Q18: Runnable 和 Callable 的区别");
        log("");
        log("  ┌──────────┬──────────────────┬──────────────────┐");
        log("  │          │ Runnable         │ Callable         │");
        log("  ├──────────┼──────────────────┼──────────────────┤");
        log("  │ 返回值   │ void (无返回值)  │ 有返回值(泛型)   │");
        log("  │ 异常     │ 不能抛 checked   │ 可以抛 checked   │");
        log("  │ 方法     │ run()            │ call()           │");
        log("  │ 提交     │ executor.execute │ executor.submit  │");
        log("  └──────────┴──────────────────┴──────────────────┘");
        log("  → 需要返回值/抛异常 → Callable；否则 → Runnable");
    }

    // ================================================================
    //  Q19: 如何给线程池命名？为什么？
    // ================================================================
    static void Q19_ThreadPoolNaming() {
        printH2("Q19: 如何给线程池命名？为什么建议命名？");
        log("");
        log("  为什么：出问题看线程堆栈，全是 pool-1-thread-1 ... 根本不知道是哪的池子。");
        log("  命名后才能快速定位！");
        log("");
        log("  方法① 用 Guava ThreadFactoryBuilder：");
        log("    ThreadFactoryBuilder.setNameFormat(\"order-pool-%d\").build()");
        log("");
        log("  方法② 自定义 ThreadFactory：");
        log("    new ThreadFactory() {");
        log("        AtomicInteger n = new AtomicInteger(1);");
        log("        public Thread newThread(Runnable r) {");
        log("            return new Thread(r, \"my-pool-\" + n.getAndIncrement());");
        log("        }");
        log("    }");
    }

    // ================================================================
    //  Q20: 如何动态修改线程池参数？
    // ================================================================
    static void Q20_DynamicThreadPool() {
        printH2("Q20: 如何动态修改线程池参数？");
        log("");
        log("  ThreadPoolExecutor 提供了 setter：");
        log("    setCorePoolSize(n)     → 修改核心线程数");
        log("    setMaximumPoolSize(n)  → 修改最大线程数");
        log("    setKeepAliveTime(t, u) → 修改存活时间");
        log("    setRejectedExecutionHandler(h) → 修改拒绝策略");
        log("");
        log("  → 但不能改队列容量，因为队列是在构造时确定的。");
        log("");
        log("  实际方案：搭配配置中心(Nacos/Apollo)监听配置变更来动态调参。");
    }

    // ================================================================
    //  Q21: AQS 的作用是什么?
    // ================================================================
    static void Q21_AQS() {
        printH2("Q21: AQS 的作用是什么？为什么要有 AQS？");
        log("");
        log("  AQS(AbstractQueuedSynchronizer) = 一个用来构建锁和同步器的框架。");
        log("");
        log("  为什么要有：");
        log("    ReentrantLock、Semaphore、CountDownLatch、ReentrantReadWriteLock");
        log("    这些锁的底层都用到了「排队 + 唤醒」的通用逻辑。");
        log("    如果没有 AQS，每个锁都得自己写一遍排队逻辑 → 重复、易错。");
        log("    → AQS 把这部分抽出来，你只需实现 tryAcquire/tryRelease 即可。");
        log("");
        log("  核心组成：");
        log("    一个 int state（状态）");
        log("    一个 FIFO 双向队列（CLH 变体，存等待线程）");
    }

    // ================================================================
    //  Q22: AQS 组件有哪些？
    // ================================================================
    static void Q22_AQSComponents() {
        printH2("Q22: AQS 组件有哪些？");
        log("");
        log("  基于 AQS 实现的组件：");
        log("    ReentrantLock         可重入锁");
        log("    Semaphore             信号量（限流）");
        log("    CountDownLatch        倒计时门闩");
        log("    CyclicBarrier         循环栅栏（内部用 ReentrantLock+Condition）");
        log("    ReentrantReadWriteLock 读写锁");
        log("    ThreadPoolExecutor    线程池（Worker 继承 AQS）");
    }

    // ================================================================
    //  Q23: AQS 原理
    // ================================================================
    static void Q23_AQSPrinciple() {
        printH2("Q23: AQS 原理了解么？");
        log("");
        log("  AQS 核心数据结构：");
        log("    volatile int state  ← 同步状态（0=释放/1=锁定/...）");
        log("    volatile Node head  ← CLH 队列头");
        log("    volatile Node tail  ← CLH 队列尾");
        log("");
        log("  排队的线程被包装成 Node，串成双向链表（FIFO）。");
        log("");
        log("  获取锁(acquire)：");
        log("    1. tryAcquire() 尝试拿锁（子类实现）");
        log("    2. 拿到 → 直接返回（快路径）");
        log("    3. 拿不到 → CAS 把自己加到队列尾部 → park() 挂起等待");
        log("");
        log("  释放锁(release)：");
        log("    1. tryRelease() 尝试释放（子类实现）");
        log("    2. 释放成功 → unpark 唤醒队列头的下一个节点");
        log("    3. 被唤醒的线程再次 tryAcquire 抢锁");
        log("");
        log("  全用 CAS + volatile + park/unpark，不依赖 synchronized。");
    }

    // ================================================================
    //  Q24: Semaphore & CountDownLatch
    // ================================================================
    static void Q24_Semaphore_CountDownLatch() throws Exception {
        printH2("Q24: Semaphore 和 CountDownLatch");
        log("");
        log("  Semaphore（信号量）= 停车场的 N 个车位");
        log("    acquire() = 抢一个车位(没抢到就等着)");
        log("    release() = 离开，空出一个车位");
        log("    → 用于：限流，同一时间只允许 N 个线程访问。");
        log("");

        // 演示 Semaphore
        Semaphore sem = new Semaphore(2);  // 2 个许可
        for (int i = 0; i < 3; i++) {
            final int id = i;
            new Thread(() -> {
                try {
                    sem.acquire();
                    log("    线程" + id + " 获得许可，工作中...");
                    Thread.sleep(200);
                    log("    线程" + id + " 释放许可");
                } catch (Exception e) {}
                finally { sem.release(); }
            }).start();
        }
        Thread.sleep(800);

        log("");
        log("  CountDownLatch（倒计时门闩）= 等人齐了一起出发");
        log("    主线程 await() 等着");
        log("    每个子线程完成任务 countDown() 一次");
        log("    计数器归零 → 主线程被唤醒");
        log("    → 用于：等所有子线程就绪/完成后再继续。");
        log("");

        // 演示 CountDownLatch
        int workerCount = 3;
        CountDownLatch latch = new CountDownLatch(workerCount);
        for (int i = 0; i < workerCount; i++) {
            final int id = i;
            new Thread(() -> {
                log("    工人" + id + " 完成工作");
                latch.countDown();
            }).start();
        }
        latch.await();
        log("    → 3 个工人都完成了，主线程可以继续！");
    }

    // ================================================================
    //  Q25: CyclicBarrier
    // ================================================================
    static void Q25_CyclicBarrier() throws Exception {
        printH2("Q25: CyclicBarrier 有什么用？原理是什么？");
        log("");
        log("  CyclicBarrier（循环栅栏）= 团建等人齐了再出发");
        log("    N 个线程互相等待，凑齐 N 个后一起继续。");
        log("    用完了计数器自动重置，可以循环使用（所以叫 Cyclic）。");
        log("");
        log("  CountDownLatch vs CyclicBarrier：");
        log("    Latch 是一次性的，Barrier 可循环");
        log("    Latch 是等着别人倒数，Barrier 是所有人互相等");

        // 演示 CyclicBarrier
        int partyCount = 3;
        CyclicBarrier barrier = new CyclicBarrier(partyCount, () ->
            log("    → 3 人到齐了，出发！")
        );
        for (int i = 0; i < partyCount; i++) {
            final int id = i;
            new Thread(() -> {
                try {
                    log("    成员" + id + " 到达集合点...");
                    barrier.await();  // 互相等待
                    log("    成员" + id + " 一起出发！");
                } catch (Exception e) {}
            }).start();
        }
        Thread.sleep(500);
    }

    // ================================================================
    //  Q26: CompletableFuture
    // ================================================================
    static void Q26_CompletableFuture() throws Exception {
        printH2("Q26: 多个任务的编排怎么做？CompletableFuture？");
        log("");
        log("  CompletableFuture = 异步任务的「乐高积木」，可以拼装、串联、合并。");
        log("");
        log("  核心能力：");
        log("    thenApply()    一个任务的结果传给下一个（有返回值）");
        log("    thenAccept()   一个任务的结果传给下一个（无返回值）");
        log("    thenRun()      不关心结果，就是接着跑下一个");
        log("    thenCombine()  等两个都完成，合并结果");
        log("    allOf()        等所有任务都完成");
        log("    anyOf()        任意一个完成就行");
        log("    exceptionally() 异常处理");
        log("");

        // 演示
        CompletableFuture<String> task1 = CompletableFuture.supplyAsync(() -> {
            return "查用户信息";
        });
        CompletableFuture<String> task2 = CompletableFuture.supplyAsync(() -> {
            return "查订单信息";
        });

        // 等两个都完成 → 合并结果
        CompletableFuture<String> merged = task1.thenCombine(task2, (user, order) -> {
            return user + " + " + order + " → 组装成页面";
        });

        String result = merged.get();
        log("    CompletableFuture 合并结果: " + result);
        log("");
        log("  → 这就是异步编排：任务 A 和 B 并行执行，拿到结果后合并。");
        log("    用 Future 得自己 join+拼接，CompletableFuture 一行搞定。");
    }

    // ================================================================
    //  Helper
    // ================================================================
    static void printHR(String title) {
        String line = "========================================";
        System.out.println("\n" + line);
        System.out.println("  " + title);
        System.out.println(line);
    }
    static void printH2(String title) {
        System.out.println("\n┌─────────────────────────────────────────────────────────┐");
        System.out.println("│ " + title);
        System.out.println("└─────────────────────────────────────────────────────────┘");
    }
}
