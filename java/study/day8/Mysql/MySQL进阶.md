# MySQL 进阶 —— 通熟易懂的核心知识

---

## 一、MySQL 存储引擎

### 1. MySQL 支持哪些存储引擎？默认使用哪个？

```sql
-- 查看支持的引擎
SHOW ENGINES;

-- 常用引擎一览
-- InnoDB       ✅ 默认（MySQL 5.5+）  支持事务、行锁、外键
-- MyISAM       老牌引擎               表锁、不支持事务、查询快
-- Memory       数据全在内存           重启就没了，适合临时表
-- Archive      高压缩比归档            只支持 INSERT/SELECT，不支持修改
-- CSV          数据存为 CSV 文件       可直接用 Excel 打开
-- Federated    访问远程 MySQL 表       类似"代理表"
```

```text
MySQL 5.5 之前默认 MyISAM
MySQL 5.5 及以后默认 InnoDB（因为支持事务和行锁，更安全）
```

### 2. MyISAM 和 InnoDB 有什么区别？

用一个生活例子理解：

```text
MyISAM  = 图书馆的登记本：查得快，但不能同时借同一本书（表锁）
InnoDB  = 网上书城：支持并发下单（行锁），下单失败还能退款（事务回滚）
```

| 对比项 | MyISAM | InnoDB |
|--------|--------|--------|
| 事务 | 不支持 | 支持（ACID） |
| 锁粒度 | 表锁 | 行锁 + 间隙锁 |
| 外键 | 不支持 | 支持 |
| 崩溃恢复 | 不安全，易丢数据 | 安全（redo log） |
| 存储结构 | 三个文件：.frm .MYD .MYI | 一个 .ibd（或共享表空间） |
| 索引结构 | B+树，叶子存数据地址 | B+树，聚集索引叶子直接存数据 |
| 计数 `count(*)` | 快（存了总行数） | 慢（需要扫索引） |
| 全文索引 | 支持（5.6 前） | 5.6+ 也支持 |
| 适用场景 | 读多写少，日志/报表 | 高并发写入，需要事务 |

---

## 二、MySQL 事务

### 3. 事务的四大特性 (ACID)

```text
A - Atomicity   原子性：要么全做，要么全不做
     类比：转账 — A 扣钱 + B 加钱 是一体的，不能只扣不加

C - Consistency  一致性：事务前后数据状态合法
     类比：天平两端 — 转账前后总金额不变

I - Isolation    隔离性：并发事务互不干扰
     类比：两个人在 ATM 同时取钱，互不影响

D - Durability   持久性：提交后的数据永久保存
     类比：签完合同盖了章——不能反悔，停电也不丢
```

```sql
-- 示例：转账事务
START TRANSACTION;
UPDATE account SET balance = balance - 100 WHERE id = 1;  -- A 扣 100
UPDATE account SET balance = balance + 100 WHERE id = 2;  -- B 加 100
COMMIT;  -- 提交，持久化
-- 如果中间停电，MySQL 重启后 undo log 把未完成的事务回滚
```

### 4. 并发事务带来了哪些问题？

```text
问题 1：脏读（Dirty Read）
  A 事务改了一行还没提交，B 事务读到了被修改的值
  万一 A 回滚了，B 读到的就是脏数据

  时间线：
  事务A:  UPDATE age=20  (未提交)
  事务B:  SELECT age=20   ← 脏读！读了没提交的数据
  事务A:  ROLLBACK

问题 2：不可重复读（Non-repeatable Read）
  A 事务内两次读同一行，值不一样（B 事务在中间 UPDATE 并提交了）

  时间线：
  事务A:  SELECT age=18
  事务B:  UPDATE age=20 → COMMIT
  事务A:  SELECT age=20   ← 和第一次不一样！

问题 3：幻读（Phantom Read）
  A 事务内两次查同一个范围内的行数不一样（B 在中间 INSERT 了新行并提交了）
```

**不可重复读 vs 幻读的区别：**

```text
不可重复读 = 同一行数据被"改"了（UPDATE）
          → 针对已存在的行，值变了

幻读      = 凭空"多"出了几行（INSERT）
          → 范围查询时多出了几行"幻影"
```

