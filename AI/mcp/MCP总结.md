# MCP（Model Context Protocol）总结文档

## 一、什么是 MCP

MCP（Model Context Protocol，模型上下文协议）是 Anthropic 于 2024 年推出的**开放标准协议**，用于让 AI 模型（尤其是大语言模型 LLM）与外部数据源、工具和服务进行标准化交互。

可以把它理解为 **"AI 应用的 USB-C 接口"**——提供一套统一规范，让 AI 以标准化方式连接各种外部系统，无需为每个数据源单独定制集成代码。

### 要解决的问题

在 MCP 之前，每个 AI 应用接入外部工具（数据库、API、文件系统等）都要单独开发适配代码，形成 **N×M** 的集成难题。MCP 通过统一协议把它变成 **N+M**。

---

## 二、架构组成

MCP 采用客户端-服务器架构：

| 角色 | 说明 |
|------|------|
| **Host（宿主）** | 运行 AI 的应用，如 Claude Desktop、IDE 插件 |
| **Client（客户端）** | 宿主内部与服务器通信的连接器 |
| **Server（服务器）** | 提供具体能力的服务，如文件访问、数据库查询、API 调用 |

**通信方式：**
- **stdio**：本地进程间通信（标准输入输出）
- **HTTP/SSE**：远程网络通信
- 底层基于 **JSON-RPC 2.0** 进行消息传递

---

## 三、三类核心能力

MCP Server 主要向 AI 暴露三类内容：

| 类型 | 说明 | 举例 |
|------|------|------|
| **Tools（工具）** | 可执行的函数 | 查询天气、发送邮件、执行 SQL |
| **Resources（资源）** | 可读取的数据 | 文件内容、数据库记录 |
| **Prompts（提示词）** | 预定义的提示词模板 | 代码审查模板、翻译模板 |

---

## 四、如何暴露给 AI

### 1. 前提：初始化握手（能力协商）

连接建立后，客户端与服务器先做 `initialize` 握手，互相声明支持的能力（capabilities）。只有声明过的能力，客户端后续才会去发现。

```jsonc
// Server 声明自己提供 tools/resources/prompts
{
  "result": {
    "capabilities": {
      "tools":     { "listChanged": true },
      "resources": { "subscribe": true, "listChanged": true },
      "prompts":   { "listChanged": true }
    }
  }
}
```

### 2. 三类内容的"发现 + 使用"方法对

每类都遵循相同模式：**一个 `list` 方法发现，一个方法消费。**

| 类型 | 发现方法 | 使用方法 | 唯一标识 |
|------|---------|---------|---------|
| Tools | `tools/list` | `tools/call` | name |
| Resources | `resources/list` | `resources/read` | URI |
| Prompts | `prompts/list` | `prompts/get` | name |

其中 Tools 的 `inputSchema`（JSON Schema 描述参数）至关重要——它是 AI 理解"工具干什么、需要传什么参数"的依据，会被转成 LLM 的 function calling 格式。

### 3. Function Calling 格式长什么样

Function Calling（函数调用/工具调用）是 LLM 厂商提供的能力，让模型能"声明想调用某个函数并给出参数"。宿主会把 MCP 的 `tools/list` 结果**逐字段映射**成厂商要求的格式，喂给 LLM。

**（1）宿主注入给 LLM 的工具声明**

以 OpenAI 风格为例，MCP 工具的 `name` / `description` / `inputSchema` 直接对应过去：

```jsonc
{
  "tools": [{
    "type": "function",
    "function": {
      "name": "get_weather",              // <- MCP tool 的 name
      "description": "查询指定城市的当前天气。", // <- MCP tool 的 description
      "parameters": {                     // <- MCP tool 的 inputSchema（JSON Schema）
        "type": "object",
        "properties": {
          "city": { "type": "string", "description": "城市名称，例如 北京" }
        },
        "required": ["city"]
      }
    }
  }]
}
```

> 可见 MCP 的 `inputSchema` 与 function calling 的 `parameters` 本质是同一份 JSON Schema，几乎零成本转换。Anthropic（Claude）风格字段名略有不同（`input_schema`、工具平铺在 `tools` 数组里），但结构一致。

