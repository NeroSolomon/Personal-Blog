/*
 * ============================================================
 *  通俗理解 Java 四大核心概念：
 *  封装、继承、多态、抽象
 * ============================================================
 *
 *  用一个"手机"的比喻贯穿全部四个概念：
 *
 *  ===== 1. 封装 (Encapsulation) =====
 *  手机内部有芯片、电池、电路——但你看不到也摸不到。
 *  你只能通过屏幕、按钮（public 方法）来操作它。
 *  手机内部细节（private）被保护起来，外部不能乱动。
 *  -> 核心：隐藏内部实现，只暴露有限接口。
 *
 *  ===== 2. 继承 (Inheritance) =====
 *  "智能手机" 继承了 "手机" 的所有基础功能（打电话、发短信），
 *  然后再加上自己的新功能（上网、拍照）。
 *  -> 核心：子类复用父类的代码，实现 "is-a" 关系。
 *
 *  ===== 3. 多态 (Polymorphism) =====
 *  ⚠ 常见误解："多态就是子类的多态"——这是不准确的！
 *
 *  多态的主体是「父类型」，而非子类。
 *  父类引用 Phone p = new HuaweiPhone();
 *  p.call() → 行为却是华为的打法。
 *
 *  核心价值：调用者只认父类型（Phone），无需关心具体是哪个子类，
 *          运行时自动找到对应的 @Override 实现。
 *  -> 核心：父类型"以不变应万变"，子类型提供不同形态。
 *
 *  ===== 4. 抽象 (Abstraction) =====
 *  "手机" 是一个抽象概念——你不可能买一台"手机"本身。
 *  你只会买华为 P60、iPhone 15 这些具体的手机。
 *  "手机"只定义了"必须能打电话"（抽象方法），
 *  具体怎么打，由各家厂商自己实现。
 *  -> 核心：只定义"能做什么"，不定义"怎么做"。
 *
 * ============================================================
 */

// =============================================================
// 【抽象】"手机"是一个抽象概念——只规定能干什么，不规定怎么干
// =============================================================
abstract class Phone {
    // ===== 【封装】把内部状态藏起来 =====
    private String owner;        // 手机主人——外面不能直接看/改
    private int batteryLevel;    // 电量——不能从外部随意改成 999
    protected String brand;      // 品牌——子类可以知道
    protected String model;      // 型号——子类可以知道

    public Phone(String brand, String model) {
        this.brand = brand;
        this.model = model;
        this.batteryLevel = 100; // 新手机满电
    }

    // 封装 = 通过 public 方法间接访问私有数据
    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
        System.out.println(">> " + brand + " " + model + " 的主人设为: " + owner);
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    // 封装：充电逻辑对外不可见，只能调用 charge()
    public void charge(int amount) {
        if (amount < 0) {
            System.out.println(">> 充电量不能为负！");
            return;
        }
        batteryLevel = Math.min(100, batteryLevel + amount);
        System.out.println(">> " + brand + " " + model + " 充了 " + amount + "% 电，当前电量: " + batteryLevel + "%");
    }

    // 封装：耗电逻辑外界看不到，内部自动处理
    protected void consumeBattery(int amount) {
        batteryLevel = Math.max(0, batteryLevel - amount);
    }

    // ===== 【抽象方法】只定义"必须会打电话"，具体怎么打，子类自己实现 =====
    public abstract void call(String number);

    // ===== 普通方法：所有手机都一样的通用功能 =====
    public void powerOn() {
        System.out.println("📱 " + brand + " " + model + " 开机了！");
    }

    public void showInfo() {
        System.out.println("--- 手机信息 ---");
        System.out.println("  品牌: " + brand);
        System.out.println("  型号: " + model);
        System.out.println("  主人: " + (owner != null ? owner : "未设置"));
        System.out.println("  电量: " + batteryLevel + "%");
    }
}

// =============================================================
// 【继承】华为手机：继承了 Phone 的所有东西，再加自己的特色
// =============================================================
class HuaweiPhone extends Phone {
    private boolean harmonyOS;   // HarmonyOS 特性——封装

    public HuaweiPhone(String model) {
        super("华为", model);
        this.harmonyOS = true;
    }

