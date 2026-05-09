/*
 * ============================================================
 *  通俗理解 快速失败 (fail-fast) 和 安全失败 (fail-safe)
 * ============================================================
 *
 *  用生活场景打比方：
 *
 *  快速失败 (fail-fast)  =  老师照着花名册点名
 *      老师点名时，班长突然往花名册上加/删人，花名册变了。
 *      老师立刻发火："名单变了！不准改！" —— 直接抛异常。
 *      典型：ArrayList, HashMap, HashSet
 *
 *  安全失败 (fail-safe)  =  老师拍照了一张花名册
 *      老师先用手机拍了张花名册的照片，然后照着照片点名。
 *      点名过程中，班长在原始花名册上改来改去，老师完全不知道，
 *      因为老师看的是「快照」，不受影响。
 *      典型：CopyOnWriteArrayList, ConcurrentHashMap
 *
 *  核心区别一图看懂：
 *
 *  ┌─────────────────────────────────────────────────────────┐
 *  │  场景              fail-fast               fail-safe    │
 *  ├─────────────────────────────────────────────────────────┤
 *  │  遍历时被修改      立即抛异常              不抛异常      │
 *  │  遍历的数据       原始集合本身            集合的快照     │
 *  │  性能             高（无拷贝开销）        有拷贝开销    │
 *  │  内存             省（不复制）            费（复制一份）│
 *  │  应用场景         单线程/不修改            多线程/可能修改│
 *  │  典型实现         ArrayList, HashMap       CopyOnWrite-  │
 *  │                   HashSet, LinkedList      Concurrent-   │
 *  └─────────────────────────────────────────────────────────┘
 *
 *  一句话总结：
 *    fail-fast：看着原件干活，原件被改了立马翻脸（抛异常）。
 *    fail-safe：拍张照对着照片干活，原件随便改，我无所谓。
 */

import java.util.*;
import java.util.concurrent.*;

public class FailFastVsFailSafeDemo {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  快速失败 vs 安全失败 —— 区别演示");
        System.out.println("========================================");

        // =============================================================
        //  一、快速失败：老师照着花名册点名，名单被改 → 当场发飙
        // =============================================================
        System.out.println("\n========== 一、快速失败 (fail-fast)：照着原件点名 ==========");

        System.out.println("\n  [例1] ArrayList：遍历时直接 add → 当场抛异常");
        List<String> names = new ArrayList<>();
        names.add("张三");
        names.add("李四");
        names.add("王五");
        System.out.println("    点名册: " + names);

        try {
            for (String name : names) {
                System.out.println("    点到: " + name);
                if (name.equals("李四")) {
                    names.add("赵六");   // 老师点名时，班长偷偷加人
                }
            }
        } catch (ConcurrentModificationException e) {
            System.out.println("    ❌ 抛异常了！ConcurrentModificationException");
            System.out.println("    → 老师怒斥：我正在点名，不许改花名册！");
        }

        System.out.println("\n  [例2] ArrayList：用 Iterator.remove 删 → 不抛异常（允许）");
        names = new ArrayList<>(Arrays.asList("张三", "李四", "王五"));
        System.out.println("    点名册: " + names);
        Iterator<String> it = names.iterator();
        while (it.hasNext()) {
            String name = it.next();
            System.out.println("    点到: " + name);
            if (name.equals("李四")) {
                it.remove();      // 用 Iterator 自己的 remove，合法的！
            }
        }
        System.out.println("    删除后: " + names);
        System.out.println("    → 用 Iterator 自己的 remove 是合法的，不会抛异常");

        System.out.println("\n  [例3] ArrayList：用迭代器时另一个线程修改 → 抛异常");
        List<Integer> scores = new ArrayList<>(Arrays.asList(90, 80, 70));
        System.out.println("    成绩单: " + scores);

        try {
            for (Integer s : scores) {
                System.out.println("    查看: " + s);
                if (s == 80) {
                    // 模拟"另一个线程同时修改"
                    // 增强 for 本质也是 Iterator，直接 add 就炸
                    // 这里用原集合的 add 来模拟多线程场景
                }
            }
        } catch (ConcurrentModificationException e) {
            // 上面没有触发，这里只是说明概念
        }

        System.out.println("     → 多线程下，一个线程在遍历 ArrayList，");
        System.out.println("       另一个线程改了它 → 立刻抛异常！");

        // HashMap 也是 fail-fast
        System.out.println("\n  [例4] HashMap：也是 fail-fast");
        Map<String, Integer> map = new HashMap<>();
        map.put("A", 1);
        map.put("B", 2);
        map.put("C", 3);
        System.out.println("    Map: " + map);
        try {
            for (String key : map.keySet()) {
                if (key.equals("B")) {
                    map.put("D", 4);   // 遍历时改 map
                }
            }
        } catch (ConcurrentModificationException e) {
            System.out.println("    ❌ 也抛异常了！HashMap 也是 fail-fast！");
        }

        // =============================================================
        //  二、安全失败：老师拍照花名册后点名，原件改了也不知道
        // =============================================================
        System.out.println("\n========== 二、安全失败 (fail-safe)：拍张照片点名 ==========");

        System.out.println("\n  [例5] CopyOnWriteArrayList：遍历时改原件 → 不抛异常！");
        List<String> safeNames = new CopyOnWriteArrayList<>();
        safeNames.add("张三");
        safeNames.add("李四");
        safeNames.add("王五");
        System.out.println("    点名册: " + safeNames);

