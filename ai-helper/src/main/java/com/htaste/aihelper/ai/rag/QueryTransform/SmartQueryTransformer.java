package com.htaste.aihelper.ai.rag.QueryTransform;


import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.rag.query.transformer.ExpandingQueryTransformer;
import dev.langchain4j.rag.query.transformer.QueryTransformer;

import java.util.Collection;
import java.util.List;

/**
 * 动态查询改写器<p>
 * 根据问题复杂度动态选择：<p>
 * - 简单问题：仅执行 Query Rewrite <p>
 * - 复杂问题：执行 Query Rewrite + Multi-Query Expansion <p>
 * @author Haoting Qin
 * @version 1.0
 * @date 2025-10-11
 */
public class SmartQueryTransformer implements QueryTransformer {
    /**
     * Query重写模型
     */
    private final ChatModel queryTransformModel;

    /**
     * 多语句查询扩展个数
     */
    private final int expandQueryNumber ;


    public SmartQueryTransformer(ChatModel queryTransformModel, int expandQueryNumber) {
        this.queryTransformModel = queryTransformModel;
        this.expandQueryNumber = expandQueryNumber;
    }

    @Override
    public Collection<Query> transform(Query query) {
        // 1. 进行 Query 重写
        LlmQueryRewriter rewriter = new LlmQueryRewriter(queryTransformModel);
        Collection<Query> rewrittenQueries = rewriter.transform(query);
        Query rewrittenQuery = rewrittenQueries.iterator().next();

        // 2. 判定是否需要扩展多语句查询
        if (isSimpleQuestion(query.text())) { // 简单问题：直接返回改写后的单一 Query
            return List.of(rewrittenQuery);
        } else { // 复杂问题：执行扩展
            ExpandingQueryTransformer expander = ExpandingQueryTransformer.builder()
                    .chatModel(queryTransformModel)
                    .n(expandQueryNumber)
                    .build();

            // 合并改写版本 + 扩展版本
            Collection<Query> expandedQueries = expander.transform(rewrittenQuery);
            return expandedQueries;
        }
    }

    /**
     * 根据问题长度和结构判断复杂度。
     * 可以根据字数、标点、关键词启发式地判断。
     */
    private boolean isSimpleQuestion(String text) {
        int length = text.length();

        // 包含逗号、顿号、问号多的句子通常为复杂问法
        long complexMarks = text.chars()
                .filter(ch -> ch == ',' || ch == '，' || ch == '和' || ch == '或')
                .count();

        // 判断规则：长度短且标点少视为“简单问题”
        return length < 50 && complexMarks < 2;
    }
}