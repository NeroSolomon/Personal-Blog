# 如何开发一个 AI Agent

## 一、理解 AI Agent 的核心概念

AI Agent（智能体）是一个能够**感知环境、自主决策、执行行动**并实现特定目标的系统。与传统的单轮问答 AI 不同，Agent 具备：

- **自主性**：能独立规划和决策
- **记忆**：维护短期/长期记忆
- **工具使用**：调用外部 API、数据库、代码执行器等
- **推理与规划**：将复杂任务分解为子任务

## 二、核心架构

```
┌─────────────────────────────────────────┐
│              AI Agent                    │
│                                         │
│  ┌──────────┐  ┌──────────┐  ┌───────┐ │
│  │  感知模块 │  │  决策模块 │  │ 行动  │ │
│  │ (Input)  │→│ (LLM/大脑)│→│(Tools)│ │
│  └──────────┘  └──────────┘  └───────┘ │
│       ↑              ↕            │      │
│       │        ┌──────────┐       │      │
│       │        │  记忆模块 │       │      │
│       │        │(Memory)  │       │      │
│       │        └──────────┘       │      │
│       └───────────────────────────┘      │
│              反馈循环                     │
└─────────────────────────────────────────┘
```

## 三、关键组件

### 1. 大脑（LLM）
- OpenAI GPT-4 / Claude / 开源模型（LLaMA、Qwen 等）
- 负责理解、推理、规划、决策

### 2. 记忆系统（Memory）
| 类型 | 说明 | 实现方式 |
|------|------|----------|
| 短期记忆 | 当前对话上下文 | 对话历史列表 |
| 长期记忆 | 跨会话持久化知识 | 向量数据库（ChromaDB、Pinecone） |
| 工作记忆 | 当前任务的中间状态 | 变量/状态机 |

### 3. 工具系统（Tools）
- 搜索引擎、代码执行器、数据库查询、文件操作、API 调用等

### 4. 规划模块（Planning）
- **ReAct**：推理 + 行动交替执行
- **Plan-and-Execute**：先制定计划，再逐步执行
- **Tree of Thought**：树状搜索多种方案

## 四、开发步骤

### 步骤 1：从零实现一个最简 Agent

```python
import openai

class SimpleAgent:
    def __init__(self, system_prompt, tools):
        self.system_prompt = system_prompt
        self.tools = tools  # 可用工具
        self.memory = []    # 对话记忆
    
    def think(self, user_input):
        """核心思考循环"""
        self.memory.append({"role": "user", "content": user_input})
        
        while True:
            # 1. 调用 LLM 进行推理
            response = openai.chat.completions.create(
                model="gpt-4",
                messages=[
                    {"role": "system", "content": self.system_prompt},
                    *self.memory
                ],
                tools=self.tools,
                tool_choice="auto"
            )
            
            message = response.choices[0].message
            self.memory.append(message)
            
            # 2. 判断是否需要调用工具
            if message.tool_calls:
                for tool_call in message.tool_calls:
                    # 3. 执行工具
                    result = self.execute_tool(
                        tool_call.function.name,
                        tool_call.function.arguments
                    )
                    # 4. 将结果反馈给 LLM
                    self.memory.append({
                        "role": "tool",
                        "tool_call_id": tool_call.id,
                        "content": str(result)
                    })
                # 继续循环，让 LLM 基于工具结果再次思考
            else:
                # 5. 无需工具调用，返回最终答案
                return message.content
    
    def execute_tool(self, name, arguments):
        """执行工具调用"""
        import json
        args = json.loads(arguments)
        tool_func = self.tool_registry.get(name)
        return tool_func(**args) if tool_func else "Tool not found"
```

### 步骤 2：定义工具