**（2）LLM 返回的调用意图**

模型不会自己执行函数，而是返回一个"我要调用哪个函数、参数是什么"的结构化结果：

```jsonc
{
  "tool_calls": [{
    "id": "call_abc123",
    "type": "function",
    "function": {
      "name": "get_weather",
      "arguments": "{\"city\": \"北京\"}"   // 注意：arguments 是 JSON 字符串
    }
  }]
}
```

**（3）宿主执行并回填结果**

宿主解析上面的意图 → 转成 MCP 的 `tools/call` 交给 Server 执行 → 把返回值作为一条 `role: "tool"` 消息附上 `tool_call_id` 回填给 LLM：

```jsonc
{
  "role": "tool",
  "tool_call_id": "call_abc123",
  "content": "晴，25°C，微风"
}
```

LLM 拿到这条结果后，继续生成给用户的自然语言回答。这样就完成了 **MCP tools/call ↔ LLM function calling** 的闭环对接。

### 4. 关键点：谁把内容"告诉"AI？

**MCP Server 本身不直接和 LLM 对话。** 完整流程：

```
1. Host 启动 -> Client 连接 Server -> initialize 握手
2. Client 调用 tools/list、resources/list、prompts/list 收集清单
3. Host 把清单（尤其 tools 的 name/description/schema）
   注入到发给 LLM 的请求里（转成 function calling 格式）
4. LLM 根据用户问题，判断要调用哪个 tool、传什么参数
5. Host 收到调用意图 -> Client 发 tools/call 给 Server
6. Server 执行 -> 返回结果 -> Host 回填给 LLM
7. LLM 基于结果生成最终回答
```

结论：**Server 把内容暴露给 Host/Client，Host 再"翻译"成 LLM 能理解的格式喂给 AI。**

### 5. 动态更新

握手时声明 `listChanged: true` 后，服务器内容变化时可主动发通知：

```jsonc
{ "method": "notifications/tools/list_changed" }
```

客户端收到后重新调用 `list` 刷新清单。

---

## 五、代码示例

配套示例见同目录下的 **`weather_server.py`**，用官方 Python SDK（FastMCP）实现了一个天气查询服务，完整演示三类内容的暴露：

- **Tools**：`get_weather`、`compare_temperature`
- **Resources**：`weather://cities`（静态）、`weather://history/{city}`（参数化模板）
- **Prompts**：`travel_advice`

FastMCP 会自动从函数签名、类型注解、docstring 生成 `list` 所需的元数据。

### 运行方式

```bash
# 安装依赖
pip install "mcp[cli]"

# 开发模式（自带调试面板 MCP Inspector）
mcp dev weather_server.py

# 直接运行（stdio 传输，供宿主连接）
python weather_server.py
```

---

## 六、暴露的到底是什么：函数 vs 协议接口 vs 传输通道

一个常见疑问：`weather_server.py` 暴露出去的是"接口"吗？

**结论：本质上是"接口"，但不是传统 HTTP REST 接口，而是 MCP 协议层的统一"能力端点"。** 要分两层看：

### 第 1 层：你写的 Python 函数 ≠ 直接暴露的接口

`get_weather`、`weather_history` 这些函数本身**不会**被当作能直接 `import` 或直接 HTTP 请求的接口。外部无法直接调用 `get_weather("北京")`。

装饰器 `@mcp.tool()` / `@mcp.resource()` / `@mcp.prompt()` 做的事是：**把函数登记（注册）到 MCP Server 的能力表里**，并自动提取签名、类型、docstring 生成元数据。

### 第 2 层：真正暴露的是协议规定的固定方法

外部（宿主/客户端）看到的是**协议规定的、固定的一组 JSON-RPC 方法**，而非你的函数名：

| 你写的函数 | 通过哪个协议方法被发现 | 通过哪个协议方法被调用 |
|-----------|---------------------|---------------------|
| `get_weather` | `tools/list` | `tools/call` (name=`get_weather`) |
| `weather_history` | `resources/templates/list` | `resources/read` (uri=...) |
| `travel_advice` | `prompts/list` | `prompts/get` |

