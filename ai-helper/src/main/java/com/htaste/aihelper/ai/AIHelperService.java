package com.htaste.aihelper.ai;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * @author Haoting Qin
 * @date 2025/9/11
 */
public interface AIHelperService {

    @SystemMessage(fromResource = "static/system-prompt.txt")
    String chat(String userMessage);

    /**
     * 多模态 - 图片理解
     * @param userMessage
     * @return
     */
    String chatWithImage(String userMessage);
//    @SystemMessage(fromResource = "static/system-promt.txt")
//    String chat(String userMessage);

    @SystemMessage(fromResource = "static/prompt/system-prompt.txt")
    String chatWithMemory(@MemoryId String memoryId, @UserMessage String message);

    @SystemMessage(fromResource = "static/system-prompt.txt")
    String chatWithSummaryMemory(@MemoryId String memoryId, @UserMessage String message);
}