```sql
-- 不可重复读示例
-- 事务A                      事务B
SELECT * FROM t WHERE id=1;  -- age=18
                              UPDATE t SET age=20 WHERE id=1;
                              COMMIT;
SELECT * FROM t WHERE id=1;  -- age=20 ← 同一行值变了

-- 幻读示例
-- 事务A                      事务B
SELECT COUNT(*) WHERE age>18;  -- 3行
                                INSERT INTO t VALUES(9, 20);
                                COMMIT;
SELECT COUNT(*) WHERE age>18;  -- 4行 ← 多了一行！
```

### 5. MySQL 事务隔离级别？默认是什么级别？

| 隔离级别 | 脏读 | 不可重复读 | 幻读 | 实现方式 |
|---------|------|----------|------|---------|
| READ UNCOMMITTED | 会 | 会 | 会 | 几乎无锁，直接读最新值 |
| READ COMMITTED | 不会 | 会 | 会 | MVCC（每次读新 ReadView） |
| **REPEATABLE READ**（默认） | 不会 | 不会 | 基本不会 | MVCC（事务开始创建 ReadView）+ Next-Key Lock |
| SERIALIZABLE | 不会 | 不会 | 不会 | 所有读加共享锁，串行执行 |

```sql
-- 查看当前隔离级别
SHOW VARIABLES LIKE 'transaction_isolation';  -- MySQL 8
SELECT @@transaction_isolation;               -- MySQL 8

-- 设置隔离级别
SET SESSION TRANSACTION ISOLATION LEVEL READ COMMITTED;
```

### 6. MySQL 的隔离级别是基于锁实现的吗？

```text
不是！MySQL InnoDB 的隔离级别是：
  MVCC（多版本并发控制）+ 锁 共同实现的

具体分工：
  READ COMMITTED  ──→  靠 MVCC（每个语句生成新 ReadView）
  REPEATABLE READ ──→  靠 MVCC（事务开始生成 ReadView，保证可重复读）
                    +  Next-Key Lock 解决幻读

  SERIALIZABLE    ──→  纯锁（所有读加 S 锁）
```

> **结论**：隔离主要靠 MVCC，"读"不加锁；写还是加锁的。只有 SERIALIZABLE 才完全靠锁。

### 7. InnoDB 对 MVCC 的具体实现

```text
MVCC 核心三要素 + 一个判定规则：

要素 1：隐藏列
  每行数据隐含 3 个列：
  - DB_TRX_ID  最近修改这行的事务 ID
  - DB_ROLL_PTR  回滚指针（指向 undo log 里的旧版本）
  - DB_ROW_ID    行 ID（没有主键时自动生成）

要素 2：Undo Log 版本链
  每次修改 → 旧版本写入 undo log → 形成版本链

  版本链（从新到旧）：
  [版本3: trx=103, age=30] → [版本2: trx=102, age=20] → [版本1: trx=101, age=18]

要素 3：ReadView（快照）
  事务开始时拍一张"照片"，记录：
  - m_ids:    当前活跃的（没提交的）事务 ID 列表
  - min_trx_id: 活跃事务中最小的 ID
  - max_trx_id: 下一个将要分配的事务 ID
  - creator_trx_id: 创建这个 ReadView 的事务 ID

判定规则（读哪个版本？）：
  遍历版本链，对每个版本：
    if (trx_id == creator_trx_id)        → 自己改的，就读这个版本（可见）
    if (trx_id <  min_trx_id)            → 已提交的事务，可见
    if (trx_id >= max_trx_id)            → 未来的事务，不可见
    if (min_trx_id <= trx_id < max_trx_id)
        if (trx_id 在 m_ids 中)          → 未提交，不可见
        else                             → 已提交，可见

  不可见 → 顺着 DB_ROLL_PTR 找下一个版本继续判断
```

```text
READ COMMITTED vs REPEATABLE READ 的 ReadView 区别：

  READ COMMITTED：
    每次 SELECT 都生成一个新的 ReadView
    → 每次都能读到已提交的最新版本
    → 所以会有"不可重复读"

  REPEATABLE READ：
    事务开始后第一次 SELECT 时生成 ReadView
    之后一直用同一个 ReadView
    → 整个事务期间读到的数据都一样
    → 实现了"可重复读"
```

---

## 三、MySQL 字段类型

### 8. char 和 varchar 的区别是什么？

```sql
CREATE TABLE user (
    name_char CHAR(10),     -- 固定 10 字符，占 10*编码字节
    name_vc   VARCHAR(10)   -- 最多 10 字符，按实际长度+1~2字节存储
);
```

