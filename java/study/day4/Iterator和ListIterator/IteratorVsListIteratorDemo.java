/*
 * ============================================================
 *  通俗理解 Iterator 和 ListIterator 的区别
 * ============================================================
 *
 *  用生活场景打比方：
 *
 *  Iterator  = 单行道上的观光车（只能向前，不能掉头）
 *             上车后只能一站一站往前看，看过的站点没法回头。
 *
 *  ListIterator  = 双向道路上的自行车（能向前、能向后、能在当前位置插东西）
 *             不仅能往前，掉个头还能往回看。路过喜欢的位置，
 *             还能原地放个东西、改个东西。
 *
 *  关键区别一图看懂：
 *
 *  ┌──────────────────────────────────────────────────────┐
 *  │  能力              Iterator      ListIterator        │
 *  ├──────────────────────────────────────────────────────┤
 *  │  向前遍历           ✓             ✓                  │
 *  │  向后遍历           ✗             ✓                  │
 *  │  获取当前索引       ✗             ✓ (nextIndex)      │
 *  │  删除元素           ✓             ✓                  │
 *  │  修改元素           ✗             ✓ (set)            │
 *  │  插入元素           ✗             ✓ (add)            │
 *  │  适用集合           Collection    List only          │
 *  └──────────────────────────────────────────────────────┘
 *
 *  一句话总结：
 *    Iterator：只能一路走到底，能删但不能改、不能插、不能回头。
 *    ListIterator：Iterator 的升级版，能来回走、能删能改能插。
 */

import java.util.*;

public class IteratorVsListIteratorDemo {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  Iterator vs ListIterator —— 区别演示");
        System.out.println("========================================");

        // =============================================================
        //  一、Iterator：单行道，只能向前走
        // =============================================================
        System.out.println("\n========== 一、Iterator：单行道（只能向前，不能回头） ==========");

        List<String> playlist = new ArrayList<>();
        playlist.add("晴天");
        playlist.add("七里香");
        playlist.add("稻香");
        playlist.add("夜曲");
        System.out.println("歌单: " + playlist);

        Iterator<String> it = playlist.iterator();
        System.out.print("用 Iterator 遍历: ");
        while (it.hasNext()) {
            String song = it.next();
            System.out.print("[" + song + "] ");
        }
        System.out.println();
        System.out.println("  -> 走完了，想回头看「七里香」？抱歉，Iterator 不支持 hasPrevious()！");

        // Iterator 的 remove：安全删除
        it = playlist.iterator();
        while (it.hasNext()) {
            if (it.next().equals("稻香")) {
                it.remove(); // 安全删除，不会引发 ConcurrentModificationException
            }
        }
        System.out.println("用 Iterator.remove 删掉「稻香」后: " + playlist);
        System.out.println("  -> Iterator 可以删元素，但不能改元素、不能插元素");

        // =============================================================
        //  二、ListIterator：双向道，能来回走
        // =============================================================
        System.out.println("\n========== 二、ListIterator：双向道（能向前、能向后、能改能插） ==========");

        playlist = new ArrayList<>();
        playlist.add("晴天");
        playlist.add("七里香");
        playlist.add("稻香");
        playlist.add("夜曲");
        System.out.println("重置歌单: " + playlist);

        ListIterator<String> lit = playlist.listIterator();

        // 1. 向前走两步
        System.out.println("\n  [1] 向前走两步:");
        System.out.println("    第" + lit.nextIndex() + "首: " + lit.next());  // 晴天
        System.out.println("    第" + lit.nextIndex() + "首: " + lit.next());  // 七里香

        // 2. 向后回头（Iterator 做不到！）
        System.out.println("\n  [2] 向后退一步:");
        System.out.println("    往回看: " + lit.previous());                 // 七里香
        System.out.println("    当前索引: " + lit.nextIndex());
        System.out.println("    再往回看: " + lit.previous());                // 晴天
        System.out.println("  -> ListIterator 有 hasPrevious() / previous()，可以回头！");

        // 3. 在当前位置插入元素（Iterator 做不到！）
        System.out.println("\n  [3] 在「晴天」后面插入「东风破」（用 add）:");
        lit.next();                    // 回到"晴天"后面
        lit.add("东风破");              // 插入在当前位置
        System.out.println("    插入后: " + playlist);
        System.out.println("  -> ListIterator 有 add()，可以在游标位置插入！");

