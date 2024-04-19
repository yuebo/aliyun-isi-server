package com.eappcat.isi.server.vo;

import lombok.Data;

@Data
public class SpeechMessage<T> {
    private SpeechMessageHeader header;
    private SpeechMessageContext context;
    private T payload;
}
