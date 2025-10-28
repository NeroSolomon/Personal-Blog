# React Fiber 详解

## 什么是 Fiber

**React Fiber** 是 React 16 引入的新的**协调引擎（Reconciliation Engine）**，是 React 核心算法的完全重写。Fiber 可以理解为是一种数据结构，也是一种架构。

## 为什么需要 Fiber

### 旧版 React 的问题

在 React 15 及之前版本，React 使用**递归**的方式进行组件树的遍历和更新：

```javascript
// 伪代码示例
function reconcile(element) {
    // 创建或更新节点
    updateNode(element);
    
    // 递归处理子节点
    element.children.forEach(child => {
        reconcile(child); // 递归调用
    });
}
```

**主要问题：**
1. **不可中断** - 一旦开始更新，必须一次性完成
2. **长时间阻塞主线程** - 大型组件树更新时会造成卡顿
3. **无法设置优先级** - 所有更新同等重要

## Fiber 的核心特性

### 1. 可中断的更新

```javascript
// Fiber 架构下的工作流程
function workLoop(deadline) {
    while (nextUnitOfWork && deadline.timeRemaining() > 0) {
        // 执行一个工作单元
        nextUnitOfWork = performUnitOfWork(nextUnitOfWork);
    }
    
    // 如果还有工作且时间不够，请求下一个时间片
    if (nextUnitOfWork) {
        requestIdleCallback(workLoop);
    }
}
```

### 2. 任务优先级

Fiber 支持多种优先级：

```javascript
// React 内部的优先级
const priorities = {
    Immediate: 1,        // 立即执行（如用户输入）
    UserBlocking: 2,     // 用户交互（如点击、滚动）
    Normal: 3,           // 普通更新（如数据请求）
    Low: 4,              // 低优先级（如不可见内容）
    Idle: 5              // 空闲时执行
};
```

## Fiber 数据结构

每个 Fiber 节点包含以下信息：

```javascript
{
    // 节点类型信息
    type: 'div',              // 元素类型
    key: null,                // key
    
    // 关系指针
    return: parentFiber,      // 父节点
    child: firstChildFiber,   // 第一个子节点
    sibling: nextSiblingFiber,// 下一个兄弟节点
    
    // 状态
    pendingProps: {...},      // 新的 props
    memoizedProps: {...},     // 旧的 props
    memoizedState: {...},     // 旧的 state
    
    // 副作用
    flags: Update,            // 标记类型（更新、删除等）
    nextEffect: nextFiber,    // 下一个有副作用的 Fiber
    
    // 优先级
    lanes: 0b0001,           // 当前优先级
    childLanes: 0b0001,      // 子树优先级
    
    // 双缓存
    alternate: workInProgressFiber, // 对应的另一棵树
}
```

## Fiber 的工作原理

### 1. 双缓存机制

React 维护两棵 Fiber 树：

```
Current Tree (当前显示)      WorkInProgress Tree (正在构建)
     ┌───┐                        ┌───┐
     │ A │◄──────alternate────────│ A'│
     └─┬─┘                        └─┬─┘
       │                            │
    ┌──┴──┐                      ┌──┴──┐
    │     │                      │     │
  ┌─▼─┐ ┌─▼─┐                  ┌─▼─┐ ┌─▼─┐
  │ B │ │ C │                  │ B'│ │ C'│
  └───┘ └───┘                  └───┘ └───┘
```

### 2. 两个阶段

#### Render 阶段（可中断）
```javascript
// 可以被打断的阶段
function renderPhase() {
    // 1. beginWork - 向下遍历
    beginWork(fiber);
    
    // 2. completeWork - 向上归并
    completeWork(fiber);
    
    // 可以在任意时刻中断，不影响 UI
}
```

#### Commit 阶段（不可中断）
```javascript
// 必须同步执行的阶段
function commitPhase() {
    // 1. before mutation
    commitBeforeMutationEffects();
    
    // 2. mutation - 操作 DOM
    commitMutationEffects();
    
    // 3. layout
    commitLayoutEffects();
}
```

## Fiber 带来的好处

### 1. 时间切片（Time Slicing）

