package com.htaste.aihelper.ai.rag.QueryTransform;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.rag.query.transformer.QueryTransformer;
import jakarta.annotation.Resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LlmMultiQueryTransformer implements QueryTransformer {
    @Resource(name = "chatModel")
    private ChatModel llm;

    private int variantCount = 3; // 重写生成子问题个数

//    public LlmMultiQueryTransformer(ChatModel llm, int variantCount) {
//        this.llm = llm;
//        this.variantCount = variantCount;
//    }

    @Override
    public Collection<Query> transform(Query query) {

        // ---------- 第一步：查询重写 ----------
        String rewritten = rewriteQuery(query.text());

        // ---------- 第二步：多查询扩展 ----------
        List<Query> expanded = expandQueries(rewritten, variantCount);

        // ---------- 结果整合 ----------
        // 把改写版本也放进结果，以确保基础语义版本仍在检索范围内
        List<Query> finalQueries = new ArrayList<>();
        finalQueries.add(new Query(rewritten));
        finalQueries.addAll(expanded);

        return finalQueries;
    }


    /**
     * 使用 LLM 对原始查询进行语义重写，确保更贴合知识库语言风格。
     */
    private String rewriteQuery(String originalQuery) {
        String prompt = String.format(
                "请改写以下问题，使其语义更清晰、更正式，并更适合检索语义相似的知识内容：\n%s",
                originalQuery
        );

        String rewritten = llm.chat(prompt);

        // 避免空响应，退回原查询
        if (rewritten == null || rewritten.isBlank()) {
            rewritten = originalQuery;
        }

        return rewritten.trim();
    }

    /**
     * 基于重写后的查询结果，生成若干语义相似但表达不同的检索问题。
     */
    private List<Query> expandQueries(String rewrittenQuery, int count) {
        String prompt = String.format(
                "根据以下问题，生成 %d 个语义相似但用词不同的搜索问题，每行一个：\n%s",
                count, rewrittenQuery
        );

        String output = llm.chat(prompt);

        List<Query> queries = new ArrayList<>();
        if (output != null) {
            String[] lines = output.split("\\r?\\n");
            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    queries.add(new Query(line.trim()));
                }
            }
        }
        return queries;
    }
}
