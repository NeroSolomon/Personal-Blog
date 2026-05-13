/*
 * ============================================================
 *  通俗理解 Enumeration 和 Iterator 的区别
 * ============================================================
 *
 *  用生活场景打比方：
 *
 *  Enumeration = 老式幻灯片播放器（只能看，不能删）
 *     - 只有两个按钮：「下一页」「还有吗？」(hasMoreElements / nextElement)
 *     - 只能一张一张往后翻，不能删幻灯片，不能改幻灯片
 *     - 这是 Java 1.0 时代的产物，只能看 Vector 和 Hashtable 的胶片
 *
 *  Iterator  = 现代浏览器书签管理器（能看能删）
 *     - 三个按钮：「下一个」「还有吗？」「删除」(hasNext / next / remove)
 *     - 不仅能看，看到不想要的书签可以直接删掉
 *     - Java 1.2 集合框架的标准配置，List/Set/Queue 都能用
 *
 *  一图看懂：
 *
 *  ┌──────────────────────────────────────────────────────┐
 *  │  能力               Enumeration        Iterator       │
 *  ├──────────────────────────────────────────────────────┤
 *  │  向前遍历            ✓ (nextElement)    ✓ (next)      │
 *  │  判断还有元素         ✓ (hasMoreElements) ✓ (hasNext)  │
 *  │  删除元素            ✗                  ✓ (remove)    │
 *  │  方法名风格          长(hasMore- )      短(hasNext)    │
 *  │  诞生时间            Java 1.0           Java 1.2      │
 *  │  适用范围            Vector/Hashtable   所有Collection │
 *  │  是否推荐            不推荐(遗留)        推荐           │
 *  │  fail-fast          是                 是             │
 *  └──────────────────────────────────────────────────────┘
 *
 *  一句话总结：
 *    Enumeration 是爷爷辈的，只能看不能删，只认 Vector/Hashtable。
 *    Iterator 是当红主力，能看能删，所有集合都能用。
 */

import java.util.*;

public class EnumerationVsIteratorDemo {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  Enumeration vs Iterator —— 区别演示");
        System.out.println("========================================");

        // =============================================================
        //  一、Enumeration：老式幻灯片播放器（只能看不能删）
        // =============================================================
        System.out.println("\n========== 一、Enumeration：老式幻灯片播放器（只能看，不能删） ==========");

        // Enumeration 只能从 Vector 或 Hashtable 获取
        Vector<String> slides = new Vector<>();
        slides.add("幻灯片1: 公司简介");
        slides.add("幻灯片2: 产品介绍");
        slides.add("幻灯片3: 财务数据");
        slides.add("幻灯片4: 未来规划");
        System.out.println("  幻灯片架(Vector): " + slides);

        // 获取 Enumeration
        Enumeration<String> enu = slides.elements();
        System.out.print("  用 Enumeration 播放: ");
        while (enu.hasMoreElements()) {      // 老式命名：hasMoreElements
            String slide = enu.nextElement(); // 老式命名：nextElement
            System.out.print("[" + slide + "] ");
        }
        System.out.println();

        // 尝试删除？Enumeration 没有 remove 方法！
        System.out.println("  → Enumeration 没有 remove() 方法，想删幻灯片？抱歉没这个功能。");
        System.out.println("  → 方法名也长的啰嗦：hasMoreElements / nextElement");

        // Enumeration 只能从老集合获取
        System.out.println("\n  Enumeration 适用范围测试：");
        System.out.println("    Vector.elements()      ✓ (老式列表)");
        Hashtable<String, String> ht = new Hashtable<>();
        System.out.println("    Hashtable.keys()       ✓ (老式字典，返回 Enumeration)");
        System.out.println("    ArrayList.iterator()   ✗ (没有 elements() 方法！)");
        System.out.println("    HashSet.iterator()     ✗ (同样没有！)");
        System.out.println("  → Enumeration 是 Vector/Hashtable 的老家当，新朋友都不认。");

        // =============================================================
        //  二、Iterator：现代浏览器书签管理器（能看能删）
        // =============================================================
        System.out.println("\n========== 二、Iterator：现代书签管理器（能看能删） ==========");

        // Iterator 来自所有 Collection
        List<String> bookmarks = new ArrayList<>();
        bookmarks.add("书签A: 技术博客");
        bookmarks.add("书签B: 广告页面（不想要）");
        bookmarks.add("书签C: 开源仓库");
        bookmarks.add("书签D: 垃圾链接（不想要）");
        bookmarks.add("书签E: 文档中心");
        System.out.println("  书签列表: " + bookmarks);

        // 获取 Iterator
        Iterator<String> it = bookmarks.iterator();
        System.out.println("  用 Iterator 遍历并删掉不想要的:");
        while (it.hasNext()) {
            String bm = it.next();
            if (bm.contains("不想要")) {
                it.remove();  // 安全删除！Enumeration 做不到的
                System.out.println("    删掉 → " + bm);
            } else {
                System.out.println("    保留 → " + bm);
            }
        }
        System.out.println("  清理后: " + bookmarks);
        System.out.println("  → Iterator 有 remove()，遍历途中随时删，这就是最大区别！");
        System.out.println("  → 方法名也更简洁：hasNext / next / remove");

        // Iterator 所有集合都能用
        System.out.println("\n  Iterator 适用范围测试：");
        System.out.println("    ArrayList.iterator()   ✓");
        System.out.println("    HashSet.iterator()     ✓");
        System.out.println("    LinkedList.iterator()  ✓");
        System.out.println("    PriorityQueue.iterator() ✓");
        System.out.println("    Vector.iterator()      ✓ (Vector 也能用新接口！)");
        System.out.println("  → Iterator 是 Collection 接口的标准，谁都能用。");

