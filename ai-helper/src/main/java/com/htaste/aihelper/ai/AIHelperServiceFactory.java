package com.htaste.aihelper.ai;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
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

    @Bean
    public AIHelperService aiHelperService() {
        return AiServices.builder(AIHelperService.class)
                .chatModel(qwenChatModel)
//                .chatMemory(memory)
//                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(10)) // 自带窗口会话记忆

                // 会话记忆摘要
                .chatMemoryProvider(memoryId ->
                        MessageConversationSummaryMemory.builder()
                        .id(memoryId)
                        .summarizer(summaryChatModel)
                        .summarizeEveryNMessages(15) // 15条消息总结摘要
                        .keepRecentMessages(6)
                        .maxWindowMessages(40)
                        .build())
                .build();
    }
}