```text
类比：
  CHAR(10)  = 酒店房间：预订了 10 间，只住 3 人也付 10 间的钱
  VARCHAR(10) = 出租车：最多坐 10 人，来几个付几个的钱

区别：
  CHAR：定长，空间换时间，存取快，适合身份证号、MD5 值等固定长度
  VARCHAR：变长，省空间，存取稍慢（要额外记录长度），适合姓名、地址等
```

| 对比项 | CHAR | VARCHAR |
|--------|------|---------|
| 存储 | 定长 | 变长 + 1~2字节存长度 |
| 最大长度 | 255 字符 | 65535 字节（实际约 65532） |
| 尾部空格 | 自动删除 | 保留 |
| 查询速度 | 略快 | 略慢（多一层偏移） |
| 空间 | 浪费 | 节省 |

### 9. varchar(100) 和 varchar(10) 的区别是什么？

```text
区别只有一点：最大能存多少个字符。

varchar(10) = 最多 10 个字符（中文 / 英文 / emoji 按编码算字节）
varchar(100)= 最多 100 个字符

存储空间：两者存 "hello"（5字节）都只占 6 字节（5 + 1长度标记）。
因为 VARCHAR 是"用多少占多少"，(10) 和 (100) 只是上限不同。

注意：MySQL 5.6+ 对索引长度有限制（默认 767 字节 = 255 字符 * 3 UTF8）
如果 VARCHAR(255) 以上建索引，可能导致索引列过长。
```

### 10. decimal 和 float/double 的区别？存钱用哪个？

```sql
CREATE TABLE product (
    price_float  FLOAT,           -- ❌ 存钱别用这个
    price_double DOUBLE,          -- ❌ 这个也不行
    price_decimal DECIMAL(10,2)   -- ✅ 存钱专用
);

-- 精度问题演示
SELECT 0.1 + 0.2;          -- 结果：0.30000000000000004  ← 浮点数有精度问题
SELECT CAST('0.1' AS DECIMAL(10,2)) + CAST('0.2' AS DECIMAL(10,2));  -- 0.30 ✅
```

```text
类比：
  FLOAT/DOUBLE = 小孩用尺子量身高 —— 大概 1.2 米，模糊
  DECIMAL      = 卷尺精确到毫米 —— 1.2345 米，精确

浮点数（IEEE 754）在二进制中无法精确表示 0.1 这样的十进制小数。
就像 1/3 在十进制里只能用 0.3333... 无限循环表示一样。

DECIMAL 内部用字符串方式存储十进制数，无精度损失。
存钱、财务计算，必须用 DECIMAL(M,D)。
  M = 总位数，D = 小数位数
  DECIMAL(10,2) = 最多 8 位整数 + 2 位小数
```

### 11. 为什么不推荐使用 TEXT 和 BLOB？

```text
TEXT 就是超长 VARCHAR，BLOB 是二进制大对象。

不推荐的原因：
  1. 不能有默认值
  2. 排序只对前 max_sort_length 字节排序（默认 1024）
  3. 查询会产生临时表到磁盘（而非内存临时表），很慢
  4. 没办法建全文索引（InnoDB 5.6+ 可以建，但有限制）
  5. 内存占用大，容易撑爆 Buffer Pool
  6. 网络传输慢

最佳实践：
  大文本/文件 → 存到文件系统/OSS，数据库里只存文件路径
  实在要存 → 拆到独立的表中，按需查询（不要混在业务主表中）
```

---

## 四、MySQL 索引

### 12. 为什么索引能提高查询速度？

```text
没有索引（全表扫描） = 在一本没目录的书中找"榴莲"这个词
                      → 必须从头翻到尾

有索引    = 有目录的书
            → 先查目录 → 翻到对应页码 → 找到内容
```

```sql
-- 无索引：全表扫描，遍历所有页
SELECT * FROM user WHERE name = '张三';
-- 10万条 → 10万个磁盘 IO → 慢

-- 有索引：B+树定位 + 一次回表（或覆盖索引不回表）
CREATE INDEX idx_name ON user(name);
SELECT * FROM user WHERE name = '张三';
-- B+树高度 2~3 层 → 2~3 次磁盘 IO → 快 1000 倍
```

### 13. 聚集索引和非聚集索引的区别？非聚集索引一定回表吗？

