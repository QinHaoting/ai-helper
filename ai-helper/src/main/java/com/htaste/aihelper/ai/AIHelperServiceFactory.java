package com.htaste.aihelper.ai;

import com.htaste.aihelper.ai.memory.MessageConversationSummaryMemory;
import com.htaste.aihelper.ai.store.MongoChatMemoryStore;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.rag.RetrievalAugmentor;
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
//    @Resource(name = "chatModel")
//    private ChatModel qwenChatModel;

    @Resource(name = "streamingChatModel")
    private StreamingChatModel streamingChatModel;

    @Resource(name = "summaryChatModel")
    private ChatModel summaryChatModel;

    @Resource
    private RetrievalAugmentor retrievalAugmentor;

    @Autowired
    private MongoChatMemoryStore mongoChatMemoryStore;



    @Bean
    public AIHelperService aiHelperService() {
        return AiServices.builder(AIHelperService.class)
//                .chatModel(qwenChatModel)
                .streamingChatModel(streamingChatModel) // 流式输出

//                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(10)) // 自带窗口会话记忆
                // 会话记忆摘要
//                .chatMemoryProvider(chatMemoryProvider)
                .chatMemoryProvider(memoryId ->
                        MessageConversationSummaryMemory.builder()
                        .id(memoryId)
                        .summarizer(summaryChatModel)
                        .summarizeEveryNMessages(15) // 15条消息总结摘要
                        .keepRecentMessages(6)
                        .maxWindowMessages(40)
                        .chatMemoryStore(mongoChatMemoryStore) // 记忆持久化
                        .build())

//                .contentRetriever(contentRetriever) // 简易RAG
               .retrievalAugmentor(retrievalAugmentor) // TODO 检索增强->查询重写+多查询扩展
                .build();
    }

    /**
     * TODO 将 ChatMemoryProvider 也定义为一个 Bean，使其可重用和清晰。
     */
//    @Bean
//    public ChatMemoryProvider chatMemoryProvider() {
//        return memoryId ->
//                MessageConversationSummaryMemory.builder()
//                        .id(memoryId)
//                        .summarizer(summaryChatModel)
//                        .summarizeEveryNMessages(15)
//                        .keepRecentMessages(6)
//                        .maxWindowMessages(40)
//                        .chatMemoryStore(mongoChatMemoryStore)
//                        .build();
//    }
}
