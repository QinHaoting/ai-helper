package com.htaste.aihelper.ai.rag;

import com.htaste.aihelper.ai.rag.QueryTransform.SmartQueryTransformer;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.aggregator.DefaultContentAggregator;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.transformer.QueryTransformer;
import dev.langchain4j.store.embedding.EmbeddingStore;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Haoting Qin
 * @date 2025/9/16
 */
@Configuration
public class RagConfig {
    @Resource(name = "qwenEmbeddingModel")
    private EmbeddingModel embeddingModel;

    @Resource
    private EmbeddingStore<TextSegment> embeddingStore;

    @Resource(name = "queryTransformModel")
    private ChatModel queryTransformModel;


    /**
     * 多语句查询扩展个数，默认为3
     */
    private final int ExpandQueryNumber = 3;

    /**
     * 内容检索结果返回个数，默认为5
    */
    private int ContentRetrieveNumber = 5;


    /**
     * 查询重写器
     * @return
     */
    @Bean
    public QueryTransformer queryTransformer() {
//        // 1. Query重写
//        LlmQueryRewriter queryRewriter = new LlmQueryRewriter(queryTransformModel);
//
//        // 2. 多语句扩展
//        ExpandingQueryTransformer expander =
//                ExpandingQueryTransformer.builder()
//                        .chatModel(queryTransformModel)
//                        .n(ExpandQueryNumber)   // 默认扩展语句的个数
//                        .build();
//
//        // 3. 组合成一个复合 transformer
//        CompositeQueryTransformer compositeTransformer =
//                new CompositeQueryTransformer(List.of(queryRewriter, expander));
//        return compositeTransformer;

        return new SmartQueryTransformer(queryTransformModel, ExpandQueryNumber);
    }

    /**
     * 查询增强器
     * @return
     */
    @Bean
    public RetrievalAugmentor retrievalAugmentor(
            QueryTransformer queryTransformer,
            ContentRetriever contentRetriever
    ) {
        return DefaultRetrievalAugmentor.builder()
                .queryTransformer(queryTransformer)
                .contentRetriever(contentRetriever)
                .contentAggregator(new DefaultContentAggregator())
//                .contentAggregator(new ReRankingContentAggregator()) // TODO 重排模型
                .build();
    }

    /**
     * 内容检索器
     * @return
     */
    @Bean
    public ContentRetriever contentRetriever() {
        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder() // 自定义内容检索器
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(ContentRetrieveNumber) // 最多返回5条结果
//                .minScore(0.75) // 最低相似度分数
                .build();
        return contentRetriever;
    }
}
