/*
 * ============================================================
 *  通俗理解 abstract 与 final 的区别
 * ============================================================
 *
 *  用"手机系统"来打比方：
 *
 *  abstract = "Android 开源系统"（半成品，必须继续开发才能用）
 *             Google 只定义了框架，三星/小米/华为各做各的版本。
 *             → 逼你继承扩展，必须被重写
 *
 *  final    = "iOS 闭源系统"（成品，苹果锁死，不准改）
 *             谁也别想继承或修改。
 *             → 阻止继承扩展，不准重写
 *
 *  两者是死对头：abstract 逼你动，final 不让你动，水火不容！
 *
 *  ┌──────────────────────────────────────────────────────┐
 *  │                                                      │
 *  │  abstract（开放的）        final（封闭的）            │
 *  │  ──────────────           ────────────               │
 *  │  类：必须被继承            类：不能被继承             │
 *  │  方法：必须被重写          方法：不能被重写           │
 *  │  变量：无此用法            变量：值不可改             │
 *  │                                                      │
 *  │  不能同时修饰同一个东西（编译报错）                   │
 *  │                                                      │
 *  └──────────────────────────────────────────────────────┘
 */

// =============================================================
//  对比一：final 变量 —— 值不能改
// =============================================================
class Config {
    // final 变量：赋值后不能再改
    final String appName = "我的应用";
    final int maxUsers;
    final static String VERSION = "1.0.0";   // 常用: public static final 常量

    public Config(int maxUsers) {
        this.maxUsers = maxUsers;   // 可以在构造器中初始化 final 变量
    }

    public void tryChange() {
        // appName = "新名字";    <- 编译报错！final 变量不可改
        // maxUsers = 999;        <- 编译报错！
        System.out.println("final 变量 appName = " + appName + "（不可改）");
        System.out.println("final 变量 maxUsers = " + maxUsers + "（不可改）");
        System.out.println("static final 常量 VERSION = " + VERSION);
    }
}

// =============================================================
//  对比二：final 方法 —— 子类不能重写
// =============================================================
class PhoneTemplate {

    // 普通方法：子类可以重写
    public void call() {
        System.out.println("📞 打电话...");
    }

    // final 方法：子类不能重写
    public final void boot() {
        System.out.println("🔋 手机开机（final 方法，所有手机开机流程都一样，不准改）");
        checkHardware();
        loadKernel();
    }

    private void checkHardware() {
        System.out.println("   -> 检测硬件...OK");
    }

    private void loadKernel() {
        System.out.println("   -> 加载内核...OK");
    }
}

class SmartPhone extends PhoneTemplate {

    @Override
    public void call() {
        System.out.println("📞 智能手机打电话（可以重写）");
    }

    // @Override
    // public void boot() {}    <- 编译报错！final 方法不能重写
}

// =============================================================
//  对比三：final 类 —— 不能被继承
// =============================================================
final class SystemCore {
    // 这个类的所有方法自动是 final（但不会显式标出来）
    public void run() {
        System.out.println("系统内核运行中...");
    }
}

// class MyCore extends SystemCore {}   <- 编译报错！final 类不能继承

// =============================================================
//  对比四：abstract 类/方法 —— 必须被继承/重写
// =============================================================
abstract class Animal {
    // 抽象方法：子类必须实现
    public abstract void makeSound();

    // 普通方法可以调用抽象方法（模板方法模式）
    public void speak() {
        System.out.print("这只动物说: ");
        makeSound();
    }
}

class Dog extends Animal {
    @Override
    public void makeSound() {
        System.out.println("汪汪汪！");
    }
}

// =============================================================
//  对比五：abstract 和 final 水火不容
// =============================================================
// abstract final class Impossible {}       <- 编译报错！不能同时
// abstract class X { abstract final void y(); }  <- 编译报错！不能同时

// =============================================================
//  主程序
// =============================================================
public class AbstractVsFinal {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  abstract vs final  —— 区别演示");
        System.out.println("========================================");

        // ========== 一、final 变量 ==========
        System.out.println("\n========== 一、final 变量：值不可改 ==========");
        Config config = new Config(100);
        config.tryChange();
        System.out.println("常见写法: public static final 定义常量，如 Config.VERSION = " + Config.VERSION);

        // ========== 二、final 方法 ==========
        System.out.println("\n========== 二、final 方法：子类不能重写 ==========");
        SmartPhone sp = new SmartPhone();
        sp.call();   // 重写版
        sp.boot();   // final 版（继承自父类，不能重写）
        System.out.println("boot() 方法被子类继承但不能修改——开机的安全流程不准动");

        // ========== 三、final 类 ==========
        System.out.println("\n========== 三、final 类：不能被继承 ==========");
        SystemCore core = new SystemCore();
        core.run();
        System.out.println("SystemCore 是 final 的，谁也不能 extends 它");

        // ========== 四、abstract 方法 ==========
        System.out.println("\n========== 四、abstract 方法：必须被重写 ==========");
        Dog dog = new Dog();
        dog.speak();
        System.out.println("abstract 方法逼子类实现——不写就编译报错");

        // ========== 五、水火不容 ==========
        System.out.println("\n========== 五、abstract 和 final 水火不容 ==========");
        System.out.println("abstract final class  -> 编译报错！");
        System.out.println("abstract final void   -> 编译报错！");
        System.out.println("一个要你继承，一个不让继承，逻辑矛盾");

        // ========== 总结 ==========
        System.out.println("\n========================================");
        System.out.println("  总结");
        System.out.println("========================================");
        System.out.println("维度       abstract（打开）     final（锁死）");
        System.out.println("类         必须被继承            不能被继承");
        System.out.println("方法       必须被子类重写        不能被重写");
        System.out.println("变量       无此用法              赋值后不可改");
        System.out.println("========================================");
        System.out.println("常见组合：");
        System.out.println("  private static final  → 类内部常量，外部不可见不可改");
        System.out.println("  public static final   → 全局常量，如 Math.PI");
        System.out.println("  final + 构造器传参     → 每个对象不可变，但不同对象值可不同");
        System.out.println("========================================");
        System.out.println("一句记完：");
        System.out.println("  abstract 是开源的 Android，欢迎修改拓展；");
        System.out.println("  final 是闭源的 iOS，锁死不准动。");
    }
}
