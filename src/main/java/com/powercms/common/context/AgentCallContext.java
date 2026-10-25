package com.powercms.common.context;

import java.util.UUID;

/**
 * Agent 调用归因上下文（FR-AI-MCP-006 配额联动）。
 *
 * <p>MCP 工具内部触发的 LLM 调用（生成/向量化）产生的 Token 消耗须计入调用方 Token 配额报表
 * 的 Agent 维度。MCP 执行器在工具执行前将调用方身份置入，AI 底座（{@code AiUsageService}）
 * 落账时读取并写入 {@code ai_usage_record.agent_token_id}；工具执行结束清除。
 *
 * <p>放置于 {@code cms-common} 的原因：模块依赖方向为 mcp→ai（Token 消耗发生在工具内部），
 * ai 模块不得反向依赖 mcp；common 为两者共同下游，消除循环依赖。
 *
 * <p>ThreadLocal 口径：仅覆盖同步请求线程（MCP tools/call 为同步执行，检索/embedding
 * 均在请求线程内完成）；异步批处理线程不继承——符合「MCP 调用维度」的统计口径。
 */
public final class AgentCallContext {

    private static final ThreadLocal<UUID> AGENT_TOKEN = new ThreadLocal<>();

    private AgentCallContext() {
    }

    /** 标记当前线程正在为指定 API Token（Agent）执行工具。 */
    public static void set(UUID tokenId) {
        AGENT_TOKEN.set(tokenId);
    }

    /** 读取当前 Agent Token ID；非 MCP 调用（后台用户/匿名）返回 null。 */
    public static UUID get() {
        return AGENT_TOKEN.get();
    }

    /** 清除（须在 finally 中调用，防线程池串号）。 */
    public static void clear() {
        AGENT_TOKEN.remove();
    }
}
