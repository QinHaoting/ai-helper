package com.htaste.aihelper;

import com.htaste.aihelper.ai.AiHelper;
import com.htaste.aihelper.util.ImageHelper;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.io.IOException;

@SpringBootTest
class AiHelperApplicationTests {

    @Autowired
    private AiHelper aiHelper;

    @Test
    void chat() {
        String message = "你好，我是小明";
        aiHelper.chat(message);
    }

    @Value("src/main/resources/static/水上列车.jpg")
    private Resource resource;


    @Test
    void chatWithMultiModal() throws IOException {
        String imageUrl = "static/水上列车.jpg";
        String imageData = ImageHelper.getImageAsBase64(imageUrl);
        UserMessage userMessage = UserMessage.from(
                TextContent.from("描述图片"),
                ImageContent.from(imageData, "image/jpg")
        );
        aiHelper.chatWithMultiModal(userMessage);
    }
}
