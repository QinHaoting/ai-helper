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
    @Resource
    private ChatModel qwenChatModel;

    @Bean
    public AIHelperService aiHelperService() {
        return AiServices.builder(AIHelperService.class)
                .chatModel(qwenChatModel)
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(10)) // 会话记忆
                .build();
    }
}
