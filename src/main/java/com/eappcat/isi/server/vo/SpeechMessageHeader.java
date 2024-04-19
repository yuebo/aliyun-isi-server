package com.eappcat.isi.server.vo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.UUID;

@Data
public class SpeechMessageHeader {
    @JSONField(name = "session_id")
    private String sessionId;
    @JSONField(name = "task_id")
    private String taskId;
    @JSONField(name = "message_id")
    private String messageId;
    private String appkey;
    private String name;
    private String namespace;
    private Integer status;
    @JSONField(name = "status_text")
    private String statusText;
    public static SpeechMessageHeader success(String taskId,String name,String namespace){
        SpeechMessageHeader response = new SpeechMessageHeader();
        response.taskId = taskId;
        response.name = name;
        response.namespace = namespace;
        response.messageId = UUID.randomUUID().toString().replace("-","");
        response.statusText = "Gateway:SUCCESS:Success.";
        response.status = 20000000;
        return response;
    }
}
