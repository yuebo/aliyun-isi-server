package com.eappcat.isi.server.vo;

import lombok.Data;

@Data
public class SpeechRunSynthesisPayload extends SpeechEmptyPayload{
    private String text;
}
