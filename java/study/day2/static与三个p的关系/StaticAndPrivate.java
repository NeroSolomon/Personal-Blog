/*
 * ============================================================
 *  通俗理解 static 与 private 的区别
 * ============================================================
 *
 *  static   → "属于谁" —— 是类所有 还是 实例所有？
 *             类成员（static）不依赖 new 出来的对象，
 *             直接 类名.xxx 就能用，所有实例共享同一份。
 *
 *  private  → "谁能看" —— 是公开 还是 私有？
 *             只在本类内部可见，外部类访问会报错。
 *
 *  ⚠ 关键点：static 和 private 是两个维度，完全不冲突！
 *    static 决定"放在哪"，private 决定"谁能碰"。
 *
 *  常见误解："static 也限制了访问" — 这是错的！
 *  public static 谁都能用，private static 才限制。
 *
 * ============================================================
 *
 *  类比解释：
 *
 *  static           → 小区的"大门"
 *                     所有人走同一个门，属于小区（类），
 *                     不属于某户人家（对象）。
 *  非 static        → 各户的"防盗门钥匙"
 *                     每家不同，必须拿到具体一户的钥匙。
 *
 *  public static    → 小区门口的"公告栏"
 *                     谁都能看。Class.publicStatic
 *  private static   → 小区物业的"总电表读数"
 *                     只有物业（本类）能看到，不是谁都能看！
 *
 * ============================================================
 */

/*
 * --- 场景一：有 private 保护的 static ---
 * 计数器只能通过提供的方法访问，外部无法随意篡改。
 */
class SafeCounter {

    // private static：数据是"类级别"的，但"不对外暴露"
    // 只有本类自己的方法能碰，外部代码无法直接 SafeCounter.count = 999
    private static int count = 0;

    // 外部可以通过 public 方法间接访问
    public static int getCount() {
        return count;
    }

    public static void increment() {
        count++;
    }
}

/*
 * --- 场景二：没有 private 保护的 static ---
 * 谁都能直接修改，数据不安全。
 */
class UnsafeCounter {
    // 没有修饰符 = default，同包下的任何类都能改
    // 甚至直接写 UnsafeCounter.count = -100
    static int count = 0;
}

/*
 * --- 场景三：static 方法 + private 方法 ---
 */
class Helper {
    // public static：谁都能调，不依赖对象
    public static void greet(String name) {
        System.out.println("你好 " + name + "！（谁都喊得动我）");
    }

    // private static：只能在本类内部用，外部调不了
    // 用于"类级别的内部工具函数"
    private static void internalLog(String msg) {
        System.out.println("[内部日志] " + msg);
    }

    // 通过 public 方法间接调用 private static
    public static void doTask() {
        internalLog("开始执行任务...");
        System.out.println("任务完成！");
        // 外部看不到 internalLog，但本类可以用
    }
}

/*
 * --- 场景四：实例成员 vs 类成员 ---
 */
class User {
    // 实例变量（非 static）：每个对象独有一份
    private String name;

    // 类变量（static）：所有对象共享一份
    // 加 private 防止外部直接 User.totalUsers = 0 破坏数据
    private static int totalUsers = 0;

    public User(String name) {
        this.name = name;
        totalUsers++;  // 每 new 一个就 +1
    }

    public String getName() {
        return name;
    }

    public static int getTotalUsers() {
        return totalUsers;
    }
}

// 主程序
public class StaticAndPrivate {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   static 与 private 区别演示");
        System.out.println("========================================");

        // ========== 一、private static 封装演示 ==========
        System.out.println("\n--- 一、private static 的作用 ---");
        System.out.println("初始化 count = " + SafeCounter.getCount());

        SafeCounter.increment();
        SafeCounter.increment();
        SafeCounter.increment();
        System.out.println("increment 3 次后 = " + SafeCounter.getCount());

        // SafeCounter.count = 999;  ← 编译报错！外部不能直接改
        System.out.println("无法直接 SafeCounter.count = 999，编译报错 ✅");

        // ========== 二、没有 private 的危险 ==========
        System.out.println("\n--- 二、没有 private 的后果 ---");
        UnsafeCounter.count = -100; // 合法！任何人都能改
        System.out.println("UnsafeCounter.count 被直接改成 = " + UnsafeCounter.count);
        System.out.println("⚠ 数据被随意篡改，不安全！");

        // ========== 三、static 方法的访问级别 ==========
        System.out.println("\n--- 三、public static vs private static ---");
        Helper.greet("小明");  // public static：随便调
        Helper.doTask();       // 内部调用了 private static 方法
        // Helper.internalLog("test");  ← 编译报错！外部不可见

        // ========== 四、实例 vs 类成员 ==========
        System.out.println("\n--- 四、实例成员 vs 类成员 ---");
        User u1 = new User("张三");
        User u2 = new User("李四");
        System.out.println("用户1: " + u1.getName());
        System.out.println("用户2: " + u2.getName());
        System.out.println("总用户数（static）: " + User.getTotalUsers());
        // u1.getName()   ← 必须通过对象
        // User.getTotalUsers() ← 通过类名，不依赖对象

        // ========== 总结 ==========
        System.out.println("\n========================================");
        System.out.println("  总结");
        System.out.println("========================================");
        System.out.println("修饰符      含义              访问方式");
        System.out.println("static      属于类            类名.xxx");
        System.out.println("非 static   属于实例           对象.xxx");
        System.out.println("private     只在本类可见       (不可在外部访问)");
        System.out.println("========================================");
        System.out.println();
        System.out.println("组合效果：");
        System.out.println("  private static  → 类级别的私有数据，外部不可见");
        System.out.println("                  → 用于需要封装、不可随意修改的场景");
        System.out.println("  public static   → 类级别的公开数据/工具方法");
        System.out.println("                  → 如 Math.PI, Integer.MAX_VALUE");
        System.out.println("  private（非static） → 每个对象的私有属性");
        System.out.println("========================================");
        System.out.println("口诀：static 管归属，private 管权限，两者不冲突！");
    }
}
