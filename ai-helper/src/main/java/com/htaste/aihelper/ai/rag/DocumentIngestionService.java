package com.htaste.aihelper.ai.rag;


import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * @author Haoting Qin
 * @version 1.0
 * @date 2025-10-11
 */
@Slf4j
@Service
public class DocumentIngestionService {

    @Resource
    private EmbeddingModel embeddingModel;

    @Resource
    private EmbeddingStore<TextSegment> embeddingStore;

    // String path = "src/main/resources/docs";
    public void ingestDocuments(String folderPath) {

        // 索引
        // 1 加载原始文档
        List<Document> documents = FileSystemDocumentLoader.loadDocuments(folderPath, new ApacheTikaDocumentParser());
        // 2 按段落切分文档，每个段落最大1000字符，可重叠200字符
        DocumentByParagraphSplitter paragraphSplitter = new DocumentByParagraphSplitter(1000, 200);
        // 3 构建嵌入入库逻辑
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(paragraphSplitter) // 文档切割
                // 为了提高文档切片质量，对每个切片都增加文档元信息（文件名）
                .textSegmentTransformer(textSegment -> TextSegment.from(
                        textSegment.metadata().getString("file_name") + "\n" + textSegment.text(),
                        textSegment.metadata()
                ))
                .embeddingModel(embeddingModel) // EmbeddingModel
                .embeddingStore(embeddingStore) // Embedding存储
                .build();

        // 4 执行embedding
        ingestor.ingest(documents);

        log.info("文档向量化完成并已存入 embeddingStore");
    }
}