        try {
            int i = 0;
            for (String name : safeNames) {
                System.out.println("    点到: " + name);
                if (name.equals("李四")) {
                    safeNames.add("赵六");   // 班长在原件上偷偷加人
                    System.out.println("    班长偷加了「赵六」");
                }
                if (++i > 4) break; // 防止无限循环
            }
        } catch (ConcurrentModificationException e) {
            System.out.println("    ❌ 不会走到这！");
        }
        System.out.println("    遍历后的原件: " + safeNames);
        System.out.println("    → 没抛异常！遍历时看到的是快照，新增的「赵六」没被遍历到");

        System.out.println("\n  [例6] 证明 CopyOnWriteArrayList 的迭代器看的是快照");
        List<String> snapshot = new CopyOnWriteArrayList<>();
        snapshot.add("A");
        snapshot.add("B");
        snapshot.add("C");
        System.out.println("    原始: " + snapshot);

        Iterator<String> snapIt = snapshot.iterator();
        System.out.println("    拿到迭代器（拍快照），开始遍历...");

        // 拿到迭代器后修改原始集合
        snapshot.add("D");
        snapshot.add("E");
        snapshot.remove("A");
        System.out.println("    原件被改成: " + snapshot);

        System.out.print("    迭代器看到的是: ");
        while (snapIt.hasNext()) {
            System.out.print("[" + snapIt.next() + "] ");   // 还是 A,B,C
        }
        System.out.println();
        System.out.println("    → 迭代器看到的还是旧快照 [A, B, C]，原件怎么变都不影响！");

        // ConcurrentHashMap 也是 fail-safe
        System.out.println("\n  [例7] ConcurrentHashMap：也是 fail-safe");
        ConcurrentHashMap<String, Integer> safeMap = new ConcurrentHashMap<>();
        safeMap.put("A", 1);
        safeMap.put("B", 2);
        safeMap.put("C", 3);
        System.out.println("    Map: " + safeMap);

        try {
            for (String key : safeMap.keySet()) {
                System.out.println("    遍历到: " + key + "=" + safeMap.get(key));
                if (key.equals("B")) {
                    safeMap.put("D", 4);   // 遍历时改，不抛异常！
                    System.out.println("    添加了 D=4（遍历时修改！）");
                }
            }
        } catch (ConcurrentModificationException e) {
            System.out.println("    ❌ 不会走到这！ConcurrentHashMap 是 fail-safe！");
        }
        System.out.println("    最终 Map: " + safeMap);
        System.out.println("    → 没抛异常！ConcurrentHashMap 是 fail-safe");

        // =============================================================
        //  三、底层原理通俗版
        // =============================================================
        System.out.println("\n========== 三、底层原理（通俗版） ==========");

        System.out.println("\n  快速失败 (fail-fast) 怎么做到的？");
        System.out.println("  ┌─────────────────────────────────────────┐");
        System.out.println("  │  集合维护一个 modCount 计数器            │");
        System.out.println("  │  每次增删改，modCount + 1                │");
        System.out.println("  │  获取迭代器时，记住当前 modCount 值      │");
        System.out.println("  │  每次 next() 时检查 modCount 是否变了    │");
        System.out.println("  │  变了 → 有人动过集合 → 抛异常！         │");
        System.out.println("  └─────────────────────────────────────────┘");

        System.out.println("\n  安全失败 (fail-safe) 怎么做到的？");
        System.out.println("  ┌─────────────────────────────────────────┐");
        System.out.println("  │  迭代器创建时，复制一份当前数据快照      │");
        System.out.println("  │  后续遍历全在快照上进行                  │");
        System.out.println("  │  原集合怎么改都和迭代器无关              │");
        System.out.println("  │  代价：多花内存、多花复制时间            │");
        System.out.println("  └─────────────────────────────────────────┘");

        // =============================================================
        //  四、选择指南
        // =============================================================
        System.out.println("\n========== 四、什么时候用哪个？ ==========");
        System.out.println();
        System.out.println("  用 fail-fast (ArrayList / HashMap)：");
        System.out.println("    - 单线程环境，遍历过程中不会修改集合");
        System.out.println("    - 遍历时若确实被意外修改，尽早暴露 bug");
        System.out.println("    - 不想为多线程安全付出拷贝成本");
        System.out.println();
        System.out.println("  用 fail-safe (CopyOnWriteArrayList / ConcurrentHashMap)：");
        System.out.println("    - 多线程环境，读多写少");
        System.out.println("    - 要一边遍历一边修改");
        System.out.println("    - 遍历过程中允许看到「稍旧」的数据");

        // =============================================================
        //  总结
        // =============================================================
        System.out.println("\n========================================");
        System.out.println("  总结");
        System.out.println("========================================");
        System.out.println("               fail-fast         fail-safe");
        System.out.println("  比喻         照着原件点名      拍张照片点名");
        System.out.println("  遍历时被改   抛异常            正常运行");
        System.out.println("  数据来源     原件              快照");
        System.out.println("  线程安全     否                是");
        System.out.println("  内存成本     低                高");
        System.out.println("  时间成本     低                高");
        System.out.println("  典型类       ArrayList         CopyOnWriteArrayList");
        System.out.println("              HashMap           ConcurrentHashMap");
        System.out.println("========================================");
        System.out.println("速记口诀：");
        System.out.println("  快速失败看原件，改了立马就翻脸；");
        System.out.println("  安全失败拍照片，随便你改我看不见。");
    }
}
