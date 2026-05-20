# JVM 进阶 —— 通熟易懂的 JVM 核心知识

---

## 一、运行时数据区

### 1. 运行时数据区中包含哪些区域？

```text
┌─────────────────────────────────────────┐
│              运行时数据区                │
├─────────────────────────────────────────┤
│  线程共享区                              │
│  ┌──────────┐  ┌──────────────────────┐ │
│  │   堆     │  │      方法区/元空间    │ │
│  │  (Heap)  │  │   (Method Area /     │ │
│  │          │  │    Metaspace)        │ │
│  └──────────┘  └──────────────────────┘ │
├─────────────────────────────────────────┤
│  线程独享区                              │
│  ┌──────┐ ┌──────┐ ┌──────┐           │
│  │虚拟机栈│ │本地方法│ │程序   │           │
│  │(VM   │ │  栈   │ │计数器 │           │
│  │Stack)│ │(Native│ │(PC   │           │
│  │      │ │Method │ │Reg)  │           │
│  │      │ │Stack) │ │      │           │
│  └──────┘ └──────┘ └──────┘           │
├─────────────────────────────────────────┤
│  直接内存 (堆外)    不是 JVM 运行时数据区 │
│  (Direct Memory)    的一部分，但也重要    │
└─────────────────────────────────────────┘
```

| 区域 | 线程共享？ | OutOfMemoryError？ |
|------|-----------|-------------------|
| 堆 (Heap) | 共享 | 会 |
| 方法区/元空间 (Method Area / Metaspace) | 共享 | 会 |
| 虚拟机栈 (VM Stack) | 独享 | 会 |
| 本地方法栈 (Native Method Stack) | 独享 | 会 |
| 程序计数器 (PC Register) | 独享 | **不会** |
| 直接内存 (Direct Memory) | 共享 | 会 |

> **记忆口诀**：两共享（堆、方法区），三独享（虚拟机栈、本地方法栈、程序计数器）。只有程序计数器不会 OOM，因为 JVM 规范规定。

### 2. 方法区和永久代的关系

**一句话：方法区是规范，永久代是 HotSpot 对方法区的一种实现。**

打个比方：

- **方法区** 就像 "汽车" 这个概念——它是一个标准/规范
- **永久代** 就像 "特斯拉 Model 3"——它是 HotSpot VM 这个品牌的具体实现
- **元空间** 就像 "比亚迪汉"——它是 HotSpot VM 后来换的新实现

```text
JDK 1.7 及以前:  方法区 = 永久代(PermGen)，在 JVM 堆内存中
JDK 1.8 及以后:  方法区 = 元空间(Metaspace)，在本地内存(堆外)中
```

---

## 二、栈和堆

### 3. 栈中存放什么数据？堆中呢？

```java
public class MemoryDemo {
    // 静态变量 → 存放在 方法区(Metaspace)
    public static String CLASS_NAME = "MemoryDemo";

    // 实例变量 → 存放在 堆中（跟着对象走）
    private String name;
    private int age;

    public int add(int a, int b) {
        // a, b → 存放在 栈帧的局部变量表中
        // result → 存放在 栈帧的局部变量表中
        int result = a + b;
        return result;
    }

    public static void main(String[] args) {
        // main 方法的栈帧入栈

        // obj 引用 → 存放在 栈（局部变量表）
        // new MemoryDemo() 对象本体 → 存放在 堆
        MemoryDemo obj = new MemoryDemo();

        int sum = obj.add(3, 5); // 调用方法，新栈帧入栈
        // add 执行完毕，栈帧出栈
    }
}
```

| 存储位置 | 存放内容 | 形象比喻 |
|---------|---------|---------|
| **栈** | 局部变量表（基本类型、对象引用）、操作数栈、方法返回值、动态链接 | 工厂的**工作台**——干完活就清空 |
| **堆** | 对象实例、数组 | 工厂的**仓库**——存东西，GC 来清理 |
| **方法区** | 类的元信息（字段、方法）、静态变量、常量池、JIT 编译后的代码 | 工厂的**图纸库**——存模板和配方 |

---

## 三、永久代 vs 元空间

### 4. 为什么要将永久代 (PermGen) 替换为元空间 (MetaSpace)?

用一个生活例子理解：

```text
永久代 = 你家厨房的冰箱（大小固定，改不了）
元空间 = 你家楼下的超市（无限大，按需拿）

问题来了：
- 你往冰箱塞太多东西（加载很多类）→ 冰箱爆了（PermGen OOM）
- 你想换个更大的冰箱？→ 得重新装修厨房（重启 JVM，改 -XX:MaxPermSize）
- 超市呢？→ 想拿多少拿多少，只要你有钱（物理内存够）
```

**具体原因：**

