package com.htaste.aihelper.store;

import com.htaste.aihelper.ai.bean.ChatMessages;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

@SpringBootTest
public class MongoDBCrudTest {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    void testInsert() {
        ChatMessages chatMessages = new ChatMessages();
        chatMessages.setMemoryId("user-1");
        chatMessages.setContent("聊天记录列表2");
        mongoTemplate.insert(chatMessages);
    }

    /**
     * 测试条件查询
     */
    @Test
    void testFind() {
        Criteria criteria = Criteria.where("memoryId").is("user-1");
        Query query = Query.query(criteria);
        List<ChatMessages> chatMessagesList = mongoTemplate.find(query, ChatMessages.class);
        for (ChatMessages chatMessages : chatMessagesList) {
            System.out.println(chatMessages);
        }
    }

    /**
     * 测试全表查询
     */
    @Test
    void testFindALL() {
        Iterable<ChatMessages> chatMessages = mongoTemplate.findAll(ChatMessages.class);
        for (ChatMessages chatMessage : chatMessages) {
            System.out.println(chatMessage);
        }
    }

    /**
     * 测试更新
     */
    @Test
    void testUpdate() {
        Criteria criteria = Criteria.where("memoryId").is("user-1");
        Query query = Query.query(criteria);
        ChatMessages chatMessages = mongoTemplate.findOne(query, ChatMessages.class);
        if (chatMessages == null) {
            return;
        }
        Update update = new Update();
        update.set("content", "更新聊天记录列表2");
        mongoTemplate.upsert(query, update, ChatMessages.class);
    }

    @Test
    void testDelete() {
        Criteria criteria = Criteria.where("memoryId").is("user-2");
        Query query = Query.query(criteria);
        mongoTemplate.remove(query, ChatMessages.class);
    }
}
