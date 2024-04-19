//package com.eappcat.isi.server.provider;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.nls.client.protocol.tts.FlowingSpeechSynthesizerListener;
//import com.alibaba.nls.client.protocol.tts.FlowingSpeechSynthesizerResponse;
//import com.eappcat.isi.server.vo.SpeechSession;
//import lombok.SneakyThrows;
//import lombok.extern.slf4j.Slf4j;
//import org.jetbrains.annotations.NotNull;
//import org.springframework.web.socket.BinaryMessage;
//import org.springframework.web.socket.TextMessage;
//
//import java.nio.ByteBuffer;
//
//
//@Slf4j
//public class AliyunFlowingSpeechSynthesizerListener extends FlowingSpeechSynthesizerListener {
//
//    private SpeechSession session = null;
//    public AliyunFlowingSpeechSynthesizerListener(SpeechSession session){
//        this.session = session;
//    }
//    @Override
//    @SneakyThrows
//    public void onSentenceBegin(FlowingSpeechSynthesizerResponse flowingSpeechSynthesizerResponse) {
//        this.session.getSession().sendMessage(new TextMessage(JSON.toJSONString(flowingSpeechSynthesizerResponse)));
//
//    }
//
//    @Override
//    @SneakyThrows
//    public void onAudioData(@NotNull ByteBuffer byteBuffer) {
//        log.info("onAudioData=====>{}", byteBuffer.remaining());
//        this.session.getSession().sendMessage(new BinaryMessage(byteBuffer,true));
//    }
//
//    @Override
//    @SneakyThrows
//    public void onSentenceEnd(FlowingSpeechSynthesizerResponse flowingSpeechSynthesizerResponse) {
//        this.session.getSession().sendMessage(new TextMessage(JSON.toJSONString(flowingSpeechSynthesizerResponse)));
//
//    }
//
//    @Override
//    @SneakyThrows
//    public void onSynthesisComplete(FlowingSpeechSynthesizerResponse flowingSpeechSynthesizerResponse) {
//        this.session.getSession().sendMessage(new TextMessage(JSON.toJSONString(flowingSpeechSynthesizerResponse)));
//    }
//
//    @Override
//    @SneakyThrows
//    public void onFail(FlowingSpeechSynthesizerResponse flowingSpeechSynthesizerResponse) {
//        this.session.getSession().sendMessage(new TextMessage(JSON.toJSONString(flowingSpeechSynthesizerResponse)));
//    }
//
//    @Override
//    @SneakyThrows
//    public void onSentenceSynthesis(FlowingSpeechSynthesizerResponse flowingSpeechSynthesizerResponse) {
//        this.session.getSession().sendMessage(new TextMessage(JSON.toJSONString(flowingSpeechSynthesizerResponse)));
//    }
//}
