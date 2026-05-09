/*
 * ============================================================
 *  通俗理解 Java 集合框架：List、Set、Map
 * ============================================================
 *
 *  用生活场景打比方：
 *
 *  List  = 奶茶店的排队小票（有序、可重复、有编号）
 *         第一张、第二张...每张小票都是独立订单，可以点一样的奶茶。
 *
 *  Set  = 微信群成员列表（不重复、无编号）
 *         不管你在群里发多少条消息，群成员里你只出现一次。
 *         无法按"第几个进群"来查，因为没索引。
 *
 *  Map  = 手机通讯录（键值对，用名字查找号码）
 *         用「张三」这个名字，就能快速找到他的电话号码。
 *         名字不能重复，号码可以重复。
 *
 *  ┌──────────────────────────────────────────────────────┐
 *  │                                                      │
 *  │  List  接口                                          │
 *  │  - 有序：怎么放进去就怎么排着                        │
 *  │  - 有索引：list.get(0) 拿第一个                     │
 *  │  - 可重复：可以加两次"张三"                         │
 *  │  - 常用：ArrayList（查快）、LinkedList（增删快）     │
 *  │                                                      │
 *  │  Set   接口                                          │
 *  │  - 不重复：自动去重                                  │
 *  │  - 无索引：不能 get(0)，只能遍历或 contains 查       │
 *  │  - 常用：HashSet（最快）、TreeSet（自动排序）        │
 *  │                                                      │
 *  │  Map   接口                                          │
 *  │  - 键值对：Key → Value，一对一对存的                 │
 *  │  - Key 不重复：同一个名字只能对应一个号码            │
 *  │  - Value 可重复：不同名字可以对应同一个号码          │
 *  │  - 常用：HashMap（最快）、TreeMap（按 Key 排序）     │
 *  │                                                      │
 *  └──────────────────────────────────────────────────────┘
 */

import java.util.*;

public class CollectionDemo {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  Java 集合：List、Set、Map —— 用法演示");
        System.out.println("========================================");

        // =============================================================
        //  一、List —— 排队小票
        // =============================================================
        System.out.println("\n========== 一、List：排队小票（有序、可重复、有索引） ==========");

        List<String> orders = new ArrayList<>();

        // 添加
        orders.add("珍珠奶茶");
        orders.add("柠檬茶");
        orders.add("珍珠奶茶");   // 重复也接受
        orders.add("杨枝甘露");

        System.out.println("奶茶订单: " + orders);
        System.out.println("第 1 张票: " + orders.get(0));       // 按索引取
        System.out.println("第 2 张票: " + orders.get(1));
        System.out.println("总共 " + orders.size() + " 单");

        // 删除
        orders.remove("珍珠奶茶");   // 只删第一个"珍珠奶茶"
        System.out.println("退掉第一杯珍珠奶茶后: " + orders);

        // 遍历
        System.out.print("遍历所有订单: ");
        for (String o : orders) {
            System.out.print("[" + o + "] ");
        }
        System.out.println();

        System.out.println("  -> ArrayList 底层是数组，按索引查快，删中间慢");


        // =============================================================
        //  二、Set —— 微信群成员
        // =============================================================
        System.out.println("\n========== 二、Set：群成员（不重复、无索引） ==========");

        Set<String> members = new HashSet<>();

        members.add("张三");
        members.add("李四");
        members.add("王五");
        members.add("张三");   // 重复，加不进去！
        members.add("张三");   // 再加一次，还是加不进去

        System.out.println("群成员: " + members);
        System.out.println("总共 " + members.size() + " 人（张三加了3次但只算1人）");

        // 判断是否存在
        System.out.println("张三在群里吗？" + members.contains("张三"));
        System.out.println("赵六在群里吗？" + members.contains("赵六"));

        // 删除
        members.remove("李四");
        System.out.println("李四退群后: " + members);

        // 遍历（无序，输出顺序和插入顺序无关）
        System.out.print("遍历成员: ");
        for (String m : members) {
            System.out.print(m + " ");
        }
        System.out.println("\n  -> HashSet 靠 hashCode 去重，不保证顺序");


