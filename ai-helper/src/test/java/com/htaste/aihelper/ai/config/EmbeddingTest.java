package com.htaste.aihelper.ai.config;

import com.htaste.aihelper.AiHelperApplication;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.w3c.dom.Text;

@SpringBootTest(classes = AiHelperApplication.class)
public class EmbeddingTest {

    @Resource(name = "qwenEmbeddingModel")
    private EmbeddingModel embeddingModel;

    @Autowired
    private EmbeddingStore<TextSegment> embeddingStore;

    @Test
    public void testEmbeddingModel() {
        String message = "你好";
        Response<Embedding> embed = embeddingModel.embed(message);

        System.out.println(embed.metadata());
        System.out.println("向量维度:" + embed.content().dimension());
        System.out.println("向量输出:" + embed.toString());
    }

    @Test
    public void testEmbeddingStore() {
        String chunk1 = "小明喜欢打篮球";
        TextSegment segment1 = TextSegment.from(chunk1);
        Embedding embedding1 = embeddingModel.embed(segment1).content();
//        System.out.println(embedding1.dimension());
        embeddingStore.add(embedding1, segment1);

        String chunk2 = "小明喜欢吃苹果";
        TextSegment segment2 = TextSegment.from(chunk2);
        Embedding embedding2 = embeddingModel.embed(segment2).content();
        embeddingStore.add(embedding2, segment2);
    }

    @Test
    public void testEmbeddingStoreSearch() {
        String userQuery = "小明喜欢什么运动？";
        Embedding embedding = embeddingModel.embed(userQuery).content();


        // 构建查询请求
        EmbeddingSearchRequest searchRequest = EmbeddingSearchRequest.builder()
                .queryEmbedding(embedding)
                .maxResults(10)
//                .minScore(0.80)
                .build();

        // 在向量数据库中查询
        EmbeddingSearchResult<TextSegment> searchResult = embeddingStore.search(searchRequest);

        for (EmbeddingMatch<TextSegment> embeddingMatch: searchResult.matches()) {
            System.out.println(embeddingMatch.embedded() + ", " + embeddingMatch.score());
        }

//        System.out.println(embeddingMatch.score());
    }
}