| 对比项 | 永久代 (PermGen) | 元空间 (Metaspace) |
|--------|-----------------|-------------------|
| 存储位置 | JVM 堆内存内 | 本地内存（堆外） |
| 大小限制 | `-XX:MaxPermSize`固定上限 | 默认无上限，受物理内存限制 |
| OOM 风险 | 容易 `java.lang.OutOfMemoryError: PermGen` | 更不容易（可弹性扩展） |
| 类卸载 | 复杂，Full GC 时 | 更简单，GC 时清理 |
| 字符串常量池 | JDK 1.7 移到堆 | 在堆中 |

**核心原因总结：**
1. **避免 OOM**：永久代大小固定，加载太多类就炸；元空间用本地内存，几乎不会炸
2. **方便 GC**：永久代和堆混在一起，GC 复杂；元空间独立，简化了 GC
3. **与 JRockit 统一**：JRockit 从来没有永久代，HotSpot 为了统一，也改了

---

## 四、字符串常量池

### 5. 字符串常量池在什么位置？JDK 1.7 为什么要移动？

```text
JDK 1.6 及以前:  永久代(PermGen) 中
                   ↓ 从堆外移到堆内 ↓
JDK 1.7      :  堆(Heap) 中
JDK 1.8      :  堆(Heap) 中

为什么从 PermGen 移到 Heap？
```

**用例子说明：**

```java
public class StringPoolDemo {
    public static void main(String[] args) {
        // JDK 1.6:
        // String对象 在堆，常量池引用 在 PermGen
        // → PermGen 是独立空间，大小固定
        // → 如果大量调用 intern()，PermGen 满了就 OOM
        // → PermGen 的 GC 很少触发，满了才回收

        // JDK 1.7:
        // String对象 在堆，常量池引用 也在堆
        // → 堆空间大，不容易满
        // → 堆的 GC 频繁，常量池里的废弃常量能被及时回收
    }
}
```

**核心原因：**

1. **PermGen 太小**：永久代默认很小，大量字符串 intern 会撑爆它
2. **GC 不友好**：永久代 GC 频率低，废弃的字符串得不到及时回收
3. **堆更适合**：字符串是频繁创建和销毁的对象，放堆里让 GC 统一管理更合理

---

## 五、堆空间结构

### 6. 堆空间的基本结构？什么情况下对象会进入老年代？

```text
                    堆内存 (Heap)
┌──────────────────────────────────────────────┐
│              新生代 (Young Gen)              │
│  ┌──────┐ ┌──────────────┐ ┌──────────────┐ │
│  │Eden │ │ Survivor S0  │ │ Survivor S1  │ │
│  │ 区   │ │    区        │ │    区        │ │
│  │(8/10)│ │   (1/10)    │ │   (1/10)    │ │
│  └──────┘ └──────────────┘ └──────────────┘ │
├──────────────────────────────────────────────┤
│              老年代 (Old Gen)                │
│                  (2/3 堆)                     │
└──────────────────────────────────────────────┘
```

**对象进入老年代的 4 种情况：**

```java
public class OldGenDemo {
    // 情况1：躲过 15 次 GC 的"老油条"
    // -XX:MaxTenuringThreshold=15（默认）
    // Survivor 区的对象每熬过一次 Minor GC，年龄 +1
    // 年龄到了，晋升老年代

    // 情况2：大对象直接进老年代
    // -XX:PretenureSizeThreshold=3M
    // 比如 10MB 的 byte[]，新生代默认 Eden 才 800MB * 8/10
    // 大对象在 Eden 和 Survivor 之间来回复制不划算

    // 情况3：Survivor 装不下了
    // 同一年龄的对象超过 Survivor 区 50% 时，
    // 大于等于该年龄的对象直接进老年代

    // 情况4：担保机制
    // Minor GC 后，Survivor 放不下存活对象
    // 老年代来担保，直接进老年代
}
```

### 7. 大对象放在哪个内存区域？

- **直接进老年代**（如果超过 `-XX:PretenureSizeThreshold`）
- 例如：`byte[] big = new byte[10 * 1024 * 1024];` // 10MB 大数组
- 原因：大对象在 Eden 和 Survivor 之间来回复制成本太高，直接放老年代省事

> 就像搬家时：一本书放小车搬，一个大沙发直接用卡车送到目的地。

---

## 六、直接内存

### 8. 直接内存有什么用？如何使用？

**是什么**：堆外的操作系统本地内存，不是 JVM 管理的内存。

**有什么用**：

```java
import java.nio.ByteBuffer;

public class DirectMemoryDemo {
    public static void main(String[] args) {
        // 堆内存 IO（需要拷贝）
        // 用户空间 → 内核空间 → 网卡/磁盘
        // 数据多拷贝了一次！

        // 直接内存 IO（零拷贝）
        ByteBuffer directBuffer = ByteBuffer.allocateDirect(1024);
        // 直接和操作系统交互，省去一次拷贝
        // 适合：NIO、Netty、大文件读写
    }
}
```

