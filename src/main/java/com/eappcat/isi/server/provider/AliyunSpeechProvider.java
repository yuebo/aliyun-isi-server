package com.eappcat.isi.server.provider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nls.client.AccessToken;
import com.alibaba.nls.client.protocol.InputFormatEnum;
import com.alibaba.nls.client.protocol.NlsClient;
import com.alibaba.nls.client.protocol.OutputFormatEnum;
import com.alibaba.nls.client.protocol.SampleRateEnum;
import com.alibaba.nls.client.protocol.asr.SpeechTranscriber;
import com.alibaba.nls.client.protocol.tts.FlowingSpeechSynthesizer;
import com.alibaba.nls.client.protocol.tts.SpeechSynthesizer;
import com.eappcat.isi.server.config.ProviderConfigProperties;
import com.eappcat.isi.server.vo.*;
import lombok.SneakyThrows;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;

import java.nio.ByteBuffer;

@Component
@ConditionalOnProperty(prefix = "provider",value = "name",havingValue = "aliyun", matchIfMissing = true)
public class AliyunSpeechProvider implements SpeechProvider {

    private NlsClient client;
    private String projectId;

    @SneakyThrows
    public AliyunSpeechProvider(ProviderConfigProperties properties) {
        AccessToken accessToken = new AccessToken(properties.getAppKey(), properties.getAppSecret());
        accessToken.apply();
        client = new NlsClient(accessToken.getToken());
        this.projectId = properties.getProjectId();
    }


    @Override
    public void onConnected(SpeechSession session) throws Exception {

    }

    @Override
    public void onStartTranscription(SpeechSession session, SpeechMessage<SpeechStartTranscriptionPayload> message) throws Exception {
        AliyunSpeechTranscriberListener aliyunSpeechTranscriberListener = new AliyunSpeechTranscriberListener(session);
        SpeechTranscriber transcriber = new SpeechTranscriber(client, aliyunSpeechTranscriberListener);
        //输入音频编码方式。
        transcriber.setFormat(InputFormatEnum.PCM);
        //输入音频采样率。
        if (message.getPayload().getSampleRate()!=null){
            if (message.getPayload().getSampleRate()==16000){
                transcriber.setSampleRate(SampleRateEnum.SAMPLE_RATE_16K);
            }else if (message.getPayload().getSampleRate()==8000){
                transcriber.setSampleRate(SampleRateEnum.SAMPLE_RATE_8K);
            }
        }
        //是否返回中间识别结果。
        transcriber.setEnableIntermediateResult(message.getPayload().getEnableIntermediateResult());
        //是否生成并返回标点符号。
        transcriber.setEnablePunctuation(message.getPayload().getEnablePunctuationPrediction());
        //是否将返回结果规整化，比如将一百返回为100。
        transcriber.setEnableITN(message.getPayload().getEnableInverseTextNormalization());
        session.getData().put("transcriber",transcriber);


        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(message.getPayload()));
        transcriber.payload.putAll(jsonObject);
        if (!transcriber.payload.containsKey("max_sentence_silence")){
            transcriber.payload.put("max_sentence_silence",1200);
        }
        transcriber.setAppKey(this.projectId);
        transcriber.start();
    }

    @Override
    public void onStopTranscription(SpeechSession session, SpeechMessage<SpeechEmptyPayload> message) throws Exception {
        SpeechTranscriber transcriber = (SpeechTranscriber)session.getData().get("transcriber");
        transcriber.stop();
    }

    @Override
    public void onBinaryMessage(SpeechSession session, ByteBuffer payload) throws Exception {
        SpeechTranscriber transcriber = (SpeechTranscriber)session.getData().get("transcriber");
        byte[] data = payload.array();
        transcriber.send(data,data.length);
    }

    @Override
    public void onDisConnected(SpeechSession session) throws Exception {
        SpeechTranscriber transcriber = (SpeechTranscriber)session.getData().get("transcriber");
        SpeechSynthesizer synthesizer = (SpeechSynthesizer)session.getData().get("synthesizer");
        if (transcriber!=null){
            transcriber.close();
        }
        if (synthesizer!=null){
            synthesizer.close();
        }
    }

    @Override
    public void onRunSynthesis(SpeechSession session, SpeechMessage<SpeechRunSynthesisPayload> message) throws Exception {
        FlowingSpeechSynthesizer synthesizer = (FlowingSpeechSynthesizer)session.getData().get("synthesizer");
        synthesizer.send(message.getPayload().getText());
    }

    @Override
    public void onStartSynthesis(SpeechSession session, SpeechMessage<SpeechStartSynthesisPayload> message) throws Exception {
        AliyunSpeechSynthesizerListener listener =  new AliyunSpeechSynthesizerListener(session);

        SpeechSynthesizer synthesizer =  new SpeechSynthesizer(client, listener);
        //设置返回音频的编码格式。
        synthesizer.setFormat(OutputFormatEnum.PCM);
        if (message.getPayload().getSpeechRate()!=null){
            if (message.getPayload().getSpeechRate()==16000){
                //设置返回音频的采样率。
                synthesizer.setSampleRate(SampleRateEnum.SAMPLE_RATE_16K);
            }else if (message.getPayload().getSpeechRate()==8000){
                //设置返回音频的采样率。
                synthesizer.setSampleRate(SampleRateEnum.SAMPLE_RATE_8K);
            }
        }

        //发音人。注意Java SDK不支持调用超高清场景对应的发音人（例如"zhiqi"），如需调用请使用restfulAPI方式。
        synthesizer.setVoice(message.getPayload().getVoice());
        //音量，范围是0~100，可选，默认50。
        synthesizer.setVolume(message.getPayload().getVolume());
        //语调，范围是-500~500，可选，默认是0。
        synthesizer.setPitchRate(message.getPayload().getPitchRate());
        //语速，范围是-500~500，默认是0。
        synthesizer.setSpeechRate(message.getPayload().getSpeechRate());
        session.getData().put("synthesizer",synthesizer);

        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(message.getPayload()));
        synthesizer.payload.putAll(jsonObject);
        synthesizer.setAppKey(this.projectId);
        synthesizer.start();

        //由于gen

        SpeechMessage<SpeechEmptyPayload> response =  new SpeechMessage<>();
        response.setHeader(SpeechMessageHeader.success(message.getHeader().getTaskId(),"SynthesisStarted",message.getHeader().getNamespace()));
        session.getSession().sendMessage(new TextMessage(JSON.toJSONString(response)));

//        synthesizer.setMinSendIntervalMS(100);

    }

    @Override
    public void onStopSynthesis(SpeechSession session, SpeechMessage<SpeechEmptyPayload> speechMessage) throws Exception {
        FlowingSpeechSynthesizer synthesizer = (FlowingSpeechSynthesizer)session.getData().get("synthesizer");
        synthesizer.stop();
    }
}