        // =============================================================
        //  TreeSet —— 自动排序
        // =============================================================
        System.out.println("\n  [扩展] TreeSet：自动排序");

        Set<Integer> scores = new TreeSet<>();
        scores.add(88);
        scores.add(95);
        scores.add(60);
        scores.add(72);
        System.out.println("成绩（自动升序）: " + scores);
        System.out.println("  -> TreeSet 默认从小到大排，存入时自动维护顺序");


        // =============================================================
        //  三、Map —— 手机通讯录
        // =============================================================
        System.out.println("\n========== 三、Map：手机通讯录（键值对，Key不重复） ==========");

        Map<String, String> contacts = new HashMap<>();

        // 添加：put(名字, 号码)
        contacts.put("张三", "13800001111");
        contacts.put("李四", "13800002222");
        contacts.put("王五", "13800003333");
        contacts.put("张三", "13900009999");   // Key 重复 → 覆盖旧值！

        System.out.println("通讯录: " + contacts);
        System.out.println("张三的号码: " + contacts.get("张三"));   // 用 Key 取值

        // 判断
        System.out.println("有李四吗？" + contacts.containsKey("李四"));
        System.out.println("有人号码是 13800002222 吗？" + contacts.containsValue("13800002222"));

        // 删除
        contacts.remove("李四");
        System.out.println("删除李四后: " + contacts);

        // 遍历方式一：只拿所有 Key
        System.out.print("所有联系人: ");
        for (String name : contacts.keySet()) {
            System.out.print(name + " ");
        }
        System.out.println();

        // 遍历方式二：同时拿 Key 和 Value（最常用）
        System.out.print("遍历通讯录: ");
        for (Map.Entry<String, String> entry : contacts.entrySet()) {
            System.out.print(entry.getKey() + "→" + entry.getValue() + "  ");
        }
        System.out.println("\n  -> HashMap 靠 hashCode 定位，查一个 Key 几乎瞬间");


        // =============================================================
        //  TreeMap —— 按 Key 排序的 Map
        // =============================================================
        System.out.println("\n  [扩展] TreeMap：按 Key 排序");

        Map<String, Integer> wordCount = new TreeMap<>();
        wordCount.put("apple",  3);
        wordCount.put("banana", 5);
        wordCount.put("cat",    1);
        System.out.println("单词统计（按字母顺序）: " + wordCount);


        // =============================================================
        //  四、实际场景：综合使用
        // =============================================================
        System.out.println("\n========== 四、实际场景：统计一个句子中的单词出现次数 ==========");
        String sentence = "hello world hello java world java java";
        String[] words = sentence.split(" ");

        Map<String, Integer> counter = new HashMap<>();
        for (String w : words) {
            counter.put(w, counter.getOrDefault(w, 0) + 1);
            // getOrDefault: 如果 w 是第一次出现，返回默认值 0
        }

        System.out.println("句子: " + sentence);
        System.out.println("统计结果: " + counter);
        System.out.println("  -> Map 的经典用法：做计数/缓存/索引");


        // =============================================================
        //  总结
        // =============================================================
        System.out.println("\n========================================");
        System.out.println("  总结");
        System.out.println("========================================");
        System.out.println("接口   生活比喻        特点                  常用实现");
        System.out.println("List   排队小票        有序可重复有索引       ArrayList");
        System.out.println("Set    群成员名单      不重复无索引           HashSet");
        System.out.println("Map    手机通讯录      键值对 Key 不重复      HashMap");
        System.out.println("========================================");
        System.out.println("选哪个？");
        System.out.println("  要顺序/要索引          → List（ArrayList）");
        System.out.println("  要去重                 → Set（HashSet）");
        System.out.println("  要用一个值查另一个值   → Map（HashMap）");
        System.out.println("  要自动排序             → TreeSet / TreeMap");
        System.out.println("========================================");
        System.out.println("速记口诀：");
        System.out.println("  List 排队有序号，Set 自动去重妙，");
        System.out.println("  Map 键值配成双，HashMap 最常用到。");
    }
}