```javascript
function App() {
    const [count, setCount] = useState(0);
    
    return (
        <div>
            <input onChange={handleInput} /> {/* 高优先级 */}
            <ExpensiveList count={count} />  {/* 可以被打断 */}
        </div>
    );
}
```

### 2. Suspense 和并发特性

```javascript
import { Suspense } from 'react';

function App() {
    return (
        <Suspense fallback={<Loading />}>
            <LazyComponent />
        </Suspense>
    );
}
```

### 3. 更流畅的用户体验

- 高优先级任务（如用户输入）可以打断低优先级任务
- 避免长时间阻塞导致的卡顿
- 支持增量渲染

## 实际应用场景

### 1. 大列表渲染

```javascript
function BigList({ items }) {
    // Fiber 会将渲染分成多个小任务
    return (
        <div>
            {items.map(item => (
                <ExpensiveItem key={item.id} data={item} />
            ))}
        </div>
    );
}
```

### 2. 并发模式

```javascript
import { unstable_useTransition as useTransition } from 'react';

function SearchResults() {
    const [isPending, startTransition] = useTransition();
    const [query, setQuery] = useState('');
    
    const handleChange = (e) => {
        // 高优先级：立即更新输入框
        setQuery(e.target.value);
        
        // 低优先级：可以被打断的搜索
        startTransition(() => {
            setSearchResults(search(e.target.value));
        });
    };
    
    return <input onChange={handleChange} />;
}
```

## 总结

**React Fiber 是：**
- 一种新的协调算法
- 一种可中断的工作机制  
- 支持优先级调度的架构
- React 并发特性的基础

**核心优势：**
- ✅ 更流畅的用户体验
- ✅ 更好的任务优先级管理
- ✅ 为未来的并发特性打下基础
- ✅ 不阻塞主线程，避免掉帧

Fiber 是 React 能够实现 Suspense、并发模式、时间切片等高级特性的基础！

# React Fiber 实现可中断的核心机制

## 1. 核心原理：时间切片（Time Slicing）

### 旧版 React（Stack Reconciler）
```javascript
// 递归方式 - 不可中断
function updateComponent(component) {
    renderComponent(component);
    component.children.forEach(child => {
        updateComponent(child); // 递归，无法中断
    });
}
```

### Fiber 架构 - 可中断
```javascript
// 循环 + 链表遍历 - 可中断
function workLoop(deadline) {
    // shouldYield 判断是否需要中断
    while (nextUnitOfWork && !shouldYield()) {
        nextUnitOfWork = performUnitOfWork(nextUnitOfWork);
    }
    
    // 如果还有工作，继续调度
    if (nextUnitOfWork) {
        scheduleCallback(workLoop);
    }
}
```

## 2. 实现可中断的三个关键点

### ① 将递归改为循环

```javascript
// 伪代码：Fiber 的工作循环
function performUnitOfWork(fiber) {
    // 1. 执行当前 fiber 的工作
    beginWork(fiber);
    
    // 2. 返回下一个要处理的 fiber（深度优先遍历）
    if (fiber.child) {
        return fiber.child; // 优先处理子节点
    }
    
    // 3. 没有子节点，处理兄弟节点
    while (fiber) {
        completeWork(fiber);
        
        if (fiber.sibling) {
            return fiber.sibling; // 处理兄弟节点
        }
        
        fiber = fiber.return; // 回到父节点
    }
    
    return null;
}
```

### ② 链表结构保存遍历状态

```javascript
// Fiber 节点的链表结构
const fiber = {
    // 保存遍历位置
    child: firstChild,      // 指向第一个子节点
    sibling: nextSibling,   // 指向下一个兄弟节点
    return: parent,         // 指向父节点
    
    // 保存工作状态
    alternate: oldFiber,    // 指向上一次的 fiber
    effectTag: 'UPDATE',    // 本次的操作类型
    
    // 保存进度
    index: 0,               // 在父节点中的位置
    pendingProps: {},       // 待处理的 props
}
```

### ③ 使用 Scheduler 进行时间切片

