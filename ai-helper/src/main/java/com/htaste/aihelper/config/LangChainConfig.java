package com.htaste.aihelper.config;

import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.model.chat.ChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class LangChainConfig {

    @Value("${langchain4j.community.dashscope.chat-model.api-key}")
    private String apiKey;

    /**
     * 定义用于多模态对话的 ChatModel (qwen-vl-max)
     * @return ChatLanguageModel instance for vision tasks
     */
    @Bean(name = "chatModel")
    @Primary // 将这个Bean标记为ChatLanguageModel类型的主要/默认Bean
    public ChatModel visionChatModel() {
        return QwenChatModel.builder()
                .apiKey(apiKey)
                .modelName("qwen-vl-max") // 指定多模态模型
                .build();
    }

    /**
     * 定义用于文本摘要的 SummaryChatModel (qwen-max)
     * @return ChatLanguageModel instance for summary tasks
     */
    @Bean(name = "summaryChatModel")
    public ChatModel summaryChatModel() {
        return QwenChatModel.builder()
                .apiKey(apiKey)
                .modelName("qwen-max") // 指定文本大模型
                .temperature(0.2f) // 摘要任务可以设置较低的temperature以保证稳定性
                .build();
    }
}