    // ===== 【多态】华为打电话的方式 =====
    // @Override 不是必须的，但强烈建议写：
    //   - 如果拼错方法名，编译器会直接报错，帮你发现问题
    //   - 不写的话，拼错了就成了一个新方法，父类方法没被覆盖，bug 就埋下了
    //   - @Override 不管父方法是 abstract 还是普通方法，都建议加上
    //
    // 注意：static 方法不能被 @Override，只能被"隐藏"(hide)
    //   - static 方法不参与多态：Parent p = new Child(); p.staticMethod() → 走父类版本
    //   - 因为 static 方法在编译时就绑定了，看的是引用类型，不是实际对象
    //   - 如果子类写同名 static 方法，不报错，但只是"藏"了父类的，不能 @Override
    @Override
    public void call(String number) {
        System.out.println("📞 [华为] 正在通过 4G/5G 拨打 " + number + " ...");
        System.out.println("   （华为信号强，通话质量极佳！）");
        consumeBattery(2);
    }

    // 继承 + 扩展：华为独有功能
    public void multiScreenCollab() {
        System.out.println("📺 华为多屏协同已启动——手机画面投到电脑上！");
    }
}

// =============================================================
// 【继承】苹果手机
// =============================================================
class IPhone extends Phone {
    private boolean faceID;      // 封装

    public IPhone(String model) {
        super("Apple", model);
        this.faceID = true;
    }

    // ===== 【多态】苹果打电话的方式不同 =====
    @Override
    public void call(String number) {
        System.out.println("📞 [Apple] 正在通过 FaceTime Audio 拨打 " + number + " ...");
        System.out.println("   （苹果生态内通话，音质超清！）");
        consumeBattery(3);
    }

    // 苹果独有功能
    public void airdrop() {
        System.out.println("📤 AirDrop 发送文件中...");
    }
}

// =============================================================
// 【继承】小米手机
// =============================================================
class XiaomiPhone extends Phone {
    private int miuiVersion;     // 封装

    public XiaomiPhone(String model) {
        super("小米", model);
        this.miuiVersion = 14;
    }

    // ===== 【多态】小米打电话的方式也不同 =====
    @Override
    public void call(String number) {
        System.out.println("📞 [小米] 正在通过 VoLTE 高清通话拨打 " + number + " ...");
        System.out.println("   （小米通话自动录音，方便回听！）");
        consumeBattery(2);
    }

    // 小米独有功能
    public void controlSmartHome() {
        System.out.println("🏠 小爱同学，把客厅灯关掉～");
    }
}

// =============================================================
// 主程序：演示四大概念
// =============================================================
public class OOP {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  Java 四大概念：封装 继承 多态 抽象");
        System.out.println("========================================");

        // ========== 一、抽象 ==========
        System.out.println("\n========== 一、抽象 (Abstraction) ==========");
        System.out.println("Phone 是抽象类，不能直接 new Phone()");
        System.out.println("它只定义「手机必须会打电话」，具体怎么打由子类决定\n");
        // Phone p = new Phone();  <- 抽象类不能实例化！

        // ========== 二、封装 ==========
        System.out.println("========== 二、封装 (Encapsulation) ==========");
        System.out.println("手机内部数据（owner、batteryLevel）是 private");
        System.out.println("外部不能直接改，只能通过 set/get 方法操作\n");

        HuaweiPhone huawei = new HuaweiPhone("Mate 60 Pro");
        huawei.showInfo();                    // batteryLevel 初始是 100

        // huawei.batteryLevel = -999;         <- 编译报错！private 不可直接访问
        // huawei.owner = "黑客";              <- 编译报错！
        System.out.println("\n尝试直接改电池：编译报错！只能通过方法：");
        huawei.setOwner("小明");
        huawei.charge(-50);                   // 封装会校验，拒绝负值
        huawei.charge(30);
        System.out.println("当前电量：" + huawei.getBatteryLevel() + "%");

        // ========== 三、继承 ==========
        System.out.println("\n========== 三、继承 (Inheritance) ==========");
        System.out.println("子类继承父类的所有 public/protected 成员");
        System.out.println("HuaweiPhone extends Phone -> 自动拥有 call/charge/powerOn 等方法\n");

        IPhone iphone = new IPhone("iPhone 16 Pro");
        XiaomiPhone xiaomi = new XiaomiPhone("14 Ultra");

        // 子类继承了父类的所有功能
        huawei.powerOn();         // 继承来的
        iphone.powerOn();         // 继承来的
        xiaomi.powerOn();         // 继承来的

        // 子类还有自己独有的功能
        System.out.println("\n各自独有功能：");
        huawei.multiScreenCollab();
        iphone.airdrop();
        xiaomi.controlSmartHome();

