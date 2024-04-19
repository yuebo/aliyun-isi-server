package com.eappcat.isi.server.vo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class SpeechStartSynthesisPayload extends SpeechEmptyPayload{
    //{"payload":{"volume":50,"voice":"siyue","pitch_rate":0,"sample_rate":16000,"format":"wav","speech_rate":0}
    private Integer volume;
    private String voice;
    @JSONField(name = "pitch_rate")
    private Integer pitchRate;
    @JSONField(name = "sample_rate")
    private Integer sampleRate;
    private String format;
    @JSONField(name = "speech_rate")
    private Integer speechRate;

    private String text;
}
