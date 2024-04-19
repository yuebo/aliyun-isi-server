package com.eappcat.isi.server.vo;

import lombok.Data;

@Data
public class SpeechMessageContext {
    private SpeechSdk sdk;
    private SpeechNetwork network;
}