```python
import json
import requests

# 定义工具函数
def search_web(query: str) -> str:
    """搜索网络信息"""
    # 调用搜索 API
    response = requests.get(f"https://api.search.com?q={query}")
    return response.json()

def calculate(expression: str) -> str:
    """执行数学计算"""
    return str(eval(expression))

def read_file(filepath: str) -> str:
    """读取文件内容"""
    with open(filepath, 'r') as f:
        return f.read()

# OpenAI 格式的工具描述
tools = [
    {
        "type": "function",
        "function": {
            "name": "search_web",
            "description": "搜索网络获取实时信息",
            "parameters": {
                "type": "object",
                "properties": {
                    "query": {
                        "type": "string",
                        "description": "搜索关键词"
                    }
                },
                "required": ["query"]
            }
        }
    },
    {
        "type": "function",
        "function": {
            "name": "calculate",
            "description": "执行数学表达式计算",
            "parameters": {
                "type": "object",
                "properties": {
                    "expression": {
                        "type": "string",
                        "description": "数学表达式，如 2+3*4"
                    }
                },
                "required": ["expression"]
            }
        }
    }
]
```

### 步骤 3：实现 ReAct 模式（推理-行动循环）

```python
REACT_PROMPT = """你是一个智能助手，请按照以下格式思考和行动：

Thought: 分析当前情况，思考下一步该做什么
Action: 选择一个工具来执行 (工具名)
Action Input: 工具的输入参数
Observation: 工具返回的结果
... (重复 Thought/Action/Observation 直到任务完成)
Thought: 我已经获得了足够的信息
Final Answer: 最终回答

可用工具: {tools}
"""

class ReActAgent:
    def __init__(self, llm, tools, max_iterations=10):
        self.llm = llm
        self.tools = {t.__name__: t for t in tools}
        self.max_iterations = max_iterations
    
    def run(self, task: str) -> str:
        prompt = REACT_PROMPT.format(tools=list(self.tools.keys()))
        scratchpad = f"\nTask: {task}\n"
        
        for i in range(self.max_iterations):
            # LLM 生成 Thought + Action
            response = self.llm.generate(prompt + scratchpad)
            scratchpad += response
            
            # 解析 Action
            if "Final Answer:" in response:
                return response.split("Final Answer:")[-1].strip()
            
            action, action_input = self.parse_action(response)
            
            # 执行工具
            if action in self.tools:
                observation = self.tools[action](action_input)
            else:
                observation = f"Error: Tool '{action}' not found"
            
            scratchpad += f"\nObservation: {observation}\n"
        
        return "达到最大迭代次数，任务未完成"
```

### 步骤 4：添加记忆系统

```python
from chromadb import Client as ChromaClient
import uuid

class MemorySystem:
    def __init__(self):
        # 短期记忆
        self.short_term = []
        
        # 长期记忆 (向量数据库)
        self.chroma = ChromaClient()
        self.collection = self.chroma.create_collection("long_term_memory")
    
    def add_short_term(self, message: dict):
        """添加短期记忆"""
        self.short_term.append(message)
        # 保持最近 20 条
        if len(self.short_term) > 20:
            # 将旧记忆转入长期记忆
            old = self.short_term.pop(0)
            self.add_long_term(old["content"])
    
    def add_long_term(self, text: str):
        """存入长期记忆（向量数据库）"""
        self.collection.add(
            documents=[text],
            ids=[str(uuid.uuid4())]
        )
    
    def recall(self, query: str, top_k: int = 5) -> list:
        """检索相关的长期记忆"""
        results = self.collection.query(
            query_texts=[query],
            n_results=top_k
        )
        return results["documents"][0] if results["documents"] else []
    
    def get_context(self, current_query: str) -> str:
        """组合短期 + 长期记忆作为上下文"""
        relevant_memories = self.recall(current_query)
        context = "### 相关历史记忆:\n"
        for mem in relevant_memories:
            context += f"- {mem}\n"
        context += "\n### 最近对话:\n"
        for msg in self.short_term[-10:]:
            context += f"{msg['role']}: {msg['content']}\n"
        return context
```

## 五、使用成熟框架快速开发

### 方案 1：LangChain

