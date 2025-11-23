package com.htaste.aihelper.ai;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Haoting Qin
 * @date 2025/9/10
 */
@Service
@Slf4j
public class AiHelper {

    @Resource
    private ChatModel qwenChatModel;

    private static String SYSTEM_MESSAGE = """
            """;

    // 简单对话
    public String chat(String message) {
        UserMessage userMessage = UserMessage.from(message);
        ChatResponse chatResponse = qwenChatModel.chat(userMessage);
        AiMessage aiMessage = chatResponse.aiMessage();
        log.info("AI输出：" + aiMessage.toString());
        return aiMessage.text();
    }

    // 多模态对话
    public String chatWithMultiModal(UserMessage userMessage) {
        ChatResponse chatResponse = qwenChatModel.chat(userMessage);
        AiMessage aiMessage = chatResponse.aiMessage();
        log.info("AI输出：" + aiMessage.toString());
        return aiMessage.text();
    }
}
