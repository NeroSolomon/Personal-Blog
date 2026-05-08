/*
 * ============================================================
 *  通俗理解 抽象类 与 接口 的区别
 * ============================================================
 *
 *  用"门"来打比方：
 *
 *  ===== 抽象类 = "门" 本身（is-a 关系） =====
 *  一扇门有材质（木门/铁门/玻璃门）、有颜色、有高度。
 *  这些都是"门"与生俱来的属性。
 *  你永远买不到一扇"门"——你买的是"木门"、"防盗门"这些具体的门。
 *  抽象类 = 定义了门的"骨架"，有状态，有构造器。
 *
 *  ===== 接口 = "能做什么"（can-do 关系） =====
 *  一扇门可以"能上锁"、"能防火"、"能自动关门"。
 *  这些都是"能力"标签，不是属性。
 *  一扇门可以同时贴上多个标签：既能上锁、又能防火。
 *  接口 = 一份"能力合同"，只定义能干什么，不存状态，没有构造器。
 *
 *  ┌────────────────────────────────────────────────────┐
 *  │                                                    │
 *  │  抽象类 (abstract class)                            │
 *  │  - 可以有构造器                                     │
 *  │  - 可以有成员变量（状态）                           │
 *  │  - 有抽象方法也有普通方法                           │
 *  │  - 单继承：一个子类只能 extends 一个               │
 *  │  - is-a 关系："防盗门 是一扇 门"                   │
 *  │                                                    │
 *  │  接口 (interface)                                   │
 *  │  - 没有构造器                                       │
 *  │  - 不能有实例变量（只能有 public static final 常量）│
 *  │  - 方法默认 public abstract（Java 8+ 可有 default）│
 *  │  - 多实现：一个类可以 implements 多个              │
 *  │  - can-do 关系："防盗门 能上锁、能防火"            │
 *  │                                                    │
 *  └────────────────────────────────────────────────────┘
 *
 *  核心区别一句话：
 *    抽象类 = 你「是什么」
 *    接口   = 你「能干什么」
 */

// =============================================================
//  抽象类：定义"门"的骨架（is-a）
// =============================================================
abstract class Door {
    // 可以有成员变量（状态）
    protected String material;    // 材质
    protected double height;      // 高度

    // 可以有构造器！
    public Door(String material, double height) {
        this.material = material;
        this.height = height;
        System.out.println(">>> [Door 构造器] 造一扇 " + material + " 门，高 " + height + "m");
    }

    // 普通方法：所有门都会的操作
    public void open() {
        System.out.println("🚪 门打开了");
    }

    public void close() {
        System.out.println("🚪 门关上了");
    }

    // 抽象方法：子类必须实现——每扇门的"开门方式"不同
    public abstract void showOpenMethod();
}

// =============================================================
//  接口：定义"能力"合同，不存状态，没有构造器
// =============================================================

// 接口1：能上锁
interface Lockable {
    // 不能有构造器！
    // 不能有实例变量！下面这行是常量（public static final）
    int MAX_PASSWORD_LENGTH = 6;

    // 方法默认 public abstract
    void lock();
    void unlock();
}

// 接口2：能防火
interface FireProof {
    void resistFire(int minutes);   // 能抗火烧多少分钟
}

// 接口3：能自动关门
interface AutoCloseable {
    void autoClose();               // 人走后自动关上
}


// =============================================================
//  具体类：防盗门 —— extends 一个抽象类 + implements 多个接口
// =============================================================
class SecurityDoor extends Door implements Lockable, FireProof, AutoCloseable {

    private String password;

    // 子类构造器必须调用父类构造器
    public SecurityDoor(double height, String password) {
        super("钢铁", height);           // 防盗门都是钢铁的
        this.password = password;
    }

    // 实现抽象类的方法
    @Override
    public void showOpenMethod() {
        System.out.println("  -> 输入密码后推拉开门");
    }

    // 实现 Lockable 接口
    @Override
    public void lock() {
        System.out.println("🔒 防盗门已上锁（密码: " + password + "）");
    }

    @Override
    public void unlock() {
        System.out.println("🔓 防盗门已解锁");
    }

    // 实现 FireProof 接口
    @Override
    public void resistFire(int minutes) {
        System.out.println("🔥 防盗门防火测试：可抗火烧 " + minutes + " 分钟");
    }