| 对比 | 堆内存 (HeapByteBuffer) | 直接内存 (DirectByteBuffer) |
|------|----------------------|--------------------------|
| 分配速度 | 快 | 慢（要跟 OS 申请） |
| IO 性能 | 慢（多一次拷贝） | 快（零拷贝） |
| GC 管理 | JVM 自动回收 | JVM 只回收引用对象，实际内存靠 Cleaner 或手动释放 |
| 适合场景 | 普通对象 | 大量 IO 操作（Netty、Kafka 等） |

> 就像寄快递：堆内存是"把东西搬到快递站再寄"，直接内存是"快递员上门取件"——少搬一次，快很多。

---

## 七、对象的创建过程

### 9. Java 对象的创建过程（五步）

```java
Object obj = new Object(); // 这一行背后的故事：
```

```text
步骤 1：类加载检查
  ↓  虚拟机遇到 new 指令，去常量池找类的符号引用
  ↓  检查类是否已加载、解析、初始化
  ↓  没有？→ 先加载类

步骤 2：分配内存
  ↓  在堆中给对象划分一块内存
  ↓  ┌─ 指针碰撞（Bump the Pointer）：堆内存规整时，指针移动
  ↓  └─ 空闲列表（Free List）：堆内存不规整时，从列表找块
  ↓  并发分配安全：CAS + 失败重试  或  TLAB（线程本地分配缓冲）

步骤 3：初始化零值
  ↓  把分配到的内存空间全部清零（不包括对象头）
  ↓  int → 0, boolean → false, 引用 → null
  ↓  保证对象字段不用赋初值就能直接使用

步骤 4：设置对象头
  ↓  设置这个对象是哪个类的实例、哈希码、GC 分代年龄
  ↓  锁状态标志、线程持有的锁等

步骤 5：执行 init 方法
  ↓  调用构造函数（<init>() 方法）
  ↓  按照程序员的意愿初始化对象
  ↓  一个真正的对象诞生了！
```

> **记忆口诀**：查（加载）、分（内存）、零（零值）、头（对象头）、Init（初始化）

---

## 八、对象访问定位

### 10. 对象的访问定位（句柄和直接指针）

```java
Object obj = new Object(); // obj 怎么找到堆里的对象？
```

**方式一：句柄访问（间接指针）**

```text
栈 (obj引用)          句柄池               堆
┌─────────┐     ┌──────────────┐     ┌──────────────┐
│ 句柄地址 │────→│  对象数据指针 │────→│  Object对象  │
└─────────┘     │              │     │  (实例数据)  │
                │  类型数据指针 │──→  └──────────────┘
                └──────────────┘        ┌──────────────┐
                                        │  类元信息    │
                                        └──────────────┘
```

**方式二：直接指针访问（HotSpot 用这个）**

```text
栈 (obj引用)                        堆
┌──────────────┐    ┌──────────────────────────────┐
│  对象地址     │───→│  ┌── 对象头 ──────────────┐  │
└──────────────┘    │  │ 类型指针 → 类元信息    │  │
                    │  └───────────────────────┘  │
                    │  ┌── 实例数据 ───────────┐  │
                    │  │ int a, String b...    │  │
                    │  └───────────────────────┘  │
                    └──────────────────────────────┘
```

| 对比 | 句柄访问 | 直接指针（HotSpot方式） |
|------|---------|----------------------|
| 速度 | 慢（两次指针定位） | 快（一次指针定位） |
| GC 移动对象时 | 只改句柄池 | 要改所有引用（但 HotSpot 有办法） |

---

## 九、垃圾回收（GC）

### 11. 为什么需要 GC？

```java
public class WithoutGC {
    // 如果手动管理内存（像 C/C++）：
    public void badExample() {
        byte[] data = new byte[1024 * 1024];
        // 用完了...忘记 free(data);
        // → 内存泄漏！程序运行越久，可用内存越少
        // → 最终 OOM，程序崩溃
    }
}

// 有了 GC：
// - 开发者不用手动释放内存
// - JVM 自动识别垃圾对象并回收
// - 减少内存泄漏和悬垂指针的问题
```

### 12. Minor GC 和 Full GC

| GC 类型 | 发生区域 | 触发条件 | Stop The World? |
|---------|---------|---------|----------------|
| **Minor GC** | 新生代 | Eden 区满了 | 是（但时间很短） |
| **Major GC** | 老年代 | 老年代空间不足 | 是 |
| **Full GC** | 整个堆 + 方法区 | System.gc()、老年代/方法区满了、担保失败 | 是（时间较长） |
| **Mixed GC** | 新生代 + 部分老年代（G1） | 老年代占比达到阈值 | 是（可控） |

**Minor GC 详细过程：**

