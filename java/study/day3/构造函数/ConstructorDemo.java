/*
 * ============================================================
 *  通俗理解 Java 构造函数（构造器 / Constructor）
 * ============================================================
 *
 *  构造函数 = 对象的"出生设置"
 *
 *  想象你要造一部手机：
 *    制造商（new）拿起一台新手机时，
 *    构造函数就是"出厂设置流程"——贴品牌标签、装电池、写序列号。
 *
 *  关键特征：
 *    1. 名字必须和类名一样，没有返回值（连 void 都不写）
 *    2. 每 new 一个对象，构造函数自动执行一次
 *    3. 可以重载：多个构造器，参数不同
 *    4. 子类构造器第一行必须是 super() 或 this()
 *
 * ============================================================
 *
 *  ┌─────────────────────────────────────────────────────────┐
 *  │ 规则一：如果你一个构造器都不写，编译器自动送你一个       │
 *  │         无参的空构造器 public Phone() {}                 │
 *  │                                                         │
 *  │ 规则二：只要你写了任何一个构造器，默认无参构造器就没了   │
 *  │         子类必须用 super(...) 显式调用父类构造器         │
 *  │                                                         │
 *  │ 规则三：构造器第一行必须是 super() 或 this()             │
 *  │         不能两个同时放第一行                             │
 *  │                                                         │
 *  │ 规则四：this(参数) 可以调用本类的另一个构造器            │
 *  │         常用于减少重复代码（一个终极构造器干所有活）     │
 *  └─────────────────────────────────────────────────────────┘
 */

// =============================================================
//  一、父类：手机
// =============================================================
class Phone {
    private String brand;
    private String model;
    private String serialNo;

    /*
     * 有参构造器：出厂时必须指定品牌、型号、序列号
     * 一旦写了这个，默认无参构造器就消失了！
     */
    public Phone(String brand, String model, String serialNo) {
        this.brand = brand;
        this.model = model;
        this.serialNo = serialNo;
        System.out.println(">>> Phone 构造器执行：品牌=" + brand + " 型号=" + model);
    }

    public void showInfo() {
        System.out.println("  品牌: " + brand + "  型号: " + model + "  序列号: " + serialNo);
    }
}

// =============================================================
//  二、子类：华为手机 —— 演示 super()
// =============================================================
class HuaweiPhone extends Phone {
    private boolean satelliteSupport;

    public HuaweiPhone(String model, String serialNo, boolean satelliteSupport) {
        /*
         * super() 必须放在第一行！调用父类构造器
         * 父类没有无参构造器，不写 super() 编译报错
         */
        super("华为", model, serialNo);
        this.satelliteSupport = satelliteSupport;
        System.out.println(">>> HuaweiPhone 构造器执行：卫星通信=" + satelliteSupport);
    }
}

// =============================================================
//  三、子类：苹果手机 —— 演示 this() 调用本类另一个构造器
// =============================================================
class IPhone extends Phone {
    private String color;
    private int storage;    // 存储容量 GB

    /*
     * 终极构造器：所有参数一把梭
     * 其他构造器通过 this() 调用它，避免代码重复
     */
    public IPhone(String model, String serialNo, String color, int storage) {
        super("Apple", model, serialNo);
        this.color = color;
        this.storage = storage;
        System.out.println(">>> IPhone 全能构造器：颜色=" + color + " 容量=" + storage + "GB");
    }

    /*
     * 简配构造器：只传型号和序列号，颜色默认深空灰，容量默认 128
     * 用 this() 委托给上面的全能构造器
     */
    public IPhone(String model, String serialNo) {
        this(model, serialNo, "深空灰", 128);   // 调用本类另一个构造器
        System.out.println(">>> IPhone 简配构造器：用的是默认颜色和容量");
    }
}

// =============================================================
//  四、演示：没有构造器的情况
// =============================================================
class SimpleBox {
    // 类体为空 —— 编译器会自动生成一个无参构造器
    // 等价于: public SimpleBox() {}
}