```javascript
// React Scheduler 的简化实现
function scheduleCallback(callback) {
    const expirationTime = getCurrentTime() + timeout;
    
    const newTask = {
        callback,
        expirationTime,
        priorityLevel,
    };
    
    // 推入任务队列
    taskQueue.push(newTask);
    
    // 请求调度
    requestHostCallback(flushWork);
}

// 执行工作
function flushWork(initialTime) {
    return workLoop(initialTime);
}

function workLoop(initialTime) {
    let currentTask = peek(taskQueue);
    
    while (currentTask !== null) {
        // 检查是否需要中断（时间片用完）
        if (currentTask.expirationTime > currentTime && shouldYieldToHost()) {
            // 时间片用完，中断
            break;
        }
        
        const callback = currentTask.callback;
        currentTask.callback = null;
        
        const continuationCallback = callback();
        
        // 如果返回了函数，说明还有工作未完成
        if (typeof continuationCallback === 'function') {
            currentTask.callback = continuationCallback;
        } else {
            // 任务完成，移除
            if (currentTask === peek(taskQueue)) {
                pop(taskQueue);
            }
        }
        
        currentTask = peek(taskQueue);
    }
    
    // 返回是否还有工作
    return currentTask !== null;
}
```

## 3. 判断是否需要中断

### shouldYield 的实现

```javascript
// 简化版 shouldYield
function shouldYield() {
    const currentTime = getCurrentTime();
    
    // 1. 检查是否超过 5ms（一帧的时间）
    if (currentTime >= deadline) {
        return true;
    }
    
    // 2. 检查是否有更高优先级的任务
    if (needsPaint || scheduling.isInputPending()) {
        return true;
    }
    
    return false;
}
```

### 实际使用的时间切片

```javascript
// React 内部的实现（使用 MessageChannel）
const channel = new MessageChannel();
const port = channel.port2;

channel.port1.onmessage = () => {
    // 执行任务
    const hasMoreWork = workLoop(getCurrentTime());
    
    // 如果还有工作，继续调度
    if (hasMoreWork) {
        port.postMessage(null);
    }
};

// 请求调度
function schedulePerformWorkUntilDeadline() {
    port.postMessage(null);
}
```

## 4. 完整的中断恢复流程

```javascript
let nextUnitOfWork = null; // 当前工作单元
let wipRoot = null;         // 工作中的根节点

// 开始渲染
function render(element, container) {
    wipRoot = {
        dom: container,
        props: { children: [element] },
        alternate: currentRoot,
    };
    nextUnitOfWork = wipRoot;
    
    // 开始调度
    scheduleCallback(workLoop);
}

// 工作循环
function workLoop(deadline) {
    let shouldYield = false;
    
    while (nextUnitOfWork && !shouldYield) {
        nextUnitOfWork = performUnitOfWork(nextUnitOfWork);
        
        // 检查是否需要中断
        shouldYield = deadline.timeRemaining() < 1;
    }
    
    // 如果所有工作完成，提交
    if (!nextUnitOfWork && wipRoot) {
        commitRoot();
    }
    
    // 返回是否还有工作（用于 Scheduler）
    return nextUnitOfWork !== null;
}

// 执行一个工作单元
function performUnitOfWork(fiber) {
    // 1. 处理当前 fiber
    if (!fiber.dom) {
        fiber.dom = createDom(fiber);
    }
    
    // 2. 创建子 fiber
    const elements = fiber.props.children;
    reconcileChildren(fiber, elements);
    
    // 3. 返回下一个工作单元（深度优先）
    if (fiber.child) {
        return fiber.child;
    }
    
    let nextFiber = fiber;
    while (nextFiber) {
        if (nextFiber.sibling) {
            return nextFiber.sibling;
        }
        nextFiber = nextFiber.return;
    }
}
```

## 5. 优先级中断示例

```javascript
// 不同优先级的更新
function handleUserInput() {
    // 高优先级更新（用户输入）
    ReactDOM.flushSync(() => {
        setInputValue(newValue);
    });
}

function loadData() {
    // 低优先级更新（数据加载）
    startTransition(() => {
        setData(newData);
    });
}

// Scheduler 会这样处理
function scheduleUpdate(fiber, lane) {
    if (isHigherPriority(lane, currentLane)) {
        // 中断当前低优先级任务
        if (workInProgress !== null) {
            workInProgress.flags |= Incomplete;
        }
        
        // 重新调度高优先级任务
        scheduleCallback(ImmediatePriority, performSyncWorkOnRoot);
    }
}
```

