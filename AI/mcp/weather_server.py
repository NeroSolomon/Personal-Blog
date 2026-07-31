"""
最小 MCP Server 示例：天气查询服务

演示 MCP Server 如何向 AI 暴露三类内容：
  1. Tools（工具）    —— 可执行的函数
  2. Resources（资源）—— 可读取的数据
  3. Prompts（提示词）—— 预定义的提示词模板

依赖安装：
    pip install "mcp[cli]"

以开发模式运行（自带调试面板 MCP Inspector）：
    mcp dev weather_server.py

或直接运行（stdio 传输，供宿主如 Claude Desktop 连接）：
    python weather_server.py
"""

from mcp.server.fastmcp import FastMCP

# 创建一个 MCP Server 实例，名字会在握手 initialize 阶段返回给客户端
mcp = FastMCP("weather-demo")


# ─────────────────────────────────────────────
# 1. Tools（工具）：通过 @mcp.tool() 装饰器暴露
#    FastMCP 会自动从函数签名 + 类型注解 + docstring
#    生成 tools/list 所需的 name / description / inputSchema
# ─────────────────────────────────────────────
@mcp.tool()
def get_weather(city: str) -> str:
    """查询指定城市的当前天气。

    Args:
        city: 城市名称，例如 "北京"、"上海"
    """
    # 这里用假数据模拟，真实场景应调用天气 API
    fake_db = {
        "北京": "晴，25°C，微风",
        "上海": "多云，28°C，东南风 3 级",
        "广州": "雷阵雨，30°C，湿度 80%",
    }
    return fake_db.get(city, f"暂无 {city} 的天气数据")


@mcp.tool()
def compare_temperature(city_a: str, city_b: str) -> str:
    """比较两个城市当前的温度差异。"""
    temps = {"北京": 25, "上海": 28, "广州": 30}
    ta, tb = temps.get(city_a), temps.get(city_b)
    if ta is None or tb is None:
        return "存在无法识别的城市"
    diff = abs(ta - tb)
    hotter = city_a if ta > tb else city_b
    return f"{hotter} 更热，温差 {diff}°C"


# ─────────────────────────────────────────────
# 2. Resources（资源）：通过 @mcp.resource() 暴露
#    用 URI 唯一标识，客户端用 resources/read 读取
#    下面第二个是「资源模板」，URI 中的 {city} 是参数
# ─────────────────────────────────────────────
@mcp.resource("weather://cities")
def list_supported_cities() -> str:
    """返回当前支持查询的城市清单（静态资源）。"""
    return "支持的城市：北京、上海、广州"


@mcp.resource("weather://history/{city}")
def weather_history(city: str) -> str:
    """返回某城市近 3 天的历史天气（参数化资源模板）。"""
    return f"{city} 近三天：晴 / 多云 / 小雨"


# ─────────────────────────────────────────────
# 3. Prompts（提示词）：通过 @mcp.prompt() 暴露
#    客户端用 prompts/list 发现、prompts/get 获取填充后的模板
# ─────────────────────────────────────────────
@mcp.prompt()
def travel_advice(city: str) -> str:
    """生成一段"根据天气给出穿衣/出行建议"的提示词。"""
    return (
        f"请先调用 get_weather 工具查询 {city} 的天气，"
        f"然后根据结果给出穿衣建议和出行注意事项。"
    )


if __name__ == "__main__":
    # 默认使用 stdio 传输：通过标准输入输出与宿主通信
    # 若要用 HTTP/SSE，可改为 mcp.run(transport="sse")
    mcp.run()