```text
1. 新对象 → Eden 区
2. Eden 区满了 → 触发 Minor GC
3. 存活对象 → Survivor S0（年龄+1），别的回收掉
4. 下次 Eden 又满了 → 存活对象进入 Survivor S1
5. 来回倒腾，年龄够了 → 晋升老年代
```

**Full GC 触发条件：**

```java
// 1. 显示调用（别这样干！）
System.gc();          // 只是建议，不一定马上执行
Runtime.getRuntime().gc();  // 同上

// 2. 老年代空间不足
// 大对象直接进老年代，老年代满了

// 3. 方法区/元空间不足
// 加载了太多类

// 4. 担保失败
// Minor GC 后 Survivor 放不下，老年代也放不下

// 5. CMS GC 并发失败
// Concurrent Mode Failure
```

> **Minor GC 会 STW 吗？** 会的！因为 Minor GC 需要通过 GC Roots 找存活对象，必须暂停所有用户线程，才能保证引用关系不变。但因为新生代小，STW 时间通常很短（几毫秒到几十毫秒）。

---

## 十、如何判断对象是否死亡

### 13. 引用计数法 vs 可达性分析

**引用计数法（Python、Object-C 用过）：**

```java
public class RefCount {
    Object ref;

    public static void main(String[] args) {
        RefCount a = new RefCount(); // a引用计数=1
        RefCount b = new RefCount(); // b引用计数=1
        a.ref = b;                   // b引用计数=2
        b.ref = a;                   // a引用计数=2
        a = null;                    // a引用计数=1
        b = null;                    // b引用计数=1
        // 问题：a 和 b 已经没有任何外部引用了
        // 但它们互相引用，计数都不为 0
        // 永远不会被回收！→ 循环引用问题
    }
}
```

> Java **没有用**引用计数法，因为有循环引用问题。Java 用的是可达性分析算法。

**可达性分析算法：**

```text
         GC Roots（起点）
        /    |    \
       /     |     \
     对象A  对象B  对象C     ← 被 GC Roots 直接/间接引用 → 存活
      |                   ↙
     对象D              对象E   ← 从 GC Roots 不可达 → 死亡，回收
      |
     对象F （不可达）
```

### 14. 可达性分析算法的流程 + GC Roots 有哪些？

**GC Roots 对象：**

| GC Root 类型 | 示例 |
|-------------|------|
| 虚拟机栈中引用的对象 | 方法里的局部变量 |
| 方法区中静态属性引用的对象 | `static Object obj = new Object();` |
| 方法区中常量引用的对象 | `static final Object OBJ = new Object();` |
| 本地方法栈中 JNI 引用的对象 | Native 方法里的对象 |
| Java 虚拟机内部的引用 | 基本类型的 Class 对象、系统类加载器、常驻的异常对象 |
| 所有被同步锁持有的对象 | `synchronized(obj)` 中的 obj |
| 反映 Java 虚拟机内部情况的 JMXBean、JVMTI 回调等 | |

**可达性分析算法流程：**

```text
1. 以 GC Roots 为起点，从这些节点开始向下搜索
2. 搜索走过的路径称为 "引用链"（Reference Chain）
3. 从 GC Roots 能到达的对象 → 存活
4. 从 GC Roots 不能到达的对象 → 至少需要被标记两次才真正回收：

   标记一次
     ↓
   （如果有 finalize() 且没被调用过）
     ↓
   放入 F-Queue，由 Finalizer 线程执行 finalize()
     ↓
   如果对象在 finalize() 中重新建立了引用 → 复活
     ↓
   如果没建立引用 → 标记第二次 → 真正回收
```

### 15. 如何判断常量是废弃常量？如何判断类是无用的类？

**判断废弃常量：**

```java
// 比如 "hello" 这个常量在字符串常量池中
// 没有任何 String 对象引用它 → 它就是废弃常量
// 下次 GC 时可能被清理

String s = "hello";  // "hello" 在常量池中，被引用
s = "world";         // "hello" 可能变成废弃常量（如果没有其他地方引用它）
```

**判断无用的类（需要同时满足 3 个条件）：**

```text
1. 该类的所有实例都已被回收（堆中没有该类的任何实例）
2. 加载该类的 ClassLoader 已被回收
3. 该类的 java.lang.Class 对象没有被任何地方引用（无法通过反射访问）

满足这三个条件后，垃圾收集器才能卸载这个类。
```

---

## 十一、垃圾收集算法

### 16. 垃圾收集有哪些算法？

| 算法 | 原理 | 优点 | 缺点 | 适用区域 |
|------|------|------|------|---------|
| **标记-清除** | 标记垃圾 → 清除 | 简单 | 产生内存碎片 | 老年代（CMS） |
| **标记-复制** | 标记存活 → 复制到新空间 → 清空旧空间 | 无碎片 | 浪费一半内存 | 新生代 |
| **标记-整理** | 标记存活 → 全部挪到一端 → 清除边界外 | 无碎片 | STW 时间较长 | 老年代 |