```text
聚集索引（主键索引）：
  B+树的叶子节点直接存完整行数据
  一张表只有一个聚集索引

非聚集索引（二级索引/辅助索引）：
  B+树的叶子节点存主键值（而非完整数据）
  一张表可以有多个
```

```text
         聚集索引（主键）               非聚集索引（name）
       ┌─────────────────┐         ┌─────────────────┐
       │  根: id 1~100   │         │ 根: A~Z         │
       └────────┬────────┘         └────────┬────────┘
         ┌──────┴──────┐              ┌──────┴──────┐
    ┌────┴────┐  ┌─────┴────┐   ┌────┴────┐  ┌─────┴────┐
    │ id<50  │  │ id>=50   │   │ A~M     │  │ N~Z      │
    └───┬────┘  └────┬─────┘   └───┬─────┘  └────┬─────┘
  叶子: id=1,整行数据  叶子: id=50,整行数据   叶子: name=张三,id=1  叶子: name=李四,id=3
                                                          │
                                                    回表 → 用 id=1 去聚集索引找完整数据
```

```text
回表：
  通过非聚集索引查到主键，再用主键去聚集索引查完整行 → 两次 B+树查找

覆盖索引就不会回表：
  SELECT id FROM user WHERE name = '张三';
  → id 在非聚集索引的叶子节点已经有了，不用回表

  SELECT id, name, age FROM user WHERE name = '张三';
  → age 不在非聚集索引中 → 必须回表

  解决：建立联合索引 (name, age)，这样叶子节点就包含 age 了
  → 覆盖索引，不回表 ✅
```

### 14. 为什么不给每列都建索引？

```text
1. 索引占空间（每个索引就是一棵 B+树）
   10列 = 10棵B+树 = 10倍空间

2. 写操作变慢
   INSERT/UPDATE/DELETE 时，不仅要改数据，还要维护所有索引
   10个索引 = 10棵B+树都要调整

3. 优化器可能选错索引
   索引太多，优化器计算执行计划时可能选错
   选了差索引反而比全表扫描还慢

4. 索引不一定生效
   WHERE name LIKE '%张'  → 左模糊，不走索引
   WHERE age/2 = 10       → 索引列有函数，不走索引
```

### 15. 索引底层数据结构？Hash 索引 vs B+树索引

```text
Hash 索引（Memory 引擎支持，InnoDB 的"自适应哈希"）
  原理：key 求 hash → 找到 value
  优点：单条查询 O(1)，极快
  缺点：
    - 不支持范围查询（BETWEEN, >, <）
    - 不支持排序（ORDER BY）
    - 不支持模糊查询（LIKE '张%'）
    - 哈希冲突多了性能退化

B+树索引（MySQL 主力索引结构）
  优点：
    - 支持范围查询（叶子节点有序+链表连接）
    - 支持排序和分组
    - 支持最左前缀
    - 树的高度稳定（3~4层），查询效率稳定 O(log N)
```

**B+树长什么样：**

```text
                       ┌──────────────┐
   非叶子节点           │  20  40  60  │  ← 只存储索引键，不存数据
  （仅做路由）          └─┬──┬──┬───┬─┘
                   ┌──────┘  │  │   └───────┐
              ┌────┴───┐   ┌─┴──┴──┐   ┌────┴───┐
              │ 5 11 17│   │25 35  │   │45 55   │
              └──┬──┬──┘   └──┬──┬─┘   └──┬──┬──┘
   叶子节点       │  │         │  │        │  │
  （存全部数据）  ├──┼─────────┼──┼────────┼──┼───── 双向链表连接（范围查询利器！）
                 5  11...    25  35...   45  55...
                 ←————————————————————————————————→
```

### 16. B+树做索引比红黑树好在哪里？

```text
关键区别：磁盘友好程度完全不同！

红黑树 = 二叉，高度高
  1000万条数据 → 高度约 25 层
  → 查一条可能读 25 个节点 → 25 次磁盘 IO
  → 每次 IO 只读一个键 → 浪费

B+树 = 多叉，矮胖
  一个节点 = 一个 Page (16KB)，能存约 1000 个键
  2000万条数据 → 高度 3 层
  → 查一条只需 3 次磁盘 IO
  → 每次 IO 读整个 Page 到内存

总结：
  红黑树：内存中用没问题，磁盘上 IO 次数太多
  B+树： 专为磁盘设计，一次 IO 读一整个 Page，减少 IO 次数
```