```python
from langchain.agents import create_openai_tools_agent, AgentExecutor
from langchain_openai import ChatOpenAI
from langchain.tools import tool
from langchain import hub

# 定义工具
@tool
def search(query: str) -> str:
    """搜索互联网获取信息"""
    return f"搜索结果: {query} 相关内容..."

@tool
def calculator(expression: str) -> str:
    """计算数学表达式"""
    return str(eval(expression))

# 创建 Agent
llm = ChatOpenAI(model="gpt-4", temperature=0)
prompt = hub.pull("hwchase17/openai-tools-agent")
tools = [search, calculator]

agent = create_openai_tools_agent(llm, tools, prompt)
executor = AgentExecutor(agent=agent, tools=tools, verbose=True)

# 运行
result = executor.invoke({"input": "北京的人口是多少？这个数字的平方根是多少？"})
print(result["output"])
```

### 方案 2：LangGraph（更灵活的多步骤 Agent）

```python
from langgraph.graph import StateGraph, END
from typing import TypedDict, Annotated
import operator

class AgentState(TypedDict):
    messages: Annotated[list, operator.add]
    next_step: str

def planner(state: AgentState):
    """规划下一步"""
    # LLM 决定下一步做什么
    plan = llm.invoke(state["messages"])
    return {"messages": [plan], "next_step": "execute"}

def executor(state: AgentState):
    """执行工具"""
    result = tool.run(state["messages"][-1])
    return {"messages": [result], "next_step": "evaluate"}

def evaluator(state: AgentState):
    """评估是否完成"""
    if task_complete(state):
        return {"next_step": "end"}
    return {"next_step": "planner"}

# 构建图
graph = StateGraph(AgentState)
graph.add_node("planner", planner)
graph.add_node("executor", executor)
graph.add_node("evaluator", evaluator)

graph.add_edge("planner", "executor")
graph.add_edge("executor", "evaluator")
graph.add_conditional_edges("evaluator", 
    lambda s: s["next_step"],
    {"planner": "planner", "end": END}
)

agent = graph.compile()
```

### 方案 3：OpenAI Assistants API（最简单）

```python
from openai import OpenAI

client = OpenAI()

# 创建 Assistant
assistant = client.beta.assistants.create(
    name="数据分析助手",
    instructions="你是一个数据分析专家，帮助用户分析数据并生成报告。",
    model="gpt-4",
    tools=[
        {"type": "code_interpreter"},
        {"type": "file_search"},
        {
            "type": "function",
            "function": {
                "name": "get_stock_price",
                "description": "获取股票价格",
                "parameters": {
                    "type": "object",
                    "properties": {
                        "symbol": {"type": "string"}
                    },
                    "required": ["symbol"]
                }
            }
        }
    ]
)

# 创建对话线程并运行
thread = client.beta.threads.create()
client.beta.threads.messages.create(
    thread_id=thread.id,
    role="user",
    content="分析苹果公司最近的股价趋势"
)
run = client.beta.threads.runs.create_and_poll(
    thread_id=thread.id,
    assistant_id=assistant.id
)
```

## 六、Multi-Agent 系统（多智能体协作）

```python
# 使用 CrewAI 框架
from crewai import Agent, Task, Crew

# 定义多个 Agent
researcher = Agent(
    role="研究员",
    goal="深入研究给定主题并收集关键信息",
    backstory="你是一位经验丰富的研究员",
    tools=[search_tool],
    llm=llm
)

writer = Agent(
    role="作家",
    goal="基于研究结果撰写高质量文章",
    backstory="你是一位专业作家",
    llm=llm
)

reviewer = Agent(
    role="审校员",
    goal="审查文章质量并提出改进建议",
    backstory="你是一位严格的编辑",
    llm=llm
)

# 定义任务
research_task = Task(
    description="研究 AI Agent 的最新发展趋势",
    agent=researcher,
    expected_output="研究报告"
)

write_task = Task(
    description="基于研究报告撰写一篇博客文章",
    agent=writer,
    expected_output="博客文章",
    context=[research_task]
)

review_task = Task(
    description="审查并优化文章",
    agent=reviewer,
    expected_output="最终文章",
    context=[write_task]
)

# 组建团队执行
crew = Crew(
    agents=[researcher, writer, reviewer],
    tasks=[research_task, write_task, review_task],
    verbose=True
)

result = crew.kickoff()
```

