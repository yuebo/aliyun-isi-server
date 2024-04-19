package com.eappcat.isi.server.provider;

import com.alibaba.fastjson.JSON;
import com.alibaba.nls.client.protocol.asr.SpeechTranscriberListener;
import com.alibaba.nls.client.protocol.asr.SpeechTranscriberResponse;
import com.eappcat.isi.server.vo.SpeechSession;
import lombok.SneakyThrows;
import org.springframework.web.socket.TextMessage;

public class AliyunSpeechTranscriberListener extends SpeechTranscriberListener {
    private SpeechSession session = null;
    public AliyunSpeechTranscriberListener(SpeechSession session){
        this.session = session;
    }
    @Override
    @SneakyThrows
    public void onTranscriberStart(SpeechTranscriberResponse response) {
        this.session.getSession().sendMessage(new TextMessage(JSON.toJSONString(response)));
    }

    @Override
    @SneakyThrows
    public void onSentenceBegin(SpeechTranscriberResponse response) {
        this.session.getSession().sendMessage(new TextMessage(JSON.toJSONString(response)));

    }

    @Override
    @SneakyThrows
    public void onSentenceEnd(SpeechTranscriberResponse response) {
        this.session.getSession().sendMessage(new TextMessage(JSON.toJSONString(response)));
    }

    @Override
    @SneakyThrows

    public void onTranscriptionResultChange(SpeechTranscriberResponse response) {
        this.session.getSession().sendMessage(new TextMessage(JSON.toJSONString(response)));
    }

    @Override
    @SneakyThrows
    public void onTranscriptionComplete(SpeechTranscriberResponse response) {
        this.session.getSession().sendMessage(new TextMessage(JSON.toJSONString(response)));

    }
    @Override
    @SneakyThrows
    public void onFail(SpeechTranscriberResponse response) {
        this.session.getSession().sendMessage(new TextMessage(JSON.toJSONString(response)));
    }
}
