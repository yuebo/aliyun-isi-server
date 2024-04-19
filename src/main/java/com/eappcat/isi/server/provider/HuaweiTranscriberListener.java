package com.eappcat.isi.server.provider;

import com.alibaba.fastjson.JSON;
import com.huawei.sis.bean.RasrListener;
import com.huawei.sis.bean.base.RasrSentence;
import com.huawei.sis.bean.response.RasrResponse;
import com.huawei.sis.bean.response.StateResponse;
import com.eappcat.isi.server.vo.*;
import lombok.SneakyThrows;
import org.springframework.web.socket.TextMessage;

import java.io.IOException;
import java.util.List;

public class HuaweiTranscriberListener implements RasrListener {
    private SpeechSession session = null;
    private SpeechMessageHeader header = null;
    public HuaweiTranscriberListener(SpeechSession session,SpeechMessageHeader header){
        this.session = session;
        this.header = header;
    }
    @Override
    @SneakyThrows
    public void onTranscriptionResponse(RasrResponse rasrResponse) {
        List<RasrSentence> list = rasrResponse.getSentenceList();
        for (RasrSentence sentence:list){
            onSentenceBegin(rasrResponse, sentence);
            onSentenceEnd(rasrResponse, sentence);
        }

    }

    private void onSentenceEnd(RasrResponse rasrResponse, RasrSentence sentence) throws IOException {
        SpeechMessage<SpeechSentenceEndPayload> response = new SpeechMessage<>();
        SpeechSentenceEndPayload body = new SpeechSentenceEndPayload();
        body.setIndex(session.getCurrentSentence());
        body.setTime(sentence.getEndTime() - sentence.getStartTime());
        body.setResult(sentence.getResult().getText());
        body.setConfidence((double) sentence.getResult().getScore());
        body.setGender("");
        body.setAudioExtraInfo("");
        body.setSentenceId(String.valueOf(session.getCurrentSentence()));
        response.setPayload(body);
        response.setHeader(SpeechMessageHeader.success(this.header.getTaskId(), "SentenceEnd", this.header.getNamespace()));
        session.getSession().sendMessage(new TextMessage(JSON.toJSONString(response)));
        session.increment();
    }

    private void onSentenceBegin(RasrResponse rasrResponse, RasrSentence sentence) throws IOException {
        SpeechMessage<SpeechSentenceBeginPayload> response = new SpeechMessage<>();
        SpeechSentenceBeginPayload body = new SpeechSentenceBeginPayload();
        body.setIndex(session.getCurrentSentence());
        body.setTime(0);
        response.setPayload(body);
        response.setHeader(SpeechMessageHeader.success(this.header.getTaskId(), "SentenceBegin", this.header.getNamespace()));
        session.getSession().sendMessage(new TextMessage(JSON.toJSONString(response)));
    }

    @Override
    @SneakyThrows
    public void onTranscriptionBegin(StateResponse stateResponse) {
        SpeechMessage<SpeechEmptyPayload> response = new SpeechMessage<>();
        response.setHeader(SpeechMessageHeader.success(this.header.getTaskId(), "TranscriptionStarted",this.header.getNamespace()));
        session.getSession().sendMessage(new TextMessage(JSON.toJSONString(response)));
    }

    @Override
    @SneakyThrows
    public void onSTranscriptionEnd(StateResponse stateResponse) {
        SpeechMessage<SpeechEmptyPayload> response = new SpeechMessage<>();
        response.setHeader(SpeechMessageHeader.success(this.header.getTaskId(), "TranscriptionCompleted",this.header.getNamespace()));
        session.getSession().sendMessage(new TextMessage(JSON.toJSONString(response)));
    }

    @Override
    public void onTranscriptionFail(StateResponse stateResponse) {

    }
}