    // 实现 AutoCloseable 接口
    @Override
    public void autoClose() {
        System.out.println("🚪 防盗门自动缓缓关闭...");
    }
}

// =============================================================
//  对比：木门 —— 只 extends 抽象类，不实现额外接口
// =============================================================
class WoodDoor extends Door {

    public WoodDoor(double height) {
        super("实木", height);
    }

    @Override
    public void showOpenMethod() {
        System.out.println("  -> 推一下就开了");
    }
    // 木门不上锁、不防火，不实现那些接口
}


// =============================================================
//  主程序
// =============================================================
public class AbstractVsInterface {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  抽象类 vs 接口  —— 区别演示");
        System.out.println("========================================");

        // ========== 一、抽象类：有状态、有构造器 ==========
        System.out.println("\n========== 一、抽象类：有状态、有构造器 ==========");
        System.out.println("Door 抽象类有 material、height 属性，有构造器");
        SecurityDoor sd = new SecurityDoor(2.1, "888888");
        sd.open();
        sd.showOpenMethod();

        // ========== 二、接口：只定义能力，无状态 ==========
        System.out.println("\n========== 二、接口：只定义能力，无状态 ==========");
        System.out.println("Lockable 接口没有属性，只定义了 lock()/unlock()");
        sd.lock();
        sd.unlock();

        // ========== 三、多实现：一个类可以同时实现多个接口 ==========
        System.out.println("\n========== 三、多实现（implements 多个接口） ==========");
        System.out.println("SecurityDoor 同时实现了 Lockable + FireProof + AutoCloseable");
        sd.lock();              // Lockable
        sd.resistFire(120);     // FireProof
        sd.autoClose();         // AutoCloseable
        System.out.println("=> 一扇门同时拥有三种能力！");

        // ========== 四、单继承 vs 多实现 ==========
        System.out.println("\n========== 四、单继承 vs 多实现 ==========");
        System.out.println("Door 只能 extends 一个父类（单继承）");
        System.out.println("但可以 implements 多个接口（多实现）");
        System.out.println("SecurityDoor extends Door implements Lockable, FireProof, AutoCloseable");

        // ========== 五、接口多态 ==========
        System.out.println("\n========== 五、接口也可以多态 ==========");
        System.out.println("接口类型引用，同样可以指向实现类：");

        Lockable lockableDoor = sd;       // 门作为"能上锁的东西"
        lockableDoor.lock();

        FireProof fireProofDoor = sd;     // 门作为"能防火的东西"
        fireProofDoor.resistFire(60);

        // 但 lockableDoor.autoClose()  编译报错！Lockable 接口没有这个方法
        System.out.println("lockableDoor 只能调 Lockable 接口的方法，调不了 autoClose()");

        // ========== 六、对比：只继承抽象类，不实现接口 ==========
        System.out.println("\n========== 六、对比：只继承抽象类 ==========");
        WoodDoor wd = new WoodDoor(2.0);
        wd.open();
        wd.showOpenMethod();
        System.out.println("木门只有 Door 的基本功能，不上锁不防火");

        // ========== 总结 ==========
        System.out.println("\n========================================");
        System.out.println("  总结");
        System.out.println("========================================");
        System.out.println("对比维度        抽象类              接口");
        System.out.println("关键字          abstract class      interface");
        System.out.println("构造器          可以有              不能有");
        System.out.println("成员变量        可以有              只能有常量(static final)");
        System.out.println("方法            抽象+普通            默认 abstract（可 default）");
        System.out.println("继承/实现       单继承 extends       多实现 implements");
        System.out.println("关系            is-a（是什么）       can-do（能做什么）");
        System.out.println("========================================");
        System.out.println("选哪个？");
        System.out.println("  如果多个类有共同的状态（属性）+行为 → 抽象类");
        System.out.println("  如果只需定义「能做什么」的能力规范 → 接口");
        System.out.println("  如果既要骨架又要多能力 → 抽象类 + 多接口");
        System.out.println("========================================");
        System.out.println("一句记完：");
        System.out.println("  抽象类定「你是谁」，接口定「你能干什么」，");
        System.out.println("  单继承保结构，多实现增能力。");
    }
}
