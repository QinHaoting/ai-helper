package com.htaste.aihelper.ai.memory;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 每累计 N 条消息触发一次总结，并将对话摘要以 SystemMessage 注入。
 * 基于 MessageWindowChatMemory 的窗口淘汰策略。
 */
public class MessageConversationSummaryMemory implements ChatMemory {

    private final MessageWindowChatMemory window;
    private final ChatModel summarizer;

    private final int summarizeEveryNMessages;
    private final int keepRecentMessages;

    private final List<ChatMessage> bufferSinceLastSummary = new ArrayList<>();
    private String runningSummary;

    private final Object lock = new Object();

    private MessageConversationSummaryMemory(Builder builder) {
        this.summarizer = builder.summarizer;
        this.summarizeEveryNMessages = builder.summarizeEveryNMessages;
        this.keepRecentMessages = builder.keepRecentMessages;
        this.window = MessageWindowChatMemory.builder()
                .id(builder.id != null ? builder.id : UUID.randomUUID().toString())
                .maxMessages(builder.maxWindowMessages)
                .chatMemoryStore(builder.chatMemoryStore)
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public Object id() {
        return window.id();
    }

    @Override
    public List<ChatMessage> messages() {
        return window.messages();
    }

    @Override
    public void add(ChatMessage message) {
        synchronized (lock) {
            window.add(message);
            bufferSinceLastSummary.add(message);

            if (bufferSinceLastSummary.size() >= summarizeEveryNMessages) {
                summarizeAndRebase();
            }
        }
    }

    @Override
    public void clear() {
        synchronized (lock) {
            window.clear();
            bufferSinceLastSummary.clear();
            runningSummary = null;
        }
    }

    public String currentSummary() {
        synchronized (lock) {
            return runningSummary;
        }
    }

    private void summarizeAndRebase() {
        String prompt = buildSummarizationPrompt();
        String newSummary;
        try {
            // TODO
            System.out.println("-----开始总结摘要-----");
            newSummary = summarizer.chat(prompt);
            if (newSummary != null) newSummary = newSummary.trim();
        } catch (Exception e) {
            // 总结失败则跳过，不影响正常对话
            return;
        }
        if (newSummary == null || newSummary.isEmpty()) {
            return;
        }

        runningSummary = newSummary;
        // TODO
        System.out.println("生成的摘要为：" + runningSummary);

        // 仅保留最近若干条“原始消息”以维持近因
        int fromIndex = Math.max(bufferSinceLastSummary.size() - keepRecentMessages, 0);
        List<ChatMessage> tail = new ArrayList<>(bufferSinceLastSummary.subList(fromIndex, bufferSinceLastSummary.size()));

        // 用摘要 + 近因重建窗口
        window.clear();
        window.add(AiMessage.from("对话摘要（自动生成，供模型参考）：\n" + runningSummary));
        for (ChatMessage m : tail) {
            window.add(m);
        }

        bufferSinceLastSummary.clear();
        bufferSinceLastSummary.addAll(tail);
    }

    private String buildSummarizationPrompt() {
        StringBuilder sb = new StringBuilder();
        sb.append("你是对话总结助手。将下面这段对话压缩成简洁、时序清晰的中文摘要：\n")
                .append("- 合并到已有摘要（若有）中，避免重复；\n")
                .append("- 列出关键目标、已知事实、偏好、决策、未决问题与下一步；\n")
                .append("- 保留关键信息与结论；\n")
                .append("- 控制在120~200字；\n")
                .append("只输出更新后的摘要文本。\n\n");

        if (runningSummary != null && !runningSummary.isEmpty()) {
            sb.append("已有摘要：\n").append(runningSummary).append("\n\n");
        } else {
            sb.append("已有摘要：无\n\n");
        }

        sb.append("新增对话片段（按时间顺序）：\n");
        for (ChatMessage m : bufferSinceLastSummary) {
            sb.append(render(m)).append("\n");
        }

        return sb.toString();
    }

    private static String render(ChatMessage m) {
        if (m instanceof UserMessage) {
            return "用户: " + ((UserMessage) m).singleText();
        } else if (m instanceof AiMessage) {
            return "助手: " + ((AiMessage) m).text();
        } else if (m instanceof SystemMessage) {
            return "系统: " + ((SystemMessage) m).text();
        } else {
            return m.toString();
        }
    }

    public static final class Builder {
        private Object id;
        private ChatModel summarizer;
        private int summarizeEveryNMessages = 10; // 默认每10条消息总结
        private int keepRecentMessages = 6;       // 总结后保留近因消息条数
        private int maxWindowMessages = 40;       // 窗口最大消息条数
        private ChatMemoryStore chatMemoryStore;

        public Builder id(Object id) {
            this.id = id;
            return this;
        }

        public Builder summarizer(ChatModel summarizer) {
            this.summarizer = summarizer;
            return this;
        }

        public Builder summarizeEveryNMessages(int n) {
            this.summarizeEveryNMessages = Math.max(1, n);
            return this;
        }

        public Builder keepRecentMessages(int k) {
            this.keepRecentMessages = Math.max(0, k);
            return this;
        }

        public Builder maxWindowMessages(int k) {
            this.maxWindowMessages = Math.max(1, k);
            return this;
        }

        public Builder chatMemoryStore(ChatMemoryStore chatMemoryStore) {
            this.chatMemoryStore = chatMemoryStore;
            return this;
        }

        public MessageConversationSummaryMemory build() {
            if (summarizer == null) {
                throw new IllegalArgumentException("summarizer (ChatModel) must not be null");
            }
            if (keepRecentMessages >= maxWindowMessages) {
                // 防止重建窗口时，摘要 + 近因超过窗口
                maxWindowMessages = Math.max(keepRecentMessages + 4, maxWindowMessages);
            }
            return new MessageConversationSummaryMemory(this);
        }
    }
}