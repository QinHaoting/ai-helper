package com.htaste.aihelper.ai;

import com.htaste.aihelper.ai.store.MongoChatMemoryStore;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Haoting Qin
 * @date 2025/9/11
 */
@Configuration
public class AIHelperServiceFactory {
    @Resource(name = "chatModel")
    private ChatModel qwenChatModel;

    @Resource(name = "summaryChatModel")
    private ChatModel summaryChatModel;

    @Resource
    private ContentRetriever contentRetriever;

    @Autowired
    private MongoChatMemoryStore mongoChatMemoryStore;

    @Resource(name = "streamingChatModel")
    private StreamingChatModel streamingChatModel;

    @Bean
    public AIHelperService aiHelperService() {
        return AiServices.builder(AIHelperService.class)
//                .chatModel(qwenChatModel)
                .streamingChatModel(streamingChatModel) // 流式输出
//                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(10)) // 自带窗口会话记忆

                // 会话记忆摘要
                .chatMemoryProvider(memoryId ->
                        MessageConversationSummaryMemory.builder()
                        .id(memoryId)
                        .summarizer(summaryChatModel)
                        .summarizeEveryNMessages(15) // 15条消息总结摘要
                        .keepRecentMessages(6)
                        .maxWindowMessages(40)
                        .chatMemoryStore(mongoChatMemoryStore) // 记忆持久化
                        .build())

//                .contentRetriever(contentRetriever) // RAG
                .build();
    }
}
