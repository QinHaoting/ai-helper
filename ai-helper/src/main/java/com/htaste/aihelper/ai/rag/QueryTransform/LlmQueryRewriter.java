package com.htaste.aihelper.ai.rag.QueryTransform;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.rag.query.transformer.QueryTransformer;

import java.util.Collection;
import java.util.List;

/**
 * RAG Query重写
 * @author Haoting Qin
 * @date 2025/10/11
 * @version 1.0
 */
public class LlmQueryRewriter implements QueryTransformer {

    private final ChatModel queryTransformModel;

    public LlmQueryRewriter(ChatModel chatModel) {
        this.queryTransformModel = chatModel;
    }

    /**
     * 使用 LLM 对原始查询进行语义重写，确保更贴合知识库语言风格。
     */
    @Override
    public Collection<Query> transform(Query originalQuery) {
        String prompt = String.format(
                "请改写以下问题，尽可能精简明了，使其语义更清晰、更正式，并更适合检索语义相似的知识内容：\n%s",
                originalQuery
        );

        String rewritten = queryTransformModel.chat(prompt);

        // 避免空响应，退回原查询
        if (rewritten == null || rewritten.isBlank()) {
            rewritten = originalQuery.text();
        }

        rewritten = rewritten.trim();

        // 返回单个改写后的 query（使用集合包装，接口要求）
        return List.of(new Query(rewritten));
    }
}