        // 4. 修改元素（Iterator 做不到！）
        System.out.println("\n  [4] 把「东风破」改成「发如雪」（用 set）:");
        lit.previous();                // 回到"东风破"上
        lit.previous();                // 回到"晴天"上
        lit.next();                    // 到"东风破"
        lit.set("发如雪");              // 改成"发如雪"
        System.out.println("    修改后: " + playlist);
        System.out.println("  -> ListIterator 有 set()，可以原地修改元素！");

        // 5. 完整遍历：先往后再往前（展示双向遍历能力）
        System.out.println("\n  [5] 完整双向演示:");
        System.out.print("    正向: ");
        lit = playlist.listIterator();
        while (lit.hasNext()) {
            System.out.print("[" + lit.next() + "] ");
        }
        System.out.println();

        System.out.print("    反向: ");
        while (lit.hasPrevious()) {
            System.out.print("[" + lit.previous() + "] ");
        }
        System.out.println();

        // =============================================================
        //  三、iterator() vs listIterator() 获取方式的区别
        // =============================================================
        System.out.println("\n========== 三、获取方式区别 ==========");

        List<String> names = new ArrayList<>(Arrays.asList("张三", "李四", "王五"));
        System.out.println("List: " + names);

        // Iterator：只能从列表头部开始
        Iterator<String> it2 = names.iterator();
        System.out.println("  names.iterator()       → 总是从头开始");

        // ListIterator：可以从任意位置开始
        ListIterator<String> lit2 = names.listIterator(1); // 从索引 1 开始
        System.out.println("  names.listIterator(1)  → 从索引 " + lit2.nextIndex() + " 开始: " + lit2.next());
        lit2 = names.listIterator();                       // 默认从头
        System.out.println("  names.listIterator()    → 默认从头，当前索引: " + lit2.nextIndex());

        // =============================================================
        //  四、实际场景对比
        // =============================================================
        System.out.println("\n========== 四、实际场景：什么时候用哪个？ ==========");

        // 场景一：只想遍历一遍 List 并可能删除某些元素 → Iterator 足够
        System.out.println("\n  [场景1] 遍历并删除符合条件的元素 → Iterator 够用");
        List<Integer> scores = new ArrayList<>(Arrays.asList(90, 55, 80, 45, 70));
        System.out.println("    原始成绩: " + scores);
        Iterator<Integer> scoreIt = scores.iterator();
        while (scoreIt.hasNext()) {
            if (scoreIt.next() < 60) {
                scoreIt.remove();
            }
        }
        System.out.println("    删除不及格后: " + scores);

        // 场景二：需要倒序遍历 → 必须用 ListIterator
        System.out.println("\n  [场景2] 倒序浏览 → 必须用 ListIterator");
        List<String> history = new ArrayList<>(Arrays.asList("第1页", "第2页", "第3页"));
        System.out.println("    浏览记录: " + history);
        ListIterator<String> histIt = history.listIterator(history.size()); // 从末尾开始
        System.out.print("    倒序回退: ");
        while (histIt.hasPrevious()) {
            System.out.print("[" + histIt.previous() + "] ");
        }
        System.out.println("\n  -> 浏览器后退功能就是这样实现的！");

        // 场景三：遍历时插入元素 → 必须用 ListIterator
        System.out.println("\n  [场景3] 遍历时插入占位符 → 必须用 ListIterator");
        List<String> template = new ArrayList<>(Arrays.asList("标题", "内容", "结尾"));
        System.out.println("    模板: " + template);
        ListIterator<String> tempIt = template.listIterator();
        while (tempIt.hasNext()) {
            tempIt.next();
            tempIt.add("---分隔---");
        }
        System.out.println("    插入分隔符后: " + template);

        // =============================================================
        //  总结
        // =============================================================
        System.out.println("\n========================================");
        System.out.println("  总结");
        System.out.println("========================================");
        System.out.println("                Iterator        ListIterator");
        System.out.println("  正向遍历       ✓               ✓");
        System.out.println("  反向遍历       ✗               ✓");
        System.out.println("  删除(remove)   ✓               ✓");
        System.out.println("  修改(set)      ✗               ✓");
        System.out.println("  插入(add)      ✗               ✓");
        System.out.println("  获取索引       ✗               ✓");
        System.out.println("  适用范围       Collection      List only");
        System.out.println("========================================");
        System.out.println("速记：");
        System.out.println("  ListIterator 是 Iterator 的亲儿子，");
        System.out.println("  爸爸只能朝前走，儿子能退能改还能插。");
    }
}
