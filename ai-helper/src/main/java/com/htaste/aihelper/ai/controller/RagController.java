package com.htaste.aihelper.ai.controller;


import com.htaste.aihelper.ai.rag.DocumentIngestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rag")
public class RagController {
    @Autowired
    private DocumentIngestionService ingestionService;

    /**
     * TODO
     * Embedding 文件夹/文件
     * @param path 文件夹/文件路径
     * s'd@return
     */
    @PostMapping("/ingest")
    public String rebuildIndex(@RequestParam String path) {
        ingestionService.ingestDocuments(path);
        return "✅ 索引构建完成！";
    }
}