## 6. 关键总结

React Fiber 实现可中断的核心要素：

1. **链表结构** - 可以随时保存当前位置
2. **循环替代递归** - 可以随时跳出循环
3. **时间切片** - 5ms 一个切片，超时就中断
4. **Scheduler** - 统一调度，管理任务优先级
5. **双缓存** - 可以安全地中断和恢复

这使得 React 能够：
- ⏸️ 暂停工作，让浏览器处理更紧急的任务
- ⏭️ 跳过已经完成的工作
- 🔄 复用之前的工作结果
- 🎯 根据优先级分配工作

# React Fiber 任务恢复时机

## 1. 核心恢复机制

### 主要恢复时机

```javascript
// React Scheduler 的工作循环
function workLoop(initialTime) {
    let currentTime = initialTime;
    currentTask = taskQueue[0];
    
    while (currentTask) {
        // ⭐ 关键：判断是否需要让出控制权
        if (currentTask.expirationTime > currentTime && shouldYield()) {
            // 中断！保存当前进度
            break;
        }
        
        // 执行任务
        const callback = currentTask.callback;
        currentTask.callback = callback(currentTime);
        
        currentTask = taskQueue[0];
    }
    
    // ⭐ 重点：如果还有任务，注册下一次调度
    if (currentTask) {
        return true; // 告诉调度器还有工作要做
    }
    
    return false;
}
```

## 2. 四种恢复触发条件

### ① 浏览器空闲时（最常见）

```javascript
// 使用 MessageChannel 模拟 requestIdleCallback
const channel = new MessageChannel();
const port = channel.port2;

channel.port1.onmessage = () => {
    // 浏览器空闲时执行
    const hasMoreWork = flushWork();
    
    if (hasMoreWork) {
        // ⭐ 还有工作，继续调度
        schedulePerformWorkUntilDeadline();
    }
};

function schedulePerformWorkUntilDeadline() {
    port.postMessage(null);
}
```

**触发时机**：
- 当前宏任务执行完成
- 下一帧渲染前的空闲时间
- 通常在 **5ms** 时间片后

### ② 高优先级任务插队

```javascript
// 高优先级任务打断低优先级任务
function ensureRootIsScheduled(root) {
    const nextLanes = getNextLanes(root);
    const newCallbackPriority = getHighestPriorityLane(nextLanes);
    
    const existingCallbackPriority = root.callbackPriority;
    
    // ⭐ 如果新任务优先级更高
    if (newCallbackPriority > existingCallbackPriority) {
        // 取消当前任务
        if (existingCallbackNode) {
            cancelCallback(existingCallbackNode);
        }
        
        // ⭐ 立即调度高优先级任务
        scheduleCallback(newCallbackPriority, () => {
            return performConcurrentWorkOnRoot(root);
        });
    }
}
```

**示例场景**：
```javascript
// 低优先级任务正在执行
function LowPriorityComponent() {
    const [data] = useState(expensiveData);
    return <ExpensiveList data={data} />;
}

// 用户突然输入 - 高优先级！
<input onChange={handleChange} /> // ⭐ 立即中断并处理
```

### ③ 同步任务强制执行

```javascript
// 某些情况下必须同步执行
function flushSyncCallbacks() {
    if (syncQueue !== null) {
        const queue = syncQueue;
        syncQueue = null;
        
        // ⭐ 同步执行所有回调，不可中断
        for (let i = 0; i < queue.length; i++) {
            queue[i]();
        }
    }
}

// 触发场景
ReactDOM.flushSync(() => {
    setState(newValue); // 立即同步更新
});
```

### ④ 任务过期时必须执行

```javascript
function shouldYield() {
    const currentTime = getCurrentTime();
    
    // ⭐ 检查任务是否过期
    if (currentTask.expirationTime <= currentTime) {
        // 任务过期，必须执行，不能中断
        return false;
    }
    
    // 检查是否超过时间片
    return getCurrentTime() >= deadline;
}
```