**图解：**

```text
标记-清除：                       标记-复制：                   标记-整理：
[活][死][活][死][死][活]          [活][死][活][死][死][活]     [活][死][活][死][死][活]
  ↓ 清除死对象                     ↓ 把活的复制到另一边           ↓ 把活的挪到一端
[活]   [活]        [活]           [死][死][死][死][死][死]     [活][活][活][ ][ ][ ]
  ↓ 问题：碎片，大对象放不下        [活][活]   [活]              ↓ 清理边界外
                                   ↓ 浪费空间，但没碎片          ✅ 无碎片，空间利用率高
```

---

## 十二、垃圾收集器

### 17. 默认垃圾回收器 / ZGC

| JDK 版本 | 默认 GC |
|---------|---------|
| JDK 8 | Parallel GC（关注吞吐量） |
| JDK 9 | G1 GC |
| JDK 11+ | G1 GC |
| JDK 17+ | G1 GC |

**ZGC 了解吗？**

```text
ZGC (Z Garbage Collector) — JDK 11 引入，JDK 15 正式生产可用
核心目标：STW 时间不超过 10ms，支持 TB 级堆内存

特点：
- 并发标记、并发整理（大部分工作和用户线程一起跑）
- 染色指针（Colored Pointers）：把 GC 信息编码到指针里
- 读屏障（Load Barrier）：并发移动对象时保持一致性
- STW 极短：只需扫描 GC Roots 的极短时间

一句话：ZGC 是"几乎感觉不到停顿"的垃圾收集器，适合大内存、低延迟场景
```

### 18. CMS 垃圾收集器的四个步骤 + 缺点

```text
CMS：Concurrent Mark Sweep（并发标记清除）

步骤：
  1. 初始标记 (Initial Mark)       → STW，只标记 GC Roots 直接关联的对象，很快
  2. 并发标记 (Concurrent Mark)     → 和用户线程并发，遍历整个对象图，耗时长但不 STW
  3. 重新标记 (Remark)              → STW，修正并发标记期间变动的标记，比初始标记稍长
  4. 并发清除 (Concurrent Sweep)    → 和用户线程并发，清除垃圾，不 STW
```

```java
// CMS 的缺点（也是面试重点）：

// 缺点 1：对 CPU 资源敏感
// 并发阶段占用 CPU，减少用户线程吞吐量

// 缺点 2：无法处理浮动垃圾
// 并发标记和并发清除期间新产生的垃圾，只能等下次 GC
// 如果老年代预留空间不足（-XX:CMSInitiatingOccupancyFraction），
// 就会出现 Concurrent Mode Failure → 退化为 Serial Old → 长时间 STW

// 缺点 3：产生内存碎片
// 因为只清除不整理（Mark-Sweep），内存碎片化
// 碎片太多导致大对象无法分配 → 触发 Full GC
// 可以配 -XX:+UseCMSCompactAtFullCollection 在 Full GC 时顺便整理
```

### 19. 并发标记要解决什么问题？

**解决的核心问题：对象消失问题（漏标）**

```text
并发标记时，用户线程还在跑，对象的引用关系在变化。
可能发生 "本该存活的对象被误判为垃圾" 的情况。

经典场景：
  标记线程刚扫完对象 A，发现 A 引用了 B
    此时用户线程把 A → B 的引用断开
    同时用户线程把 C → B 的引用建立
  标记线程接下来扫 C，而 C 不是 GC Root（扫不到），B 就被漏掉了！
```

**解决方案（CMS 和 G1 都用）：**

```text
CMS 使用 "增量更新" (Incremental Update)：
  当 A → B 的引用断开时，把 A 记录下来
  重新标记阶段再扫一遍 A，看它引用了谁

G1/Shenandoah 使用 "原始快照" (SATB, Snapshot At The Beginning)：
  并发标记开始时拍一张快照
  标记期间新产生的对象默认存活
  旧对象引用断开时记录，重新标记阶段再看
```

**漏标必须同时满足两个条件（Wilson 1994）：**
1. 赋值器插入了一条或多条从黑色对象到白色对象的引用
2. 赋值器删除了所有从灰色对象到白色对象的直接或间接引用

> 破坏其中一个条件即可解决：CMS 破坏条件 2（增量更新），G1 破坏条件 1（SATB）。

---

## 十三、G1 垃圾收集器

### 20. G1 的步骤 + 缺点

```text
G1 (Garbage First)：将堆分成多个大小相等的 Region（每个 1~32MB）

G1 的步骤：
  1. 初始标记 (Initial Mark)
     → STW，标记 GC Roots 直接关联的对象（和 Minor GC 一起完成）

  2. 并发标记 (Concurrent Mark)
     → 和用户线程并发，做可达性分析

  3. 最终标记 (Remark)
     → STW，修正 SATB 漏标，很短

  4. 筛选回收 (Cleanup / Mixed GC)
     → 统计每个 Region 的回收价值（垃圾最多的先回收）
     → 把要回收的 Region 的存活对象复制到空 Region
     → 清理旧 Region
```

