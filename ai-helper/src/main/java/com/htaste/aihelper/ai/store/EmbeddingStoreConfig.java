package com.htaste.aihelper.ai.store;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.pinecone.PineconeEmbeddingStore;
import dev.langchain4j.store.embedding.pinecone.PineconeServerlessIndexConfig;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Haoting Qin
 * @date 2025/9/16
 */
@Configuration
public class EmbeddingStoreConfig {

    @Value("${embedding-store.pinecone.api-key}")
    private String PINECONE_API_KEY;

    @Value("${embedding-store.pinecone.index-name}")
    private String PINECONE_INDEX_NAME;

    @Value("${embedding-store.pinecone.cloud}")
    private String PINECONE_CLOUD;

    @Value("${embedding-store.pinecone.region}")
    private String PINECONE_REGION;

    @Resource
    private EmbeddingModel embeddingModel;

    @Bean
    public EmbeddingStore<TextSegment> getEmbeddingStore() {

        EmbeddingStore<TextSegment> embeddingStore = PineconeEmbeddingStore.builder()
                .apiKey(PINECONE_API_KEY)
                .index(PINECONE_INDEX_NAME)
                .nameSpace("RAG")
                .createIndex(PineconeServerlessIndexConfig.builder()
                        .cloud(PINECONE_CLOUD)
                        .region(PINECONE_REGION)
                        .dimension(embeddingModel.dimension())
                        .build())
                .build();
        return embeddingStore;
//        return new InMemoryEmbeddingStore<>(); // 默认内存实现

    }
}