客户端永远只跟 `tools/call` 这个统一入口打交道，再用参数 `name` 指定调哪个工具。**函数是"被路由的目标"，而非"直接暴露的端点"。**

> 一句话：你注册的是**函数（业务逻辑）**；对外暴露的是**协议标准方法**；这些方法**跑在哪种信道上**由 transport 决定。

---

## 七、MCP 如何与宿主通信：stdio vs SSE

**先记住一句话**：MCP Server 和宿主说的是**同一种"语言"**（JSON-RPC 消息），区别只在于用什么**信道**把话传过去。好比对话内容一样，一个是**面对面说话**（stdio），一个是**打电话**（SSE）。

### 1. stdio 方式：像"面对面递纸条"

宿主把 MCP Server **当子进程拉起来**，借用进程的 stdin/stdout 两根管道当信道。

```
┌─────────────┐                          ┌──────────────┐
│   宿主       │  ─── 写入子进程 stdin ──> │  MCP Server  │
│ (Claude     │      "查北京天气"          │ (weather_    │
│  Desktop)   │  <── 读子进程 stdout ───  │  server.py)  │
│             │      "晴，25°C"           │              │
└─────────────┘                          └──────────────┘
```

- ✅ 简单、快（本地无网络开销）、安全（不开端口）、省心（不用管 IP/端口/鉴权）
- ❌ 只能本地用、一对一（只服务拉起它的那个宿主）
- 代码：`mcp.run()`（当前默认）

### 2. SSE 方式：像"打电话/网络专线"

Server 独立启动、**监听端口**，任何知道地址的宿主都能连。

> SSE = Server-Sent Events，基于 HTTP，特点是**建立一次连接后，服务器可持续主动往客户端推消息**。

```
┌─────────────┐   ① HTTP POST 发请求          ┌──────────────┐
│   宿主       │   ──"查北京天气"──────────>    │  MCP Server  │
│  (客户端)    │   ② SSE 长连接持续收结果        │  监听 :8000   │
│             │   <═══"晴，25°C"═══════════    │  一直在线     │
│             │   <═══"工具列表变了"═══════     │  等连接       │
└─────────────┘                              └──────────────┘
```

- ✅ 能远程、能多客户端共享、Server 可独立部署升级
- ❌ 复杂些（要管端口/URL/鉴权）、有网络开销
- 代码：`mcp.run(transport="sse")`

### 3. 对比表

| 对比项 | stdio（递纸条） | SSE（网络专线） |
|--------|---------------|----------------|
| 信道 | 子进程 stdin/stdout | HTTP + SSE 长连接 |
| Server 启动 | 被宿主当子进程拉起 | 自己独立监听端口 |
| 位置 | 必须本地同机 | 本地或远程都行 |
| 多客户端共享 | 不能（一对一） | 能（一对多） |
| 端口/鉴权 | 不要 | 要 |
| 典型场景 | 本地工具（读写文件、连本地库） | 团队/云端共享服务 |
| 代码写法 | `mcp.run()` | `mcp.run(transport="sse")` |

**选型**：本地个人用、访问本机资源 → stdio；要部署到服务器/多人共享/跨机器 → SSE。

---

## 八、SSE 模式下暴露哪些 HTTP 接口

**关键认知：不是"一个函数一个接口"。** 无论写多少个 tool/resource/prompt，SSE 模式对外只暴露**固定的 2 个 HTTP 端点**：

| HTTP 接口 | 方法 | 作用 |
|-----------|------|------|
| `GET /sse` | GET | 建立 **SSE 长连接**，Server 通过它持续把消息/结果**推**给客户端 |
| `POST /messages/?session_id=xxx` | POST | 客户端**发**请求，所有 JSON-RPC 消息都从这里进去 |

> 新版 Streamable HTTP 模式甚至合并成单个 `/mcp` 端点。

### 5 个函数去哪了？—— 变成"协议方法"，不是"HTTP 接口"

