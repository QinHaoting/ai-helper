package com.htaste.aihelper.ai;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * @author Haoting Qin
 * @date 2025/9/11
 */
public interface AIHelperService {

//    @SystemMessage(fromResource = "static/system-promt.txt")
//    String chat(String userMessage);

    @SystemMessage(fromResource = "static/system-promt.txt")
    String chatWithMemory(@MemoryId int memoryId, @UserMessage String message);
}
