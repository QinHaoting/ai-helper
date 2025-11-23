package com.htaste.aihelper.ai.rag;

import com.htaste.aihelper.AiHelperApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = AiHelperApplication.class)
class DocumentIngestionServiceTest {

    @Autowired
    private DocumentIngestionService documentIngestionService;

    @Test
    void testIngestDocument() {
        String path = "src/main/resources/docs";
        documentIngestionService.ingestDocuments(path);
    }
}