package com.htaste.aihelper.ai;

import com.htaste.aihelper.AiHelperApplication;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


/**
 * @author Haoting Qin
 * @date 2025/9/11
 */
@SpringBootTest(classes = AiHelperApplication.class)
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
    void chatWithImage() {
        String userMessage = "你好，我想咨询我的物流订单";

        String result = aiHelperService.chatWithImage(userMessage);
        System.out.println(result);
    }

    @Test
    void chatWithMemory() {
        String user1Message = "你好，我是小明，想咨询我的物流订单";
        String result1 = aiHelperService.chatWithMemory("user-1", user1Message);
        System.out.println("小明的故事:");
        System.out.println("小明：" + user1Message);
        System.out.println("助手：" + result1);

        String user2Message = "我是小红，想查看我的物流订单，并准备退掉2025年9月11日的订单";
        String result2 = aiHelperService.chatWithMemory("user-2", user2Message);
        System.out.println("小红的故事:");
        System.out.println("小红：" + user2Message);
        System.out.println("助手：" + result2);

        String userMessage = "我是谁？我刚才让你干什么";
        result1 = aiHelperService.chatWithMemory("user-1", userMessage);
        result2 = aiHelperService.chatWithMemory("user-2", userMessage);
        System.out.println("小明的故事" + result1);
        System.out.println("----------------");
        System.out.println("小红的故事" + result2);
        System.out.println("----------------");
    }


    @Test
    void chatWithMemory2() {
        String user1Message = "你好，我是小明";
        System.out.println("[ User ]: " + user1Message);
        String result1 = aiHelperService.chatWithMemory("user-1", user1Message);
        System.out.println("[  AI  ]: " + result1);

        user1Message = "我们来规划一个上海周末行程";
        System.out.println("[ User ]: " + user1Message);
        result1 = aiHelperService.chatWithMemory("user-1", user1Message);
        System.out.println("[  AI  ]: " + result1);

        user1Message = "预算控制在2000元内，公共交通优先，喜欢博物馆和美食。";
        System.out.println("[ User ]: " + user1Message);
        result1 = aiHelperService.chatWithMemory("user-1", user1Message);
        System.out.println("[  AI  ]: " + result1);


        // 小红
        String user2Message = "我是小红，帮我计算1+1=？";
        System.out.println("[ User ]: " + user2Message);
        String result2 = aiHelperService.chatWithMemory("user-2", user2Message);
        System.out.println("[  AI  ]: " + result2);

        user1Message = "不需要亲子友好，2位成人。";
        System.out.println("[ User ]: " + user1Message);
        result1 = aiHelperService.chatWithMemory("user-1", user1Message);
        System.out.println("[  AI  ]: " + result1);

        user1Message = "第一天想去自然博物馆，晚上去武康路附近走走。";
        System.out.println("[ User ]: " + user1Message);
        result1 = aiHelperService.chatWithMemory("user-1", user1Message);
        System.out.println("[  AI  ]: " + result1);

        user1Message = "第二天安排城隍庙小吃和浦东江景。";
        System.out.println("[ User ]: " + user1Message);
        result1 = aiHelperService.chatWithMemory("user-1", user1Message);
        System.out.println("[  AI  ]: " + result1);

        user1Message = "给出最终生成的计划";
        System.out.println("[ User ]: " + user1Message);
        result1 = aiHelperService.chatWithMemory("user-1", user1Message);
        System.out.println("[  AI  ]: " + result1);


        System.out.println("--------------//------------------");


        // 小红
        user2Message = "根据计算结果，再加上2等于多少？";
        System.out.println("[ User ]: " + user2Message);
        result2 = aiHelperService.chatWithMemory("user-2", user2Message);
        System.out.println("[  AI  ]: " + result2);

    }


    @Test
    void chatWithRAG() {
        String userMessage = "怎么退款？给出相关的步骤";
        String result = aiHelperService.chat(userMessage);
        System.out.println(result);
    }
}