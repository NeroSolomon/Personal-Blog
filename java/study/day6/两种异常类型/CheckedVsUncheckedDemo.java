/*
 * ============================================================
 *  通俗理解 Checked 异常 和 Unchecked 异常
 * ============================================================
 *
 *  用生活场景打比方：
 *
 *  Checked 异常  =  天气预报说今天有 70% 概率下雨
 *      编译器(老妈)看到预报，逼你出门前必须做出选择：
 *        A) 带上伞（try-catch 抓住自己处理）
 *        B) 跟别人说「我不带伞，淋雨了别怪我」（throws 抛给调用者）
 *      反正你不能啥也不做就出门——编译器不让你编译通过！
 *      典型：文件没找到(FileNotFoundException)、网络断了(IOException)
 *
 *  Unchecked 异常 =  走路玩手机撞电线杆
 *      编译器(老妈)管不了这个——这是你自己傻，写了 bug。
 *      不强制处理，程序直接崩给你看。
 *      典型：空指针(NullPointerException)、数组越界(ArrayIndexOutOfBounds)
 *            除零(ArithmeticException)、类型强转错(ClassCastException)
 *
 *  Error = 地震了
 *      不用 try-catch，抓了也没什么能做的，等死就行。
 *      典型：内存溢出(OutOfMemoryError)、栈溢出(StackOverflowError)
 *
 *  继承关系一图看懂：
 *
 *  Throwable
 *  ├── Error                     ← 不要捕获（地震），Error定义了不期望被用户程序捕获的异常
 *  │   └── OutOfMemoryError, StackOverflowError...
 *  └── Exception                 ← 用于用户程序可以捕获的异常情况
 *      ├── RuntimeException      ← Unchecked（玩手机撞电线杆，不强制处理）
 *      │   └── NullPointerException, ArithmeticException,
 *      │       IndexOutOfBoundsException, IllegalArgumentException...
 *      └── 其他 Exception        ← Checked（可能下雨，编译器逼你带伞）
 *          └── IOException, SQLException,
 *              FileNotFoundException, ClassNotFoundException...
 *
 *  ┌──────────────────────────────────────────────────────────┐
 *  │  对比项      Checked 异常        Unchecked 异常           │
 *  ├──────────────────────────────────────────────────────────┤
 *  │  比喻       老妈逼你带伞         你自己撞电线杆           │
 *  │  编译器      强制检查，必须处理   不检查，爱处理不处理     │
 *  │  继承        Exception(非RTE)    RuntimeException        │
 *  │  何时用      可预见的恢复场景     程序Bug、不可恢复       │
 *  │  处理方式    try-catch/throws    可以不处理                │
 *  │  代表        IOException          NullPointerException    │
 *  │             SQLException         ArrayIndexOutOfBounds    │
 *  └──────────────────────────────────────────────────────────┘
 *
 *  一句话总结：
 *    Checked → 外部环境可能出问题（文件不在、网络断了），
 *              编译器逼你处理，因为这是可以恢复的。
 *    Unchecked → 你代码写错了（空指针、越界），
 *                编译器不管你，因为这是 bug，该修代码而不是抓异常。
 */

import java.io.*;

public class CheckedVsUncheckedDemo {

    // =============================================================
    //  Checked 异常的经典方法：必须声明 throws 或 try-catch
    // =============================================================

