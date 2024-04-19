package com.eappcat.isi.server.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@Data
public class SpeechSession {
    private WebSocketSession session;
    private Map<String,Object> data=new HashMap<>();
    private boolean binaryReceived = false;
    private int currentSentence = 1;
    private SpeechMessage<SpeechStartTranscriptionPayload> initializedMessage;
    public void increment() { this.currentSentence ++ ;}
}