## 3. 完整的调度流程

```javascript
// 简化的调度流程
function scheduleUpdateOnFiber(fiber, lane) {
    // 1. 标记更新
    markUpdateLaneFromFiberToRoot(fiber, lane);
    
    // 2. 确保根节点被调度
    ensureRootIsScheduled(root);
}

function ensureRootIsScheduled(root) {
    // 3. 获取优先级
    const nextLanes = getNextLanes(root);
    const newCallbackPriority = getHighestPriorityLane(nextLanes);
    
    // 4. 如果已有相同优先级任务在执行，复用
    if (newCallbackPriority === existingCallbackPriority) {
        return;
    }
    
    // 5. 取消旧任务，调度新任务
    if (existingCallbackNode) {
        cancelCallback(existingCallbackNode);
    }
    
    // 6. 根据优先级调度
    let schedulerPriorityLevel;
    switch (lanesToEventPriority(nextLanes)) {
        case DiscreteEventPriority:
            schedulerPriorityLevel = ImmediateSchedulerPriority;
            break;
        case ContinuousEventPriority:
            schedulerPriorityLevel = UserBlockingSchedulerPriority;
            break;
        case DefaultEventPriority:
            schedulerPriorityLevel = NormalSchedulerPriority;
            break;
        default:
            schedulerPriorityLevel = IdleSchedulerPriority;
    }
    
    // ⭐ 7. 注册回调，等待恢复执行
    newCallbackNode = scheduleCallback(
        schedulerPriorityLevel,
        performConcurrentWorkOnRoot.bind(null, root)
    );
}
```

## 4. 实际例子

### 场景一：正常的时间切片

```javascript
function App() {
    return (
        <div>
            {/* 假设渲染需要 50ms */}
            <ExpensiveList items={10000} />
        </div>
    );
}

// 执行流程：
// T0:    开始渲染，处理前 200 个项
// T5ms:  达到时间片限制 → ⭐ 中断
// T5ms:  requestIdleCallback 注册回调
// T10ms: 浏览器空闲 → ⭐ 恢复，处理 201-400 个项
// T15ms: 再次中断
// ...循环直到完成
```

### 场景二：高优先级插队

```javascript
function SearchBox() {
    const [query, setQuery] = useState('');
    const [results, setResults] = useState([]);
    
    return (
        <>
            {/* 高优先级：用户输入 */}
            <input 
                value={query}
                onChange={e => setQuery(e.target.value)} 
            />
            
            {/* 低优先级：搜索结果 */}
            <Results data={results} />
        </>
    );
}

// 执行流程：
// T0:    正在渲染大量搜索结果（低优先级）
// T3ms:  用户输入字符 → ⭐ 立即中断当前渲染
// T3ms:  优先处理输入更新（高优先级）
// T5ms:  输入处理完成 → ⭐ 恢复渲染搜索结果
```

## 5. 恢复时的状态保存

```javascript
// Fiber 保存了完整的工作状态
const fiber = {
    // 当前位置
    return: parentFiber,
    child: null,
    sibling: nextFiber,
    
    // 工作进度
    index: 2,                    // 正在处理第 3 个子节点
    pendingProps: newProps,      // 待处理的属性
    memoizedProps: oldProps,     // 已处理的属性
    
    // ⭐ 关键：双缓存
    alternate: currentFiber,     // 指向旧的 fiber
    
    // 副作用链
    firstEffect: effect1,
    lastEffect: effect3,
};

// 恢复时可以直接从 nextUnitOfWork 继续
function resumeWork() {
    while (nextUnitOfWork && !shouldYield()) {
        nextUnitOfWork = performUnitOfWork(nextUnitOfWork);
    }
}
```

## 6. 总结

**任务恢复的四个时机：**

1. ⏱️ **浏览器空闲时**（MessageChannel/setTimeout）
2. 🚀 **高优先级任务插队后**（立即）
3. ⚡ **同步任务强制执行**（立即）
4. ⏰ **任务过期必须完成**（立即）

**关键特点：**
- 通过 Scheduler 管理任务队列
- 使用链表结构保存进度
- 支持优先级抢占
- 保证高优先级任务及时响应