/*
 * ============================================================
 *  通俗理解 public、protected、private 的区别
 * ============================================================
 *
 *  想象你有一个"家"（类），家里有这三样东西：
 *
 *  public    → 你家门口的"免费糖果篮"
 *              谁都能拿，路人、邻居、你家人都可以。
 *
 *  protected → 你家客厅的"家庭药箱"
 *              只有家人（子类）和邻居（同包）能用。
 *
 *  private   → 你卧室枕头底下的"私房钱"
 *              只有你自己能碰，别人都不知道它的存在。
 *
 *  (default) → 你家后院的"烧烤架"
 *              邻居（同包内的类）可以借用，外人不行。
 *              不写修饰符就是 default。
 *
 * ============================================================
 */

// 父类：一个"家"
class Home {

    public String candyBasket = "门前糖果篮 - 谁都能拿";

    protected String medicineKit = "客厅药箱 - 家人和邻居能用";

    private String secretMoney = "枕头下私房钱 - 只有自己能碰";

    String bbqGrill = "后院烧烤架 - 邻居也能借";  // 不写 = default

    // public 方法：谁都能叫的服务
    public void publicService() {
        System.out.println(">>> [public] 来来来，路人都可以叫我干活！");
    }

    // protected 方法：只有自己人和邻居能使唤
    protected void protectedService() {
        System.out.println(">>> [protected] 只有家人和邻居能叫我帮忙。");
    }

    // private 方法：只有自己知道的小秘密
    private void privateThing() {
        System.out.println(">>> [private] 这是我的私事，别人不知道！");
    }

    // default 方法：邻居能叫，外人不行
    void defaultService() {
        System.out.println(">>> [default] 邻里之间帮个忙～外人勿扰。");
    }

    // 自己在家当然什么都能用
    public void doEverything() {
        System.out.println("\n--- 在自己家，什么都能用 ---");
        System.out.println("1. " + candyBasket);
        System.out.println("2. " + medicineKit);
        System.out.println("3. " + secretMoney);
        System.out.println("4. " + bbqGrill);
        publicService();
        protectedService();
        privateThing();
        defaultService();
    }
}

// 子类：孩子继承了家
class Child extends Home {

    public void childTry() {
        System.out.println("\n--- 孩子（子类）能用什么？ ---");
        System.out.println("1. " + candyBasket);       // √ public
        System.out.println("2. " + medicineKit);       // √ protected
        // System.out.println(secretMoney);            // ✗ private，报错！
        System.out.println("3. 私房钱？看不见！");
        System.out.println("4. " + bbqGrill);          // √ default (同包)

        publicService();                               // √
        protectedService();                            // √
        // privateThing();                             // ✗ private，报错！
        defaultService();                              // √ (同包)
    }
}

// 邻居：同包内的另一个类
class NeighborSamePackage {

    public void neighborTry(Home h) {
        System.out.println("\n--- 邻居（同包）能用什么？ ---");
        System.out.println("1. " + h.candyBasket);     // √ public
        System.out.println("2. " + h.medicineKit);     // √ protected (同包)
        // System.out.println(h.secretMoney);          // ✗ private，报错！
        System.out.println("3. 私房钱？看不见！");
        System.out.println("4. " + h.bbqGrill);        // √ default (同包)

        h.publicService();                             // √
        h.protectedService();                          // √ (同包)
        // h.privateThing();                           // ✗ private，报错！
        h.defaultService();                            // √ (同包)
    }
}

// 主程序
public class AccessModifiers {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   public / protected / private 区别演示");
        System.out.println("========================================");

        Home home = new Home();
        home.doEverything();

        Child child = new Child();
        child.childTry();

        NeighborSamePackage neighbor = new NeighborSamePackage();
        neighbor.neighborTry(home);

        System.out.println("\n========================================");
        System.out.println("  总结表格");
        System.out.println("========================================");
        System.out.println("修饰符      自己    同包    子类    外人");
        System.out.println("public      √       √      √      √");
        System.out.println("protected   √       √      √      ✗");
        System.out.println("default     √       √      ✗      ✗");
        System.out.println("private     √       ✗      ✗      ✗");
        System.out.println("========================================");
        System.out.println("口诀：public 最开放，private 最私密，");
        System.out.println("      protected 给家人，default 给邻居。");
    }
}
