package com.htaste.aihelper.ai.rag;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author Haoting Qin
 * @date 2025/9/16
 */
@Configuration
public class RagConfig {
    @Resource(name = "qwenEmbeddingModel")
    private EmbeddingModel qwenEmbeddingModel;

    @Resource
    private EmbeddingStore<TextSegment> embeddingStore;

    @Bean
    public ContentRetriever contentRetriever() {
        // ----- RAG -----
//        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        // 1 索引
        // 1.1 加载文档
        List<Document> documents = FileSystemDocumentLoader.loadDocuments("src/main/resources/docs", new ApacheTikaDocumentParser());
        // 1.2 切分文档，按段落切分，每个段落最大1000字符，可重叠200字符
        DocumentByParagraphSplitter paragraphSplitter = new DocumentByParagraphSplitter(1000, 200);
        // 1.3. 自定义文档加载器，把向量数据存储到向量数据库中
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(paragraphSplitter) // 文档切割
                // 为了提高文档切片质量，对每个切片都增加文档元信息（文件名）
                .textSegmentTransformer(textSegment -> TextSegment.from(
                        textSegment.metadata().getString("file_name") + "\n" + textSegment.text(),
                            textSegment.metadata()
                ))
                .embeddingModel(qwenEmbeddingModel) // EmbeddingModel
                .embeddingStore(embeddingStore) // Embedding存储
                .build();
        ingestor.ingest(documents); // 加载文档

        // 2 检索
        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder() // 自定义内容检索器
                .embeddingStore(embeddingStore)
                .embeddingModel(qwenEmbeddingModel)
                .maxResults(5) // 最多返回5条结果
                .minScore(0.75) // 最低相似度分数
                .build();
        return contentRetriever;
    }
}