```text
G1 的缺点：
  1. 记忆集(Remembered Set) 占用内存大
     每个 Region 都要维护一个记忆集来记录跨 Region 引用
     内存占用可能达到堆的 10%-20%

  2. 写屏障开销
     每次引用赋值都要维护记忆集，有一定性能开销

  3. 大对象处理
     Humongous Object（超过 Region 50% 的对象）
     直接在连续的 Region 中分配，GC 效率不如普通对象

  4. 并发模式下的吞吐量
     追求低延迟，但吞吐量可能不如 Parallel GC
```

---

## 十四、安全点和安全区

### 21. 安全点 (Safepoint) 和安全区 (Safe Region)

**安全点：**

```text
不是所有地方都能停下来做 GC 的！必须走到"安全点"才能停。

你可以理解为高速公路上的"服务区"：
  - 你在高速上开车（用户线程执行）
  - GC 来了（要停车）
  - 必须在服务区（安全点）才能停下来
  - 不能随便在路中间停车

安全点是哪些地方？
  - 方法调用
  - 循环跳转
  - 异常跳转
  - 方法返回

JVM 怎么让线程停在安全点？
  - 抢先式中断：虚拟机中断所有线程，没到安全点的让它跑到安全点（几乎不用了）
  - 主动式中断：设置一个标志位，线程主动轮询，发现标志位就挂起
```

**安全区：**

```text
安全区 = 一段代码片段中，引用关系不会发生变化。

相当于：你不在高速上了，在服务区里休息（Sleep/Blocked 状态）
  - 线程在安全区时可以安全地做 GC
  - 线程要离开安全区时，检查 GC 是否完成
  - 完成了 → 离开；没完成 → 等着

典型场景：
  - 线程 sleep 时
  - 线程 blocked 时（等锁）
```

---

## 十五、类加载

### 22. 什么是类加载？何时类加载？

```text
类加载 = 把 .class 文件（字节码）读到内存中，
       转换成 JVM 能识别的 Class 对象的过程。

什么时候会触发类加载？
  1. new 一个对象时
  2. 访问一个类的静态变量/方法时
  3. 反射调用时
  4. 初始化子类时，父类必须先初始化
  5. 启动类（main 方法所在的类）
  6. JDK 7 的动态语言支持等
```

### 23. 类加载流程

```text
加载  →  验证  →  准备  →  解析  →  初始化  →  使用  →  卸载
Loading  Verify  Prepare  Resolve  Initialize   Use    Unload

加载   : 通过全限定名读取字节码，生成 Class 对象
验证   : 检查字节码是否安全（文件格式、元数据、字节码、符号引用 四层验证）
准备   : 给静态变量分配内存，设置零值（static int a = 10 → 准备阶段 a = 0）
解析   : 把常量池中的符号引用替换为直接引用（内存地址）
初始化 : 执行类构造器 <clinit>()，真正给静态变量赋值（a = 10）
```

> **注意**：准备阶段 `static int a = 10` 的值为 0；到了初始化阶段才真正变成 10。
>
> 但如果 `static final int a = 10`，编译期 Javac 就会生成 ConstantValue 属性，准备阶段就直接赋值 10。

### 24. 类加载器及双亲委派

**有哪些类加载器？**

```java
public class ClassLoaderDemo {
    public static void main(String[] args) {
        // Bootstrap ClassLoader (C++实现)
        System.out.println(String.class.getClassLoader());
        // null ← 因为是 C++ 实现的，Java 层拿不到引用

        // Extension/Platform ClassLoader
        // 加载 jre/lib/ext 下的 jar
        ClassLoader ext = ClassLoader.getSystemClassLoader().getParent();
        System.out.println(ext); // sun.misc.Launcher$ExtClassLoader

        // Application ClassLoader
        // 加载 classpath 下的类
        ClassLoader app = ClassLoader.getSystemClassLoader();
        System.out.println(app); // sun.misc.Launcher$AppClassLoader
    }
}
```

```text
类加载器层级关系：
  Bootstrap ClassLoader (启动类加载器)  ← rt.jar, 核心类库
        ↓ (parent)
  Extension/Platform ClassLoader (扩展类加载器)  ← jre/lib/ext
        ↓ (parent)
  Application ClassLoader (应用类加载器)  ← classpath
        ↓ (用户可自定义)
  Custom ClassLoader (自定义类加载器)
```

### 25. 双亲委派模型