## 七、生产环境关键考量

### 1. 安全与防护
```python
# 工具调用白名单
ALLOWED_TOOLS = {"search", "calculator", "read_file"}

# 输入验证
def validate_tool_call(tool_name, args):
    if tool_name not in ALLOWED_TOOLS:
        raise SecurityError(f"未授权的工具: {tool_name}")
    # 防注入
    if "exec(" in str(args) or "import os" in str(args):
        raise SecurityError("检测到危险输入")

# 沙箱执行代码
import subprocess
def safe_execute(code: str):
    result = subprocess.run(
        ["python", "-c", code],
        capture_output=True, timeout=30,
        # 使用 Docker 容器隔离更安全
    )
    return result.stdout.decode()
```

### 2. 可观测性与调试
```python
import logging
from datetime import datetime

class AgentLogger:
    def log_step(self, step_type, content, metadata=None):
        log_entry = {
            "timestamp": datetime.now().isoformat(),
            "step_type": step_type,  # thought/action/observation
            "content": content,
            "metadata": metadata,
            "token_usage": self.get_token_count(content)
        }
        logging.info(json.dumps(log_entry, ensure_ascii=False))
        # 也可以发送到 LangSmith / LangFuse 等平台
```

### 3. 成本控制
- **缓存**：相同查询缓存 LLM 响应
- **模型分级**：简单任务用小模型，复杂任务用大模型
- **Token 预算**：设置每次任务的最大 Token 限制
- **最大迭代次数**：防止死循环

### 4. 错误处理与容错

```python
class RobustAgent:
    def execute_with_retry(self, func, max_retries=3):
        for attempt in range(max_retries):
            try:
                return func()
            except RateLimitError:
                time.sleep(2 ** attempt)  # 指数退避
            except ToolExecutionError as e:
                # 让 LLM 知道工具失败，尝试替代方案
                self.memory.append({
                    "role": "system",
                    "content": f"工具执行失败: {e}，请尝试其他方法"
                })
            except Exception as e:
                logging.error(f"未预期错误: {e}")
                break
        return "任务执行失败，请重试"
```

## 八、推荐学习路径

```
阶段一: 基础
├── 理解 LLM 的 Function Calling / Tool Use
├── 用 Python 手写一个最简 ReAct Agent
└── 理解 Prompt Engineering 在 Agent 中的作用

阶段二: 框架
├── LangChain / LangGraph
├── CrewAI / AutoGen (多 Agent)
└── OpenAI Assistants API

阶段三: 进阶
├── 向量数据库与 RAG 集成
├── 复杂规划算法 (Tree of Thought, Graph of Thought)
├── 人机协作 (Human-in-the-loop)
└── Agent 评估与 Benchmark

阶段四: 生产
├── 安全防护与沙箱隔离
├── 可观测性 (LangSmith / LangFuse)
├── 成本优化与性能调优
└── 部署与扩展 (API 化、队列化)
```

## 九、推荐资源

| 资源 | 说明 |
|------|------|
| [LangChain 文档](https://python.langchain.com) | 最流行的 Agent 框架 |
| [LangGraph](https://langchain-ai.github.io/langgraph/) | 状态机驱动的 Agent |
| [CrewAI](https://docs.crewai.com) | 多 Agent 协作框架 |
| [AutoGPT](https://github.com/Significant-Gravitas/AutoGPT) | 自主 Agent 先驱项目 |
| 论文《ReAct》 | Agent 核心模式的理论基础 |
| 论文《Toolformer》 | LLM 学习使用工具 |
| Andrew Ng 的 AI Agent 课程 | DeepLearning.AI 出品 |

---

**总结**：开发 AI Agent 的核心就是 **LLM + 工具 + 记忆 + 循环**。建议从手写一个简单的 ReAct Agent 开始理解原理，然后再使用框架加速开发，最后关注生产环境的安全、可观测性和成本控制。