// =============================================================
//  五、演示：写了有参构造器后，无参构造器消失的后果
// =============================================================
class LockedBox {
    private String password;

    public LockedBox(String password) {
        this.password = password;
    }
    // 此时 new LockedBox()  编译报错！无参构造器没了
    // 只能 new LockedBox("123456")
}

// =============================================================
//  六、演示：想两者兼得？自己显式写两个构造器
// =============================================================
class PhoneBox {
    private String brand;

    // 无参构造器：显式写出来
    public PhoneBox() {
        this.brand = "默认品牌";
    }

    // 有参构造器（重载）
    public PhoneBox(String brand) {
        this.brand = brand;
    }

    public String getBrand() {
        return brand;
    }
}

// =============================================================
//  主程序
// =============================================================
public class ConstructorDemo {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  构造函数（构造器）完全演示");
        System.out.println("========================================");

        // ========== 一、有参构造器 + super() ==========
        System.out.println("\n========== 一、super() 调用父类构造器 ==========");
        System.out.println("huawei = new HuaweiPhone(...)");
        HuaweiPhone huawei = new HuaweiPhone("Mate 60 Pro", "SN-HW-001", true);
        huawei.showInfo();

        // ========== 二、this() 调用本类其他构造器 ==========
        System.out.println("\n========== 二、this() 调用本类构造器 ==========");
        System.out.println("iphone1 = new IPhone(全部参数)");
        IPhone iphone1 = new IPhone("iPhone 16 Pro", "SN-AP-001", "钛金色", 256);
        iphone1.showInfo();

        System.out.println("\niphone2 = new IPhone(只有型号和序列号)");
        IPhone iphone2 = new IPhone("iPhone 16", "SN-AP-002");
        iphone2.showInfo();

        // ========== 三、编译器的默认构造器 ==========
        System.out.println("\n========== 三、编译器赠送的默认无参构造器 ==========");
        SimpleBox box = new SimpleBox();   // 编译通过！
        System.out.println("SimpleBox 类没有写构造器，但 new SimpleBox() 成功了");
        System.out.println("因为编译器自动生成了无参构造器");

        // ========== 四、无参构造器消失 ==========
        System.out.println("\n========== 四、写了有参构造器后无参构造器消失 ==========");
        // LockedBox box2 = new LockedBox();    <- 编译报错！
        LockedBox box2 = new LockedBox("123456");   // 只能这样
        System.out.println("LockedBox 只能 new LockedBox(\"密码\")，不能 new LockedBox()");
        System.out.println("因为写了有参构造器，默认无参的就没了");

        // ========== 五、两者兼得：显式写两个构造器 ==========
        System.out.println("\n========== 五、两者兼得：自己写无参 + 有参两个构造器 ==========");
        PhoneBox pb1 = new PhoneBox();            // 无参
        PhoneBox pb2 = new PhoneBox("华为");      // 有参
        System.out.println("PhoneBox 无参版: " + pb1.getBrand());
        System.out.println("PhoneBox 有参版: " + pb2.getBrand());
        System.out.println("=> 结论：想两者兼得，自己显式写出两个构造器（重载）");

        // ========== 总结 ==========
        System.out.println("\n========================================");
        System.out.println("  总结");
        System.out.println("========================================");
        System.out.println("要点                  说明");
        System.out.println("名字 = 类名            没有返回值，连 void 也不写");
        System.out.println("new 时自动执行         每 new 一次就跑一次");
        System.out.println("可以重载              多个构造器，参数列表不同");
        System.out.println("super(参数)            调用父类构造器，必须第一行");
        System.out.println("this(参数)             调用本类另一个构造器，必须第一行");
        System.out.println("没写构造器             编译器送一个无参空构造器");
        System.out.println("写了任意构造器         默认无参的消失");
        System.out.println("想两者兼得             自己显式写无参 + 有参（重载）");
        System.out.println("========================================");
        System.out.println("一句话：构造函数 = new 的时候对象自动执行的初始化代码");
    }
}
