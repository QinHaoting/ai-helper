package com.htaste.aihelper.config;

import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.community.model.dashscope.QwenEmbeddingModel;
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class LangChainConfig {

    @Value("${langchain4j.community.dashscope.chat-model.api-key}")
    private String qwen_api_key;

    @Value("${langchain4j.open-ai.chat-model.api-key}")
    private String open_ai_api_key;

    @Value("${langchain4j.open-ai.chat-model.base-url}")
    private String open_ai_base_url;

    /**
     * 定义用于多模态对话的 ChatModel
     * @return ChatLanguageModel instance for vision tasks
     */
    @Bean(name = "chatModel")
    @Primary // 将这个Bean标记为ChatLanguageModel类型的主要/默认Bean
    public ChatModel visionChatModel() {
        return QwenChatModel.builder()
                .apiKey(qwen_api_key)
                .modelName("qwen-vl-max") // 指定多模态模型
                .build();
    }

    /**
     * RAG的Query重写模型
     * @return
     */
    @Bean(name = "queryTransformModel")
    public ChatModel queryTransformModel() {
        return QwenChatModel.builder()
                .apiKey(qwen_api_key)
                .modelName("qwen-turbo")
                .build();
    }

    /**
     * 流式输出
     * @return
     */
    @Bean(name = "streamingChatModel")
    @Primary // 将这个Bean标记为ChatLanguageModel类型的主要/默认Bean
    public StreamingChatModel streamingChatModel() {
        return QwenStreamingChatModel.builder()
                .apiKey(qwen_api_key)
                .modelName("qwen-vl-max") // 指定多模态模型
                .build();
    }

    /**
     * 定义用于文本摘要的 SummaryChatModel
     * @return ChatLanguageModel instance for summary tasks
     */
    @Bean(name = "summaryChatModel")
    public ChatModel summaryChatModel() {
        return QwenChatModel.builder()
                .apiKey(qwen_api_key)
                .modelName("qwen-plus") // 指定文本大模型
                .temperature(0.2f) // 摘要任务可以设置较低的temperature以保证稳定性
                .build();

//                .baseUrl(open_ai_base_url)
//                .apiKey(open_ai_api_key)
//                .modelName("deepseek-v3.1") // 指定文本大模型
//                .temperature(0.2f) // 摘要任务可以设置较低的temperature以保证稳定性
//                .build();
    }

    /**
     * 定义用于文本转成向量的 EmbeddingModel
     * @return EmbeddingModel
     */
    @Bean(name = "qwenEmbeddingModel")
    public EmbeddingModel qwenEmbeddingModel() {
        return QwenEmbeddingModel.builder()
                .apiKey(qwen_api_key)
                .modelName("text-embedding-v3") // 指定Embedding模型
                .build();
    }
}