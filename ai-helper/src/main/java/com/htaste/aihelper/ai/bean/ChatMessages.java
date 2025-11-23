package com.htaste.aihelper.ai.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 聊天记录持久化对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("chat_messages")
public class ChatMessages {

    /**
     * 唯一标识，映射到MongoDB文档的_id字段
     */
//    @MongoId
//    private Long id;

    /**
     * 会话ID，唯一表示会话
     */
    private String memoryId;

    /**
     * 聊天记录列表的json字符串
     */
    private String content;
}
