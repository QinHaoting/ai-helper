package com.htaste.aihelper.ai;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


/**
 * @author Haoting Qin
 * @date 2025/9/11
 */
@SpringBootTest
class AIHelperServiceTest {

    @Resource
    private AIHelperService aiHelperService;

//    @Test
//    void chat() {
//        String userMessage = "你好，我想咨询我的物流订单";
//        String result = aiHelperService.chat(userMessage);
//        System.out.println(result);
//    }

    @Test
    void chatWithMemory() {
        String user1Message = "你好，我是小明，想咨询我的物流订单";
//        String result1 = aiHelperService.chatWithMemory(1, user1Message);
//        System.out.println("小明的故事:");
//        System.out.println("小明：" + user1Message);
//        System.out.println("助手：" + result1);

        String user2Message = "我是小红，想查看我的物流订单，并准备退掉2025年9月11日的订单";
//        String result2 = aiHelperService.chatWithMemory(2, user2Message);
//        System.out.println(result2);

        String userMessage = "我是谁？我刚才让你干什么";
        String result1 = aiHelperService.chatWithMemory(1, userMessage);
        String result2 = aiHelperService.chatWithMemory(2, userMessage);
        System.out.println("小明的故事" + result1);
        System.out.println("----------------");
        System.out.println("小红的故事" + result2);
        System.out.println("----------------");
    }

    @Test
    void chatWithRAG() {
        String userMessage = "怎么退款？给出相关的步骤";
        String result = aiHelperService.chat(userMessage);
        System.out.println(result);
    }
}