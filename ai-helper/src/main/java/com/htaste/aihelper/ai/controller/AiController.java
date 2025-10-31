package com.htaste.aihelper.ai.controller;

import com.htaste.aihelper.ai.AIHelperService;
import com.htaste.aihelper.ai.bean.ChatForm;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/ai")
public class AiController {
    @Resource
    private AIHelperService aiHelperService;

    @PostMapping(value = "/chat", produces = "text/stream;charset=utf-8")
    public Flux<String> chat(@RequestBody ChatForm chatForm) {
        return aiHelperService.chatWithStream(chatForm.getMemoryId(), chatForm.getMessage());

//        return aiHelperService.chatWithStream(chatForm.getMemoryId(), chatForm.getMessage())
//                .map(chunk -> ServerSentEvent.<String>builder()
//                        .data(chunk)
//                        .build());
    }
}
