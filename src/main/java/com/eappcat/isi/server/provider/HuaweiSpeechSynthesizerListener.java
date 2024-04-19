package com.eappcat.isi.server.provider;

import com.huawei.sis.bean.RttsListener;
import com.huawei.sis.bean.response.RttsDataResponse;
import com.huawei.sis.bean.response.StateResponse;
import com.eappcat.isi.server.vo.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.BinaryMessage;


@Slf4j

public class HuaweiSpeechSynthesizerListener implements RttsListener {

    private SpeechSession session = null;
    private SpeechMessageHeader header = null;
    public HuaweiSpeechSynthesizerListener(SpeechSession session,SpeechMessageHeader header){
        this.session = session;
        this.header = header;
    }

    @Override
    @SneakyThrows
    public void onTranscriptionResponse(RttsDataResponse rttsDataResponse) {
        this.session.getSession().sendMessage(new BinaryMessage(rttsDataResponse.getData(),true));
    }

    @Override
    @SneakyThrows
    public void onTranscriptionBegin(StateResponse stateResponse) {
//        SpeechMessage<SpeechEmptyPayload> response = new SpeechMessage<>();
//        response.setHeader(SpeechMessageHeader.success(this.header.getTaskId(), "SentenceBegin",this.header.getNamespace()));
//        this.session.getSession().sendMessage(new TextMessage(JSON.toJSONString(response)));
    }

    @Override
    @SneakyThrows
    public void onSTranscriptionEnd(StateResponse stateResponse) {
//        SpeechMessage<SpeechEmptyPayload> response = new SpeechMessage<>();
//        response.setHeader(SpeechMessageHeader.success(this.header.getTaskId(), "SentenceEnd",this.header.getNamespace()));
//        this.session.getSession().sendMessage(new TextMessage(JSON.toJSONString(response)));
    }

    @Override
    public void onTranscriptionFail(StateResponse stateResponse) {
        log.error("请求语音合成错误：{}", stateResponse);
    }
}
