package com.eappcat.isi.server.vo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class SpeechSentenceEndPayload extends SpeechEmptyPayload{
    private Integer index;
    private Integer time;
    private String result;
    private Double confidence;
    private Integer status;
    @JSONField(name = "sentence_id")
    private String sentenceId;
    private String gender;
    @JSONField(name = "audio_extra_info")
    private String audioExtraInfo;
}