```text
双亲委派的工作流程：
  一个类加载器收到加载请求时：
    1. 先检查自己是否已加载过
    2. 没有 → 向上委托给父加载器
    3. 父加载器也一直向上委托
    4. 直到 Bootstrap ClassLoader（最顶层）
    5. 从上往下尝试加载
    6. 谁先找到了，谁来加载
```

```java
// 双亲委派的核心代码（ClassLoader.java）：
protected Class<?> loadClass(String name, boolean resolve)
    throws ClassNotFoundException {
    synchronized (getClassLoadingLock(name)) {
        // 1. 先查缓存
        Class<?> c = findLoadedClass(name);
        if (c == null) {
            try {
                // 2. 有父加载器 → 委托给父加载器
                if (parent != null) {
                    c = parent.loadClass(name, false);
                } else {
                    // 3. 没有父加载器 → 委托给 Bootstrap
                    c = findBootstrapClassOrNull(name);
                }
            } catch (ClassNotFoundException e) {
                // 父加载器找不到，继续
            }
            if (c == null) {
                // 4. 父加载器都找不到，自己尝试加载
                c = findClass(name);
            }
        }
        return c;
    }
}
```

### 26. 为什么需要双亲委派？

```text
核心原因：保证 Java 核心类库的安全性和唯一性。

例子：
  如果你自己写了一个 java.lang.String 类，打包成 jar 放到 classpath：
    1. 类加载请求 → AppClassLoader
    2. 向上委托 → Extension ClassLoader
    3. 向上委托 → Bootstrap ClassLoader
    4. Bootstrap 发现 rt.jar 里已经有 java.lang.String
    5. 直接用官方的 String，你的冒牌 String 不会被加载！

  避免后果：
    - 核心类库被篡改（安全威胁）
    - 同一个类被多次加载，产生 ClassCastException
```

---

## 十六、Tomcat 如何打破双亲委派？

### 27. Tomcat 如何打破双亲委托机制？

```text
Tomcat 需要打破双亲委派的原因（场景）：

  ┌─────────────────────────────────────────┐
  │              Tomcat 服务器               │
  ├─────────────────────────────────────────┤
  │  WebApp A           WebApp B            │
  │  Spring 4.0         Spring 5.0          │
  │  UserService.java   UserService.java    │
  └─────────────────────────────────────────┘

需求 1：不同 WebApp 要隔离（不同版本的 Spring 不能互相打架）
需求 2：不同 WebApp 要共享（Tomcat 的公共库不需要每个 WebApp 都加载一次）
需求 3：热部署（替换 class 文件后不用重启整个 Tomcat）
```

```text
Tomcat 的类加载器结构：
  Bootstrap ClassLoader
        ↓
  System/AppClassLoader
        ↓
  Common ClassLoader  ← 各 WebApp 共享的公共库
        ↓
  ┌─────┴─────┐
  ↓           ↓
  Catalina    Shared
  ClassLoader ClassLoader
  (Tomcat     (共享)
  私有)       ↓
         WebApp ClassLoader   ← 每个 WebApp 独立一个
         (打破双亲委派的关键！)

WebApp ClassLoader 的加载顺序（反过来了！）：
  1. 先从自己的 /WEB-INF/classes 加载
  2. 找不到再从 /WEB-INF/lib/*.jar 加载
  3. 还是找不到，才委托给父加载器

因为先自己加载而不是先委托父加载器 → 打破了双亲委派！
```

---

## 十七、JVM 参数与调优

### 28. 堆内存相关的 JVM 参数

```bash
# ========== 堆相关 ==========
-Xms2048m              # 初始堆大小（ms = memory start）
-Xmx2048m              # 最大堆大小（mx = memory max）
                       # 生产环境建议 -Xms == -Xmx，避免堆扩容缩容

-Xmn512m               # 新生代大小（推荐不直接设，用比值）
-XX:NewRatio=2         # 老年代/新生代 = 2（即新生代占 1/3）
-XX:SurvivorRatio=8    # Eden/Survivor = 8（Eden:S0:S1 = 8:1:1）

# ========== 元空间相关 ==========
-XX:MetaspaceSize=256m         # 元空间初始大小
-XX:MaxMetaspaceSize=512m      # 元空间最大大小

# ========== GC 日志 ==========
-Xlog:gc*:file=gc.log:time,uptime,level,tags  # JDK 9+

# ========== GC 选择 ==========
# JDK 8
-XX:+UseSerialGC        # Serial + Serial Old（单线程，客户端用）
-XX:+UseParallelGC      # Parallel Scavenge + Parallel Old（吞吐量优先）
-XX:+UseConcMarkSweepGC # ParNew + CMS（低延迟）
-XX:+UseG1GC            # G1（JDK 9+ 默认）

# ========== GC 调优 ==========
-XX:MaxTenuringThreshold=15     # 对象晋升老年代年龄阈值
-XX:PretenureSizeThreshold=3m   # 大对象直接进老年代
-XX:+PrintGCDetails             # 打印 GC 详情（JDK 8）

# ========== OOM 诊断 ==========
-XX:+HeapDumpOnOutOfMemoryError   # OOM 时自动 dump 堆快照
-XX:HeapDumpPath=/tmp/heap.hprof  # dump 文件路径
```

