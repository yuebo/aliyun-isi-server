package com.eappcat.isi.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.eappcat.isi.server.provider.SpeechProvider;
import com.eappcat.isi.server.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SpeechWebsocketHandler extends AbstractWebSocketHandler {

    private Map<String, SpeechSession> sessionMap = new ConcurrentHashMap<>();
    @Autowired
    private SpeechProvider provider;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        SpeechSession speechSession = new SpeechSession();
        speechSession.setSession(session);
        this.sessionMap.put(session.getId(), speechSession);
        provider.onConnected(speechSession);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        provider.onDisConnected(this.sessionMap.get(session.getId()));
        this.sessionMap.remove(session.getId());

    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info("text message: {}",message.getPayload());
        SpeechSession speechSession = this.sessionMap.get(session.getId());

        JSONObject jsonObject = JSON.parseObject(message.getPayload());
        String name = jsonObject.getJSONObject("header").getString("name");
        switch (name){
            case "StartTranscription":
                provider.onStartTranscription(speechSession, jsonObject.toJavaObject(new TypeReference<SpeechMessage<SpeechStartTranscriptionPayload>>() {}));
                break;
            case "StopTranscription":
                provider.onStopTranscription(speechSession, jsonObject.toJavaObject(new TypeReference<SpeechMessage<SpeechEmptyPayload>>() {}));
                break;
            case "RunSynthesis":
                provider.onRunSynthesis(speechSession, jsonObject.toJavaObject(new TypeReference<SpeechMessage<SpeechRunSynthesisPayload>>() {}));
                break;
            case "StartSynthesis":
                provider.onStartSynthesis(speechSession, jsonObject.toJavaObject(new TypeReference<SpeechMessage<SpeechStartSynthesisPayload>>() {}));
                break;
            case "StopSynthesis":
                provider.onStopSynthesis(speechSession, jsonObject.toJavaObject(new TypeReference<SpeechMessage<SpeechEmptyPayload>>() {}));
                break;
            default:
                log.error("unknown message: {}",message.getPayload());
        }
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        log.info("binary message: {}",message.getPayload());
        SpeechSession speechSession = this.sessionMap.get(session.getId());
        provider.onBinaryMessage(speechSession,message.getPayload());
    }

}