### 17. 最左前缀匹配原则

```sql
-- 假设联合索引 (a, b, c)
CREATE INDEX idx_abc ON t(a, b, c);

-- ✅ 走索引（从 a 开始，依次匹配）
SELECT * FROM t WHERE a = 1;                          -- 用 a
SELECT * FROM t WHERE a = 1 AND b = 2;                -- 用 a,b
SELECT * FROM t WHERE a = 1 AND b = 2 AND c = 3;      -- 用 a,b,c
SELECT * FROM t WHERE a = 1 AND c = 3;                -- 用 a（c 断了，只能匹配到 a）

-- ✅ 范围查询后面断掉
SELECT * FROM t WHERE a = 1 AND b > 2 AND c = 3;      -- a,b 走索引，c 不走（b 是范围，断了）

-- ❌ 不走索引（没有从 a 开始）
SELECT * FROM t WHERE b = 2;                          -- 跳过了 a
SELECT * FROM t WHERE c = 3;                          -- 跳过了 a,b
SELECT * FROM t WHERE b = 2 AND c = 3;                -- 跳过了 a
```

```text
类比：字典的"拼音+部首"索引
  你必须先查拼音，才能用部首
  跳过拼音直接查部首 → 查不到（全表扫描）
```

### 18. 什么是覆盖索引？

```sql
-- 表结构
CREATE TABLE user (
    id INT PRIMARY KEY,
    name VARCHAR(50),
    age INT,
    INDEX idx_name_age (name, age)
);

-- 普通查询（需要回表）
EXPLAIN SELECT * FROM user WHERE name = '张三';
-- Extra: (无特殊标记) ← 需要回表查 id 对应的整行

-- 覆盖索引（不用回表）
EXPLAIN SELECT name, age FROM user WHERE name = '张三';
-- Extra: Using index  ← 所需数据全在 idx_name_age 的叶子节点里，
--                         不需要再去聚集索引查完整行！
```

```text
覆盖索引 = 查询需要的列全被索引覆盖，不用回表
    好处：少一次 B+树查找，减少 IO，查询更快

常见用法：利用联合索引覆盖更多字段
    SELECT id FROM t WHERE name=?      → 覆盖（所有二级索引叶子都带主键）
    SELECT id, name FROM t WHERE name=? → name 在索引中，覆盖
    SELECT id, name, age FROM t WHERE name=? → 联合索引 (name, age) → 覆盖
```

### 19. 如何查看 SQL 是否用到了索引？

```sql
-- 最常用的方式：EXPLAIN
EXPLAIN SELECT * FROM user WHERE name = '张三';

-- 关键字段解读：
-- type       访问类型：const > eq_ref > ref > range > index > ALL（全表扫描最差）
-- key        实际用的索引（NULL 表示没用索引）
-- rows       预估扫描行数
-- Extra      Using index（覆盖索引，好）
--            Using where（用 WHERE 过滤，正常）
--            Using filesort（用文件排序，差，需优化）
--            Using temporary（用临时表，很差，需优化）

-- 示例输出：
+----+------+----------+------+------+-------------+
| id | type | key      | rows | Extra              |
+----+------+----------+------+------+-------------+
|  1 | ref  | idx_name | 1    | Using index        |  ← 用到索引了，非常好
+----+------+----------+------+------+-------------+

-- MySQL 8 还可以用 EXPLAIN ANALYZE（实际执行+耗时）
EXPLAIN ANALYZE SELECT * FROM user WHERE name = '张三';
-- -> Index lookup on user using idx_name (name='张三')  (cost=0.35 rows=1)
--   (actual time=0.024..0.025 rows=1 loops=1)
```

---

## 五、MySQL 锁

### 20. 表级锁和行级锁有什么区别？

| 对比项 | 表级锁 | 行级锁 |
|--------|--------|--------|
| 锁定粒度 | 整张表 | 一行或几行 |
| 并发度 | 低（一个写锁阻塞所有写） | 高（不同行可并发写） |
| 加锁开销 | 小 | 大（要看记录是否冲突） |
| 死锁概率 | 低 | 高（多行锁可能互相等待） |
| 引擎支持 | MyISAM、Memory | InnoDB |
| 场景 | 全表更新、DDL | 高并发写入 |

