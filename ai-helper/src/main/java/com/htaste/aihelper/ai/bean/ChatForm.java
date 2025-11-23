package com.htaste.aihelper.ai.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatForm {
    private String memoryId;

    private String message;
}
