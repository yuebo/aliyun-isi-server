package com.eappcat.isi.server.provider;

import com.alibaba.fastjson.JSON;
import com.alibaba.nls.client.protocol.tts.SpeechSynthesizerListener;
import com.alibaba.nls.client.protocol.tts.SpeechSynthesizerResponse;
import com.eappcat.isi.server.vo.SpeechSession;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;

import java.nio.ByteBuffer;


@Slf4j
public class AliyunSpeechSynthesizerListener extends SpeechSynthesizerListener {

    private SpeechSession session = null;
    public AliyunSpeechSynthesizerListener(SpeechSession session){
        this.session = session;
    }

    @Override
    @SneakyThrows
    public void onComplete(SpeechSynthesizerResponse response) {
        this.session.getSession().sendMessage(new TextMessage(JSON.toJSONString(response)));

    }

    @Override
    @SneakyThrows
    public void onFail(SpeechSynthesizerResponse response) {
        this.session.getSession().sendMessage(new TextMessage(JSON.toJSONString(response)));

    }

    @Override
    @SneakyThrows
    public void onMessage(ByteBuffer message) {
        this.session.getSession().sendMessage(new BinaryMessage(message,true));
    }
}