    // 方式A：把异常声明出去 → throws，让调用者处理
    static String readFirstLine(String path) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(path));
        String line = reader.readLine();
        reader.close();
        return line;
    }

    // 方式B：自己捕获处理 → try-catch
    static String readFirstLineSafe(String path) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String line = reader.readLine();
            reader.close();
            return line;
        } catch (IOException e) {
            return "文件读不了，返回默认值";
        }
    }

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  Checked vs Unchecked 异常 —— 区别演示");
        System.out.println("========================================");

        // =============================================================
        //  一、Checked 异常：编译器逼你处理
        // =============================================================
        System.out.println("\n========== 一、Checked 异常：老妈逼你带伞 ==========");
        System.out.println();
        System.out.println("  本质：继承 Exception，但不是 RuntimeException 的子类。");
        System.out.println("  规则：编译器强制你 try-catch 或 throws，否则编译不过！");

        System.out.println("\n  例子 1：FileReader 构造方法声明了 throws FileNotFoundException");
        System.out.println("    public FileReader(String name) throws FileNotFoundException");
        System.out.println("    → 所以你不 try-catch 就编译报错！");

        System.out.println("    [方式A] 用 throws 把异常甩给上级：");
        System.out.println("      String readFirstLine(String p) throws IOException {...}");

        System.out.println("    [方式B] 用 try-catch 自己兜底：");
        String result = readFirstLineSafe("不存在的文件.txt");
        System.out.println("      结果: " + result + "  ← catch 住了，没崩");

        System.out.println("\n  常见 Checked 异常速查：");
        System.out.println("    IOException         所有 IO 操作失败");
        System.out.println("    FileNotFoundException  文件不存在（IOException 子类）");
        System.out.println("    SQLException        数据库操作失败");
        System.out.println("    ClassNotFoundException  类找不到(如 Class.forName)");
        System.out.println("    InterruptedException  线程被中断(sleep/wait)");
        System.out.println("    ParseException      解析失败(如日期格式化)");

        // =============================================================
        //  二、Unchecked 异常：编译器不管你
        // =============================================================
        System.out.println("\n========== 二、Unchecked 异常：你撞电线杆，编译器不管 ==========");
        System.out.println();
        System.out.println("  本质：继承 RuntimeException 的异常。");
        System.out.println("  规则：不强制处理，编译能过，运行时崩。");

        System.out.println("\n  例子 2：空指针 NullPointerException（最出名）");
        try {
            String s = null;
            System.out.println("    空字符串长度: " + s.length());  // 炸！
        } catch (NullPointerException e) {
            System.out.println("    ❌ 空指针炸了！但你可以 catch，也可以不管。");
        }
        System.out.println("    → 这个方法没声明 throws NullPointerException，编译器也不抱怨。");

        System.out.println("\n  例子 3：除零 ArithmeticException");
        try {
            int x = 10 / 0;
        } catch (ArithmeticException e) {
            System.out.println("    ❌ 除零炸了！/ by zero");
        }

        System.out.println("\n  例子 4：数组越界 ArrayIndexOutOfBoundsException");
        try {
            int[] arr = {1, 2, 3};
            System.out.println(arr[100]);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("    ❌ 越界炸了！Index 100 out of bounds");
        }

        System.out.println("\n  例子 5：类型转换错误 ClassCastException");
        try {
            Object obj = "hello";
            Integer num = (Integer) obj;  // 字符串不能强转成 Integer
        } catch (ClassCastException e) {
            System.out.println("    ❌ 类型转换炸了！");
        }

        System.out.println("\n  常见 Unchecked 异常速查：");
        System.out.println("    NullPointerException      空指针，用之前没判空");
        System.out.println("    ArithmeticException       除零");
        System.out.println("    IndexOutOfBoundsException  数组/集合越界");
        System.out.println("    IllegalArgumentException  传入非法参数");
        System.out.println("    ClassCastException        类型强转失败");
        System.out.println("    NumberFormatException     字符串转数字失败");

        // =============================================================
        //  三、核心区别：编译器态度完全不同
        // =============================================================
        System.out.println("\n========== 三、核心区别：编译器态度 ==========");

        // 演示：Checked 异常不处理真的编译不过
        System.out.println("\n  【实验】Checked 异常不 try-catch 行不行？");
        System.out.println("    下边这行如果取消注释，编译器直接报错：");
        System.out.println("    // BufferedReader r = new BufferedReader(new FileReader(\"x.txt\"));");
        System.out.println("    → ❌ 编译错误：unreported exception FileNotFoundException");
        System.out.println("                  must be caught or declared to be thrown");

        System.out.println("\n  【实验】Unchecked 异常不 try-catch 行不行？");
        System.out.println("    String s = null; s.length();  // 编译完全通过！");
        System.out.println("    → 编译器不吭声，运行时才炸。");

        // =============================================================
        //  四、自定义 Checked 和 Unchecked 异常
        // =============================================================
        System.out.println("\n========== 四、如何自定义异常？只需看继承谁 ==========");
        System.out.println();
        System.out.println("  想自定义 Checked 异常   → extends Exception");
        System.out.println("  想自定义 Unchecked 异常 → extends RuntimeException");
        System.out.println();
        System.out.println("  // 自定义 Checked 异常");
        System.out.println("  class MyCheckedException extends Exception {");
        System.out.println("      public MyCheckedException(String msg) { super(msg); }");
        System.out.println("  }");
        System.out.println("  → 别人调用时必须 try-catch 或 throws，因为它的爹是 Exception");
        System.out.println();
        System.out.println("  // 自定义 Unchecked 异常");
        System.out.println("  class MyUncheckedException extends RuntimeException {");
        System.out.println("      public MyUncheckedException(String msg) { super(msg); }");
        System.out.println("  }");
        System.out.println("  → 别人调用可以不处理，因为它的爹是 RuntimeException");

        // =============================================================
        //  五、什么时候用哪种？
        // =============================================================
        System.out.println("\n========== 五、什么时候用哪种？设计原则 ==========");
        System.out.println();
        System.out.println("  用 Checked 异常，当：");
        System.out.println("    - 这个错误调用者「有可能」恢复");
        System.out.println("    - 读文件失败 → 可以用备用文件 / 提示用户重试");
        System.out.println("    - 网络请求失败 → 可以重试 / 走离线模式");
        System.out.println("    - 一句话：外部环境导致的，非代码 bug");

        System.out.println();
        System.out.println("  用 Unchecked 异常，当：");
        System.out.println("    - 这个错误是「程序员写错了」，调用者没法恢复");
        System.out.println("    - 空指针 → 是你没判空，调用者能干啥？");
        System.out.println("    - 数组越界 → 是你索引算错了，调用者能干啥？");
        System.out.println("    - 一句话：代码 bug，修代码而不是 catch 完装没事");

        // =============================================================
        //  六、Error：地震了，别抓
        // =============================================================
        System.out.println("\n========== 六、Error：地震了，别抓 ==========");
        System.out.println();
        System.out.println("  Error 继承 Throwable，不属于 Exception 体系：");
        System.out.println("    OutOfMemoryError   → 内存溢出了，基本没法恢复");
        System.out.println("    StackOverflowError → 栈溢出了，通常是无限递归");
        System.out.println("    NoClassDefFoundError → 运行时缺类");
        System.out.println();
        System.out.println("  原则：不要 try-catch Error！抓了也干不了正事。");

        // =============================================================
        //  七、try-with-resources 处理 Checked 异常
        // =============================================================
        System.out.println("\n========== 七、最佳实践：try-with-resources ==========");
        System.out.println();
        System.out.println("  Checked 异常最烦人的就是每次得手动 close。");
        System.out.println("  try-with-resources 自动关，告别 finally 写 close：");
        System.out.println();
        System.out.println("  老写法（啰嗦）：");
        System.out.println("    BufferedReader r = null;");
        System.out.println("    try {");
        System.out.println("        r = new BufferedReader(new FileReader(p));");
        System.out.println("        return r.readLine();");
        System.out.println("    } finally {");
        System.out.println("        if (r != null) r.close();  ← 烦死了");
        System.out.println("    }");
        System.out.println();
        System.out.println("  新写法（清爽）：");
        System.out.println("    try (BufferedReader r = new BufferedReader(new FileReader(p))) {");
        System.out.println("        return r.readLine();");
        System.out.println("    }  ← 自动 close，不用写 finally！");

        // =============================================================
        //  总结
        // =============================================================
        System.out.println("\n========================================");
        System.out.println("  总结");
        System.out.println("========================================");
        System.out.println();
        System.out.println("               Checked             Unchecked");
        System.out.println("  比喻         老妈逼你带伞         自己撞电线杆");
        System.out.println("  继承         Exception 非 RTE     RuntimeException");
        System.out.println("  编译器       强制处理             不强制");
        System.out.println("  处理方式     try-catch/throws     可选");
        System.out.println("  语义         外部环境可能出错      代码有 bug");
        System.out.println("  可恢复性     通常可恢复            通常是 bug 不可恢复");
        System.out.println("  代表         IOException           NullPointerException");
        System.out.println("========================================");
        System.out.println("面试必问题：Checked 和 Unchecked 异常有什么区别？");
        System.out.println("  Checked 继承 Exception(非RTE)，编译器强制处理，");
        System.out.println("  用于可恢复的外部错误(IO/网络/DB)。");
        System.out.println("  Unchecked 继承 RuntimeException，编译不检查，");
        System.out.println("  代表程序 Bug(空指针/越界)，应该修代码而非抓异常。");
        System.out.println("========================================");
        System.out.println("速记口诀：");
        System.out.println("  Checked 环境会出错，编译器逼你预案做；");
        System.out.println("  Unchecked 代码有 Bug，写好代码是正路；");
        System.out.println("  想强制就 extends Exception，不管就 extends RuntimeException。");
    }
}
