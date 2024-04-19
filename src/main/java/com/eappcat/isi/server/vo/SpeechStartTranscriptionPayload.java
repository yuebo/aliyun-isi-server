package com.eappcat.isi.server.vo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class SpeechStartTranscriptionPayload extends SpeechEmptyPayload {
    @JSONField(name = "sample_rate")
    private Integer sampleRate;
    private String format;
    @JSONField(name = "enable_intermediate_result")
    private Boolean enableIntermediateResult;
    @JSONField(name = "enable_inverse_text_normalization")
    private Boolean enableInverseTextNormalization;
    @JSONField(name = "enable_punctuation_prediction")
    private Boolean enablePunctuationPrediction;
}
