package com.eappcat.isi.server.vo;

import lombok.Data;

@Data
public class SpeechSentenceBeginPayload extends SpeechEmptyPayload{
    private Integer index;
    private Integer time;
}