        // =============================================================
        //  三、Enumeration 还能转 Iterator
        // =============================================================
        System.out.println("\n========== 三、Enumeration 可以转成 Iterator ==========");
        System.out.println();
        System.out.println("  如果遇到老代码返回 Enumeration，可以这样转：");
        System.out.println("    Enumeration<String> enu = oldLib.getData();");
        System.out.println("    Iterator<String> it = enu.asIterator();  // Java 9+");

        // 演示转换
        Vector<String> oldData = new Vector<>(Arrays.asList("老数据1", "老数据2", "老数据3"));
        Enumeration<String> oldEnum = oldData.elements();
        Iterator<String> newIt = oldEnum.asIterator();  // Java 9+

        System.out.print("    转成 Iterator 后遍历: ");
        newIt.forEachRemaining(s -> System.out.print("[" + s + "] "));
        System.out.println();
        System.out.println("  → asIterator() 也不支持 remove，因为背后的 Enumeration 本来就没这个能力。");

        // =============================================================
        //  四、性能对比：差别极小
        // =============================================================
        System.out.println("\n========== 四、性能对比 ==========");
        System.out.println();
        System.out.println("  都是 O(n) 遍历，性能几乎一样，主要差在功能上。");
        System.out.println();

        final int TEST_N = 1000000;
        Vector<Integer> bigVec = new Vector<>();
        for (int i = 0; i < TEST_N; i++) bigVec.add(i);

        // 用 Enumeration 遍历
        long start = System.nanoTime();
        Enumeration<Integer> bigEnum = bigVec.elements();
        long sumEnum = 0;
        while (bigEnum.hasMoreElements()) sumEnum += bigEnum.nextElement();
        long enumTime = System.nanoTime() - start;

        // 用 Iterator 遍历
        start = System.nanoTime();
        Iterator<Integer> bigIt = bigVec.iterator();
        long sumIter = 0;
        while (bigIt.hasNext()) sumIter += bigIt.next();
        long iterTime = System.nanoTime() - start;

        System.out.println("  " + TEST_N + " 个元素遍历求和:");
        System.out.println("    Enumeration : " + enumTime / 1000000.0 + " ms  (求和=" + sumEnum + ")");
        System.out.println("    Iterator    : " + iterTime / 1000000.0 + " ms  (求和=" + sumIter + ")");
        System.out.println("  → 速度几乎没差别，选谁看功能，不看性能。");

        // =============================================================
        //  五、和 ListIterator 的区别
        // =============================================================
        System.out.println("\n========== 五、和 ListIterator 的三方对比 ==========");
        System.out.println();
        System.out.println("  ┌──────────────────────────────────────────────────────┐");
        System.out.println("  │  能力        Enumeration   Iterator    ListIterator   │");
        System.out.println("  ├──────────────────────────────────────────────────────┤");
        System.out.println("  │  正向遍历    ✓            ✓           ✓              │");
        System.out.println("  │  反向遍历    ✗            ✗           ✓              │");
        System.out.println("  │  删除元素    ✗            ✓           ✓              │");
        System.out.println("  │  修改元素    ✗            ✗           ✓              │");
        System.out.println("  │  插入元素    ✗            ✗           ✓              │");
        System.out.println("  │  范围        Vector/HT     所有Coll    仅List          │");
        System.out.println("  │  时代        Java 1.0      Java 1.2    Java 1.2       │");
        System.out.println("  │  推荐度      不推荐        推荐        需要时用        │");
        System.out.println("  └──────────────────────────────────────────────────────┘");
        System.out.println();
        System.out.println("  进化路线：Enumeration → Iterator → ListIterator");
        System.out.println("    爷爷只能看 → 爸爸能看能删 → 儿子能看能删能改还能来回走");

        // =============================================================
        //  总结
        // =============================================================
        System.out.println("\n========================================");
        System.out.println("  总结");
        System.out.println("========================================");
        System.out.println("               Enumeration        Iterator");
        System.out.println("  比喻         老式幻灯机         现代书签管理器");
        System.out.println("  诞生          Java 1.0           Java 1.2");
        System.out.println("  判断          hasMoreElements   hasNext (简洁)");
        System.out.println("  取值          nextElement        next (简洁)");
        System.out.println("  删除          ✗ 没有              ✓ remove()");
        System.out.println("  适用范围      Vector/Hashtable    所有 Collection");
        System.out.println("  线程安全      取决于集合          取决于集合");
        System.out.println("  是否推荐      遗留，不推荐         推荐使用");
        System.out.println("========================================");
        System.out.println("面试官问：Enumeration 和 Iterator 有什么区别？");
        System.out.println("  答：Enumeration 是 Java 1.0 遗留接口，只能正向遍历，");
        System.out.println("      不能删除元素，仅适用于 Vector/Hashtable。");
        System.out.println("      Iterator 是集合框架标准接口，能正向遍历 + 安全删除，");
        System.out.println("      适用于所有 Collection。日常开发只用 Iterator。");
        System.out.println("========================================");
        System.out.println("速记：");
        System.out.println("  Enumeration 爷爷辈，看看行、删没门；");
        System.out.println("  Iterator 接班人，看删都得门门通；");
        System.out.println("  如果还要退改插，ListIterator 当先锋。");
    }
}