### 21. 哪些操作会加表级锁？哪些会加行级锁？

```sql
-- 表级锁
LOCK TABLES user READ;   -- 读锁，别人可读不可写
LOCK TABLES user WRITE;  -- 写锁，别人不可读不可写
UNLOCK TABLES;

ALTER TABLE user ADD COLUMN phone VARCHAR(20);  -- DDL 加表锁（或 MDL 锁）
-- MySQL 5.6+ Online DDL 部分场景不锁表

-- MyISAM 引擎的 SELECT 自动加读锁，INSERT/UPDATE/DELETE 自动加写锁

-- =============================================

-- InnoDB 行级锁（自动加）
UPDATE user SET age = 20 WHERE id = 1;   -- 只锁 id=1 这一行
UPDATE user SET age = 20 WHERE id = 1 AND id = 2; -- 锁两行

-- InnoDB 加行锁的前提：WHERE 条件走了索引
-- 如果没走索引 → 退化为表锁！

UPDATE user SET age = 20 WHERE name = '张三';
-- 如果 name 没索引 → 锁全表（因为不知道哪些行满足条件，只能全锁）
```

### 22. InnoDB 有哪几类行锁？

```text
1. Record Lock（记录锁）
   锁住索引中的一条记录
   SELECT * FROM t WHERE id = 5 FOR UPDATE;  ← 锁 id=5 这一条

2. Gap Lock（间隙锁）
   锁住一个范围，但不包括记录本身
   防止 INSERT 新数据
   SELECT * FROM t WHERE id BETWEEN 10 AND 20 FOR UPDATE;
   ← 锁住 10~20 这个区间（不包括 10 和 20 两条记录）

3. Next-Key Lock（临键锁）
   = Record Lock + Gap Lock
   锁住记录 + 记录前面的间隙
   是 InnoDB REPEATABLE READ 下防止幻读的关键武器
```

### 23. Next-Key Lock 的加锁范围？

```sql
-- 假设索引有值：5, 10, 15, 20
-- 加锁后形成的区间：
--  (-∞, 5]  (5, 10]  (10, 15]  (15, 20]  (20, +∞)
--   ↑                    ↑
--   每个 ( ] 是一个 Next-Key Lock（左开右闭）

-- 示例：
SELECT * FROM t WHERE id = 10 FOR UPDATE;
-- 加锁范围：(5, 10]   ← 锁 id=10 + (5,10)这个间隙

SELECT * FROM t WHERE id = 13 FOR UPDATE;
-- id=13 不存在，锁住它所在的间隙：(10, 15)

SELECT * FROM t WHERE id > 10 AND id <= 15 FOR UPDATE;
-- 锁定范围：(5, 10] U (10, 15] U (15, 20]
```

```text
等值查询唯一索引，且记录存在 → 退化为 Record Lock
等值查询唯一索引，记录不存在 → 退化为 Gap Lock
等值查询非唯一索引 → Next-Key Lock（可能有多个）
范围查询 → Next-Key Lock
```

### 24. 当前读和快照读有什么区别？

```sql
-- 快照读（Snapshot Read）→ 读 MVCC 版本链，不加锁
SELECT * FROM user WHERE id = 1;  -- 普通 SELECT，不加锁
-- 读了 ReadView 对应的历史版本，不阻塞别人写

-- 当前读（Current Read）→ 读最新提交版本，加锁
SELECT * FROM user WHERE id = 1 FOR UPDATE;      -- 加 X 锁（当前读）
SELECT * FROM user WHERE id = 1 LOCK IN SHARE MODE; -- 加 S 锁（当前读）
-- 读最新数据，阻塞别人修改

-- UPDATE / DELETE / INSERT → 都是当前读（必然要基于最新数据修改）
```

| 对比 | 快照读 | 当前读 |
|------|--------|--------|
| 读什么 | 历史版本（MVCC） | 最新提交版本 |
| 加锁 | 不加锁 | 加 S 锁 / X 锁 |
| 阻塞 | 不阻塞 | 可能阻塞或被阻塞 |
| 语句 | `SELECT`（普通） | `SELECT ... FOR UPDATE`、`UPDATE`、`DELETE` |

### 25. MySQL 如何使用乐观锁和悲观锁？

