package com.junsheng.XiaozhiAI.bean;

import lombok.Data;

/**
 * Data received from frontend
 */
@Data
public class ChatForm {

    private String memoryId;
    private String message;
}
