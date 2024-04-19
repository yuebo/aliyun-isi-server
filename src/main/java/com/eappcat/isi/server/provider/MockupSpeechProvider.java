package com.eappcat.isi.server.provider;

import com.alibaba.fastjson.JSON;
import com.eappcat.isi.server.vo.*;
//import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;

import java.nio.ByteBuffer;

//@Component
public class MockupSpeechProvider implements SpeechProvider{
    @Override
    public void onConnected(SpeechSession session) {

    }

    @Override
    public void onDisConnected(SpeechSession speechSession) {

    }

    @Override
    public void onRunSynthesis(SpeechSession speechSession, SpeechMessage<SpeechRunSynthesisPayload> speechMessage) throws Exception {

    }

    @Override
    public void onStartSynthesis(SpeechSession speechSession, SpeechMessage<SpeechStartSynthesisPayload> speechMessage) throws Exception {

    }

    @Override
    public void onStopSynthesis(SpeechSession speechSession, SpeechMessage<SpeechEmptyPayload> speechMessage) throws Exception {

    }


    @Override
    public void onStartTranscription(SpeechSession session, SpeechMessage<SpeechStartTranscriptionPayload> message) throws Exception {
        SpeechMessage<SpeechEmptyPayload> response = new SpeechMessage<>();
        response.setHeader(SpeechMessageHeader.success(message.getHeader().getTaskId(), "TranscriptionStarted",message.getHeader().getNamespace()));
        session.setInitializedMessage(message);
        session.getSession().sendMessage(new TextMessage(JSON.toJSONString(response)));
    }

    @Override
    public void onStopTranscription(SpeechSession session, SpeechMessage<SpeechEmptyPayload> message) throws Exception {
        SpeechMessage<SpeechEmptyPayload> response = new SpeechMessage<>();
        response.setHeader(SpeechMessageHeader.success(message.getHeader().getTaskId(), "TranscriptionCompleted",message.getHeader().getNamespace()));
        session.getSession().sendMessage(new TextMessage(JSON.toJSONString(response)));
    }

    @Override
    public void onBinaryMessage(SpeechSession speechSession, ByteBuffer payload) throws Exception {
        if (!speechSession.isBinaryReceived()){
            speechSession.setBinaryReceived(true);
            SpeechMessage<SpeechSentenceBeginPayload> response = new SpeechMessage<>();
            SpeechSentenceBeginPayload body = new SpeechSentenceBeginPayload();
            body.setIndex(speechSession.getCurrentSentence());
            body.setTime(0);
            response.setPayload(body);
            response.setHeader(SpeechMessageHeader.success(speechSession.getInitializedMessage().getHeader().getTaskId(), "SentenceBegin",speechSession.getInitializedMessage().getHeader().getNamespace()));
            speechSession.getSession().sendMessage(new TextMessage(JSON.toJSONString(response)));
        }else{
            if (payload.array().length<3200){
                SpeechMessage<SpeechSentenceEndPayload> response = new SpeechMessage<>();
                SpeechSentenceEndPayload body = new SpeechSentenceEndPayload();
                body.setIndex(speechSession.getCurrentSentence());
                body.setTime(0);
                body.setResult("你好，我是添可");
                body.setConfidence(0.9d);
                body.setGender("");
                body.setAudioExtraInfo("");
                body.setSentenceId("1234");
                response.setPayload(body);
                response.setHeader(SpeechMessageHeader.success(speechSession.getInitializedMessage().getHeader().getTaskId(), "SentenceEnd",speechSession.getInitializedMessage().getHeader().getNamespace()));
                speechSession.getSession().sendMessage(new TextMessage(JSON.toJSONString(response)));
                speechSession.increment();
            }
        }
    }
}