```sql
-- 悲观锁：先锁再操作（认为冲突概率高）
-- 适用于写多、冲突多的场景
START TRANSACTION;
SELECT stock FROM product WHERE id = 1 FOR UPDATE;  -- 锁住这行
-- 检查库存...
UPDATE product SET stock = stock - 1 WHERE id = 1;
COMMIT;

-- 乐观锁：先操作再检查（认为冲突概率低）
-- 适用于读多、冲突少的场景，通常用版本号或时间戳
UPDATE product
SET stock = stock - 1, version = version + 1
WHERE id = 1 AND version = 5;  -- 只有版本号匹配才更新

-- 如果 affected_rows = 0，说明别人已经改过了 → 重试或报错
```

```text
乐观锁 = 相信世界美好，没人跟你抢 → 最后检查一下有没有人改过
悲观锁 = 总觉得有人要抢 → 先锁住再操作

乐观锁 + 版本号 不是数据库层面的锁，是应用层的 CAS 思想
悲观锁（FOR UPDATE）是数据库层面的行锁
```

---

## 六、MySQL 日志

### 26. MySQL 中常见的日志有哪些？

| 日志 | 作用 | 引擎 | 说明 |
|------|------|------|------|
| **redo log** | 重做日志，保证持久性 | InnoDB | 物理日志，记录"数据页做了什么修改" |
| **undo log** | 回滚日志，保证原子性 + MVCC | InnoDB | 逻辑日志，记录"修改前的数据" |
| **binlog** | 归档日志，主从复制 | Server 层 | 逻辑日志，记录"SQL 语句或行变更" |
| **slow query log** | 慢查询日志 | Server 层 | 记录执行时间超过阈值的 SQL |
| **error log** | 错误日志 | Server 层 | 记录启动、关闭、运行中的错误 |
| **general log** | 通用日志 | Server 层 | 记录所有连接和 SQL（默认关闭） |
| **relay log** | 中继日志 | 从库 | 从库接收主库 binlog 后的临时存储 |

### 27. 慢查询日志有什么用？

```sql
-- 开启慢查询
SET GLOBAL slow_query_log = ON;
SET GLOBAL long_query_time = 2;  -- 超过 2 秒的 SQL 会被记录
SET GLOBAL log_queries_not_using_indexes = ON;  -- 也记录没走索引的 SQL

-- 查看慢查询日志位置
SHOW VARIABLES LIKE 'slow_query_log_file';

-- 用 mysqldumpslow 分析
-- mysqldumpslow -s t -t 10 slow.log  # 按时间排，取前 10
```

```text
作用：
  1. 找出执行慢的 SQL → 针对性优化
  2. 发现没走索引的查询
  3. 监控数据库性能趋势

慢查询不等于坏查询 — 有些复杂报表就是会慢
关键是看能不能优化（加索引、改写 SQL、分表等）
```

### 28. binlog 主要记录了什么？

```sql
-- binlog 三种格式：
-- STATEMENT：记录 SQL 语句本身
-- ROW：      记录每行数据的变更（MySQL 5.7 后默认）
-- MIXED：    混合模式，大多数用 STATEMENT，特殊用 ROW

SHOW BINARY LOGS;
SHOW BINLOG EVENTS IN 'binlog.000001' LIMIT 10;
```

```text
binlog 是 Server 层的逻辑日志，记录的是：
  - 表结构变更（ALTER TABLE）
  - 数据变更（INSERT、UPDATE、DELETE）
  NOT 记录单纯的 SELECT

主要用途：
  1. 主从复制：主库的 binlog 传给从库，从库重放实现数据同步
  2. 数据恢复：基于时间点恢复数据（PITR）

binlog 特点：
  - 逻辑日志（记录 SQL 或行变化，不是物理页变化）
  - 追加写，一个文件满了切下一个
  - 事务提交后才写入
```

### 29. redo log 如何保证事务的持久性？

```text
redo log 是 InnoDB 的"保险单"——

场景：你修改了一行数据
  1. 先将修改记录到 redo log buffer（内存）
  2. 事务提交时，redo log buffer 刷到磁盘（redo log file）
  3. 这时候数据库宕机了（数据页还没刷盘怎么办？）
  4. 重启后，InnoDB 读取 redo log，把没来得及刷盘的数据恢复过来

这就是 WAL 技术（Write-Ahead Logging）：先写日志，再写数据。

参数：
  innodb_flush_log_at_trx_commit = 1  ← 每次提交都刷盘，最安全
  innodb_flush_log_at_trx_commit = 2  ← 每秒刷一次，宕机可能丢 1 秒数据
  innodb_flush_log_at_trx_commit = 0  ← 每秒刷一次，但由 OS 控制
```