        // ========== 四、多态 ==========
        System.out.println("\n========== 四、多态 (Polymorphism) ==========");
        System.out.println("核心：父类引用指向不同子类对象，同一个方法不同表现");
        System.out.println();
        System.out.println("多态不是「子类的多态」，主体是父类型 Phone：");
        System.out.println("  - 写代码时：我只认 Phone，不关心你是华为还是苹果");
        System.out.println("  - 运行时：JVM 自动找到真正的子类，调用对应的 @Override 方法");
        System.out.println("  - 好处：新增一个手机品牌，for 循环一行代码都不用改！\n");

        /*
         * 两种写法对比：
         *
         *   HuaweiPhone p = new HuaweiPhone();   // 正确，p 是华为类型
         *   Phone p = new HuaweiPhone();         // 也正确，p 是父类型
         *
         * 区别在哪？
         *
         *   HuaweiPhone p:
         *     p.call()          √  能用
         *     p.multiScreenCollab()  √  华为独有功能也能用
         *     但 p 只能装华为，不能装苹果！
         *
         *   Phone p:
         *     p.call()          √  能用（多态，自动走华为的版本）
         *     p.multiScreenCollab()  ✗  父类没有这个方法，编译报错！
         *     但 p 可以装任何手机（华为、苹果、小米...）
         *     也就是说Phone p = new HuaweiPhone(); 声明之后，下面还可以 p = new XiaomiPhone()?
         *
         * 什么时候用 Phone p？
         *   当你写的是一个「通用逻辑」，针对所有手机都适用的时候。
         *   比如下面这个数组：不管你是哪家的手机，都能统一遍历、统一调 call()
         *   这就是多态的核心价值：面向父类型编程，不关心具体是哪个子品牌。
         */

        // 【多态的关键】用父类类型声明，装不同子类对象
        // 这里的每个 new 都是子类，但数组声明的是 Phone[]
        Phone[] phones = {
            new HuaweiPhone("Mate 60 Pro"),
            new IPhone("iPhone 16 Pro"),
            new XiaomiPhone("14 Ultra")
        };

        // 循环调用 call()：同一个方法名，不同手机表现完全不同！
        System.out.println("--- 让所有手机都打电话 ---");
        for (Phone p : phones) {
            p.call("10086");
            System.out.println();
        }

        // 多态的另一个体现：同一个 charge 方法，内部逻辑由父类统一处理
        System.out.println("--- 给所有手机充电 ---");
        for (Phone p : phones) {
            p.charge(20);
        }

        // ========== 多态进阶：父类引用可以随时切换指向的子类对象 ==========
        System.out.println("\n--- 多态进阶：同一个变量，换不同的手机 ---");
        Phone myPhone = new HuaweiPhone("Mate 60 Pro");
        myPhone.call("10086");        // 华为的打法

        myPhone = new IPhone("iPhone 16 Pro");   // 同一个变量，换苹果
        myPhone.call("10086");        // 苹果的打法

        myPhone = new XiaomiPhone("14 Ultra");   // 再换小米
        myPhone.call("10086");        // 小米的打法
        System.out.println("同一个 Phone p，三次 call 行为完全不同！");

        // ========== 总结 ==========
        System.out.println("\n========================================");
        System.out.println("  总结");
        System.out.println("========================================");
        System.out.println("概念      通俗比喻              关键字/技术");
        System.out.println("封装      手机壳包住内部电路     private + getter/setter");
        System.out.println("继承      智能机继承功能机的本事  extends");
        System.out.println("多态      同样按「打电话」，      @Override + 父类引用");
        System.out.println("          华为/苹果/小米各不同");
        System.out.println("          ↑ 关键是面向父类型编程，子类提供不同形态");
        System.out.println("抽象      你不会买一台「手机」，  abstract");
        System.out.println("          只会买具体品牌型号");
        System.out.println("========================================");
        System.out.println("四者关系：");
        System.out.println("  抽象定义了「标准」（Phone 规定必须会打电话）");
        System.out.println("  封装保护了「内部」（电池不能随便改）");
        System.out.println("  继承实现了「复用」（华为小米都有了 Phone 的能力）");
        System.out.println("  多态带来了「灵活」（同一句 p.call() 表现各不同）");
        System.out.println("========================================");
        System.out.println("一句记完：");
        System.out.println("  用 abstract 定标准，用 private 保安全，");
        System.out.println("  用 extends 复代码，用父类型写多态。");
    }
}