`/sse` 和 `/messages` 是**大楼的两个门**；函数是**楼里的各个房间**。访客统一从大门进来，再拿"门牌号"（method + name）找到具体房间：

| 你写的函数 | 走哪个门 | JSON-RPC `method` | 定位参数 |
|-----------|---------|-------------------|---------|
| `get_weather` | POST /messages | `tools/call` | `name: "get_weather"` |
| `compare_temperature` | POST /messages | `tools/call` | `name: "compare_temperature"` |
| `list_supported_cities` | POST /messages | `resources/read` | `uri: "weather://cities"` |
| `weather_history` | POST /messages | `resources/read` | `uri: "weather://history/北京"` |
| `travel_advice` | POST /messages | `prompts/get` | `name: "travel_advice"` |

### 完整流程（以"查北京天气"为例）

```
① GET /sse                → Server 回 endpoint: /messages/?session_id=abc123
② POST /messages?...      body: {"method":"initialize", ...}
③ POST /messages?...      body: {"method":"tools/list"}   → 结果从 /sse 推回
④ POST /messages?...      body: {"method":"tools/call",
                                 "params":{"name":"get_weather",
                                           "arguments":{"city":"北京"}}}
                          → 结果 "晴，25°C" 从 /sse 长连接推回
```

注意 ③④ 的返回值**不是** POST 的直接响应，而是从 `/sse` 长连接推回来的。

> **记忆点：门是固定的 2 个，房间（函数）有多少都藏在门后，靠"门牌号"路由。** 这正是 MCP 相比 REST 的最大不同——**统一入口 + 协议路由**，而非一功能一 URL。

---

## 九、为什么结果要走 SSE 推回，而不是 POST 直接返回

**核心：MCP 需要一条"服务器能随时主动说话"的通道，而普通 HTTP 的 POST/响应是"你不问我就不能开口"。**

### 普通 HTTP 请求-响应的两个硬伤

1. **服务器无法主动推送**：但 MCP 的 Server 经常要主动开口——进度通知（工具跑得慢时汇报"已完成 50%"）、列表变更通知（`notifications/tools/list_changed`）、采样请求、资源订阅更新。纯 POST 一问一答根本实现不了。
2. **耗时操作会卡住**：若 `tools/call` 用 POST 响应返回，一个 30 秒的工具就得让 HTTP 请求干等 30 秒，易超时。

### SSE 的解法：拆成"两条道"

```
        ┌──── POST /messages ────>  （客户端"发起"，发完即走，不干等）
客户端                                Server
        <──── GET /sse (长连接) ───   （所有回复+主动通知都从这条常驻通道推回）
```

- POST 通道：像**投信的邮筒**，投完就走，不站着等回信
- SSE 长连接：一条**常驻的下行广播通道**，Server 想什么时候说话就什么时候说话

### 为什么不用 WebSocket？（设计权衡）

| 方案 | 服务器主动推 | 缺点 |
|------|:---:|------|
| 纯 POST 响应 | ❌ | 不能主动说话、耗时阻塞 |
| 轮询 | ⚠️ | 浪费资源、有延迟 |
| WebSocket | ✅ | 协议重、要握手升级、代理/防火墙支持差、全双工对 MCP 过度设计 |
| **SSE** | ✅ | 只能单向下行（配合 POST 上行刚好够用）|

SSE 的甜点：**就是普通 HTTP**（穿透代理/防火墙/CDN 无压力）、自带断线重连、实现简单。MCP 通信本就"下行需求远多于上行"，SSE 的单向下行推送刚好契合，WebSocket 的全双工反而多余。

> **类比**：POST 像打电话下单（说完挂机不干等），SSE 像家里装了店家专用对讲机（一直开着，店家随时能喊"已接单/还有5分钟/菜好了"）。没有对讲机就只能一直握着听筒干等，且店家有新消息也没法主动告诉你。

---

## 十、实际意义

有了 MCP，AI 不再局限于"聊天"，而能真正"做事"——读取文件、查询数据库、调用第三方 API、操作开发工具等。整个生态因统一协议而可复用、可组合。
