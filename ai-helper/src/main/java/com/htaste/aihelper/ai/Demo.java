package com.htaste.aihelper.ai;

import com.htaste.aihelper.ai.memory.MessageConversationSummaryMemory;
import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;

public class Demo {
    public static void main(String[] args) {
        ChatModel summarizer = QwenChatModel.builder()
                .apiKey("sk-8ed216e03162445b9e14dc64ce46979f")
                .modelName("qwen-max") // 或其他模型
                .temperature(0.2f)
                .build();

        MessageConversationSummaryMemory memory = MessageConversationSummaryMemory.builder()
                .id("demo-001")
                .summarizer(summarizer)
                .summarizeEveryNMessages(10)
                .keepRecentMessages(6)
                .maxWindowMessages(40)
                .build();

        // 按你现有的对话流程，将 User/Ai/System 消息不断 memory.add(...)
        // 每累计10条消息，将自动总结并重整记忆。

        // 3) 连续写入 12 条消息（用户/助手交替），第 10 条时会触发总结
        addAndLog(memory, UserMessage.from("我们来规划一个上海周末行程"));
        addAndLog(memory, AiMessage.from("好的，有没有预算、交通方式或兴趣偏好？"));
        addAndLog(memory, UserMessage.from("预算控制在2000元内，公共交通优先，喜欢博物馆和美食。"));
        addAndLog(memory, AiMessage.from("收到：预算2000、地铁优先、博物馆+美食。是否需要亲子友好？"));
        addAndLog(memory, UserMessage.from("不需要亲子友好，2位成人。"));
        addAndLog(memory, AiMessage.from("建议住静安寺附近，出行方便。"));
        addAndLog(memory, UserMessage.from("第一天想去自然博物馆，晚上去武康路附近走走。"));
        addAndLog(memory, AiMessage.from("可以，白天博物馆+晚间安福路/武康路街区。"));
        addAndLog(memory, UserMessage.from("第二天安排城隍庙小吃和浦东江景。"));
        addAndLog(memory, AiMessage.from("OK，需不需要我帮你查高铁票？")); // 第10条，触发总结

        addAndLog(memory, UserMessage.from("需要，从杭州出发，周六9点前到上海。"));
        addAndLog(memory, AiMessage.from("明白，我会查询7-8点时段的车次并预估票价。"));

        // 4) 打印摘要与窗口中的消息
        System.out.println("\n=== 摘要是否已生成 ===");
        System.out.println(memory.currentSummary() != null);

        System.out.println("\n=== 当前摘要内容（模拟） ===");
        System.out.println(memory.currentSummary());

        System.out.println("\n=== 当前记忆窗口（类型 -> 内容） ===");
        int i = 1;
        for (ChatMessage m : memory.messages()) {
            System.out.printf("%02d. %-14s %s%n",
                    i++, m.getClass().getSimpleName(), render(m));
        }
        System.out.println("\n窗口消息总数: " + memory.messages().size());
    }

    private static void addAndLog(MessageConversationSummaryMemory memory, ChatMessage msg) {
        boolean before = memory.currentSummary() != null;
        memory.add(msg);
        boolean after = memory.currentSummary() != null;
        if (!before && after) {
            System.out.println(">>> 触发一次摘要（累计消息达到 10 条）");
        }
    }

    private static String render(ChatMessage m) {
        if (m instanceof UserMessage) return "用户: " + ((UserMessage) m).singleText();
        if (m instanceof AiMessage) return "助手: " + ((AiMessage) m).text();
        if (m instanceof SystemMessage) return "系统: " + ((SystemMessage) m).text();
        return m.toString();
    }
}