### 29. 遇到 GC 问题怎么分析和解决？

```text
典型 GC 问题：
  1. Full GC 频繁 → 老年代配置太小 / 大对象分配过快 / 内存泄漏
  2. Minor GC 频繁 → 新生代太小 / 大量短命对象
  3. STW 太长   → 选择合适的 GC 收集器 / 减少堆大小

分析步骤：
  1. 看 GC 日志（频率、耗时、回收量）
     jstat -gcutil <pid> 1000    # 每秒输出 GC 状态

  2. YGC 每分钟 50 次以上 → 新生代太小，调大 -Xmn

  3. Full GC 每小时几次 → 可以接受
     Full GC 几分钟一次 → 有问题！
     → 看每次 Full GC 的回收量，如果每次回收量很少 → 可能是内存泄漏
     
  4. dump 堆快照分析：
     jmap -dump:live,format=b,file=heap.hprof <pid>
     用 MAT / JProfiler / VisualVM 分析
     看哪些对象占内存最多（Retained Heap）
     看 GC Roots 到这些对象的引用链

  5. 常见内存泄漏场景：
     - ThreadLocal 没 remove
     - 静态集合持续 add
     - IO 流/连接没关
     - 监听器/回调没移除
```

### 30. 如何降低 Full GC 的频率？

```text
1. 避免调用 System.gc()
   要么在 JVM 参数里禁掉：-XX:+DisableExplicitGC

2. 调大老年代空间
   -Xmx 设大一点，让老年代不那么容易满

3. 减少大对象直接进老年代
   对象尽量用完就释放，不要长期持有

4. 让对象尽量在 Minor GC 中被回收
   不要过早晋升（调大 MaxTenuringThreshold）

5. 增加 Survivor 空间
   -XX:SurvivorRatio 调小（比如 6:1:1 而不是 8:1:1）

6. 选择 G1 GC
   G1 的 Mixed GC 在老年代满之前就会开始回收

7. 避免代码里的内存泄漏
   - 集合用完了 clear()
   - ThreadLocal 用完了 remove()
   - 缓存加过期策略
```

### 31. 项目中实践过 JVM 调优吗？怎么做的？

```text
调优流程（不是瞎改参数！）：

1. 明确目标（延迟？吞吐量？内存占用？）
   - 接口响应时间 < 100ms → 追求低延迟 → G1/ZGC
   - 批处理任务 → 追求吞吐量 → Parallel GC
   - 内存受限的微服务 → 控制堆大小

2. 收集基线数据
   启动时打印 GC 日志，运行压测采集数据

3. 分析问题
   - Full GC 频繁 → 堆大小/对象生命周期
   - Minor GC 太慢 → 新生代太大（存活对象多，复制成本高）
   - STW 太长 → GC 收集器选择

4. 一次只改一个参数
   改完 → 压测 → 对比 → 确定效果 → 继续下一个

5. 记录所有变更
   改了什么参数，为什么改，效果如何

常见调优案例：
  - Web 应用 Full GC 频繁 → 分析发现是静态 Map 导致内存泄漏 → 修复代码
  - RPC 服务 RT 抖动 → G1 换到 ZGC → P99 稳定在 50ms 以内
  - 批处理 OOM → -Xmx 从 2G 提到 4G → 解决
```

### 32. 线上 CPU 飙升怎么排查？

```text
排查步骤（按顺序来）：

1. 找到 CPU 最高的进程
   top -c
   记下 PID

2. 找到该进程中 CPU 最高的线程
   top -Hp <PID>
   记下 TID（线程 ID，十进制）

3. 把 TID 转成十六进制
   printf "%x\n" <TID>
   记下 16 进制值

4. 查看线程堆栈
   jstack <PID> | grep -A 30 <16进制TID>
   找到线程正在执行什么代码

5. 常见 CPU 飙升原因：
   - 死循环（while(true){}）
   - Full GC 频繁（查看 GC 日志确认）
   - 大量正则匹配/JSON 解析
   - HashMap 死循环（JDK 7 resize 的 bug）
   - 线程死锁 + 自旋等待

6. 进阶工具：
   arthas dashboard          # 实时看 CPU/内存/GC
   arthas thread -n 3        # 看 CPU 最高的 3 个线程
   arthas thread -b          # 找出有死锁的线程
   jprofile GUI 工具          # 图形化分析
```

---

> **总结**：JVM 知识环环相扣 — 从内存布局到 GC 算法，从类加载到线上调优，每一个环节都是面试的高频考点。建议结合代码和工具动手实践，理解更深刻。