### 30. 页修改之后为什么不直接刷盘呢？

```text
核心原因：随机写 vs 顺序写 的性能差距。

直接刷数据页（随机写）：
  修改了 id=1, id=100, id=8888 三行 → 三次随机 IO
  → 磁头来回跳，慢！

先写 redo log（顺序写）：
  只是把修改记录追加到 redo log 文件末尾
  → 追加写 → 不需要磁头跳来跳去 → 快！

类比：
  直接刷盘 = 快递员给 3 个不同小区的住户送快递（满城跑）
  redo log = 快递员只送到小区门卫室（集中存放），门卫再慢慢分发

InnoDB 的做法：
  1. 修改记录写入 redo log（顺序写，快）
  2. 有空了再把"脏页"慢慢刷到磁盘（后台，不影响用户请求）
  3. 脏页刷盘也是尽量合并相邻页一起刷
```

### 31. binlog 和 redo log 有什么区别？

| 对比项 | redo log | binlog |
|--------|----------|--------|
| 层级 | InnoDB 引擎层 | MySQL Server 层 |
| 内容 | 物理日志：数据页的修改 | 逻辑日志：SQL / 行变更 |
| 写入方式 | 循环写（4个文件来回覆盖） | 追加写（满了切新文件） |
| 用途 | 崩溃恢复（crash-safe） | 主从复制 + 数据恢复 |
| 持久化时机 | 事务执行中就写入 | 事务提交后才写入 |

### 32. undo log 如何保证事务的原子性？

```text
undo log = 后悔药

每条修改前，先把旧值记到 undo log 里：

  UPDATE user SET age = 25 WHERE id = 1;
   → 先在 undo log 记下：id=1, age 原来是 20
   → 再把 age 改成 25

  INSERT INTO user VALUES(10, '张三', 30);
   → undo log 记下：新插入的主键 id=10
   → 回滚时：DELETE FROM user WHERE id = 10

  DELETE FROM user WHERE id = 5;
   → undo log 记下：被删除的整行数据
   → 回滚时：INSERT 回去

如果事务回滚（ROLLBACK），按照 undo log 反向操作：
  UPDATE  → 用旧值改回去
  INSERT → DELETE 掉
  DELETE → INSERT 回去

如果数据库崩溃：
  1. 重启后先读 redo log，恢复所有已提交的事务
  2. 再读 undo log，把所有未提交的事务回滚
  3. 保证事务的原子性 ✅
```

```text
undo log 的两大用途：
  1. 事务回滚（保证原子性）
  2. MVCC（版本链依赖 undo log 存的历史版本）

redo log + undo log 组合拳：
  redo log 保证持久性（提交了的数据一定在）
  undo log 保证原子性（没提交的数据一定不在）
  两者一起保证崩溃恢复后的数据正确
```

---

## 附录：一条 UPDATE 语句的执行流程（串联所有日志）

```text
UPDATE user SET age = 25 WHERE id = 1;

1. 执行器：通过索引找到 id=1 的数据页
2. Buffer Pool：如果页不在内存，从磁盘加载到 Buffer Pool
3. 写入 undo log：记录修改前的 age 值（为回滚 + MVCC 做准备）
4. 修改 Buffer Pool 中的页（变成脏页）
5. 写入 redo log buffer：记录 "哪个页的哪个位置改成了什么"
6. 准备提交事务 → redo log buffer 刷到 redo log 文件（prepare 状态）
7. 写入 binlog：记录这条 UPDATE 语句
8. redo log 标记为 commit 状态（两阶段提交完成！）
9. 返回客户端：OK, 1 row affected
10. 后台线程：适时把脏页刷回磁盘（和用户请求无关）

crash-safe 保证：
  重启时 redo log 里 prepare 状态 + binlog 完整 → 提交
  重启时 redo log 里 prepare 状态 + binlog 不完整 → 回滚
```

---

> **总结**：MySQL 知识从存储引擎到事务、从索引到底层数据结构、从锁到日志，环环相扣。建议结合实际建表、写 SQL、看 EXPLAIN 输出来加深理解。
