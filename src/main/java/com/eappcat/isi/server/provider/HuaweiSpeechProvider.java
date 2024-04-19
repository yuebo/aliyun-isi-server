package com.eappcat.isi.server.provider;

import com.alibaba.fastjson.JSON;
import com.huawei.sis.bean.AuthInfo;
import com.huawei.sis.bean.SisConfig;
import com.huawei.sis.bean.SisConstant;
import com.huawei.sis.bean.request.RasrRequest;
import com.huawei.sis.bean.request.RttsRequest;
import com.huawei.sis.client.RasrClient;
import com.huawei.sis.client.RttsClient;
import com.eappcat.isi.server.config.ProviderConfigProperties;
import com.eappcat.isi.server.vo.*;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;

import java.nio.ByteBuffer;


/**
 * 华为云语音ISI服务相关API
 */
@Component
@ConditionalOnProperty(prefix = "provider",value = "name",havingValue = "huawei")
@Slf4j
public class HuaweiSpeechProvider implements SpeechProvider{
    private AuthInfo authInfo;

    private ProviderConfigProperties configProperties;
    public HuaweiSpeechProvider(ProviderConfigProperties properties) {
        authInfo = new AuthInfo(properties.getAppKey(), properties.getAppSecret(),properties.getRegion(),properties.getProjectId());
        this.configProperties = properties;
    }
    @Override
    public void onConnected(SpeechSession session) throws Exception {

    }

    @Override
    public void onStartTranscription(SpeechSession session, SpeechMessage<SpeechStartTranscriptionPayload> message) throws Exception {
        log.info("onStartTranscription=====>{}", message);
        SisConfig config = new SisConfig();
        // 设置连接超时，默认10000ms
        config.setConnectionTimeout(SisConstant.DEFAULT_CONNECTION_TIMEOUT);
        // 设置读取超时，默认10000ms
        config.setReadTimeout(SisConstant.DEFAULT_READ_TIMEOUT);
        // 设置websocket等待超时时间，默认20000ms
        config.setWebsocketWaitTimeout(SisConstant.DEFAULT_WEBSOCKET_WAIT_TIME);
        HuaweiTranscriberListener listener = new HuaweiTranscriberListener(session,message.getHeader());
        RasrClient rasrClient = new RasrClient(authInfo, listener, config);
        RasrRequest request = new RasrRequest("pcm8k16bit", "chinese_8k_general");
        // 1. 设置是否添加标点符号，yes 或 no， 默认"no"

        request.setAddPunc(Boolean.FALSE.equals(message.getPayload().getEnablePunctuationPrediction())?"no":"yes");
        // 2. 设置头部的最大静音时间，[0,60000], 默认10000ms
        request.setVadHead(3000);
        // 3. 设置尾部最大静音时间，[0, 3000], 默认500ms，
        request.setVadTail(1500);
        // 4. 设置最长持续时间，仅在continue-stream，sentence-stream模式下起作用，[1, 60], 默认30s
        request.setMaxSeconds(60);
        // 5. 设置是否显示中间结果，yes或no，默认“no”。例如分3次发送音频，选择no结果一次性返回，选择yes分三次返回。
        request.setIntermediateResult(Boolean.TRUE.equals(message.getPayload().getEnableIntermediateResult())?"yes":"no");
        // 6. 设置热词表id, 若没有则设置，否则会报错。
        // request.setVocabularyId("");
        // 7. 设置是否将音频中数字转写为阿拉伯数字，yes or no，默认yes
        request.setDigitNorm(Boolean.FALSE.equals(message.getPayload().getEnableInverseTextNormalization())?"no":"yes");
        rasrClient.continueStreamConnect(request);
        session.getData().put("rasrClient",rasrClient);
        rasrClient.sendStart();

    }

    @Override
    public void onStopTranscription(SpeechSession session, SpeechMessage<SpeechEmptyPayload> message) throws Exception {
        RasrClient rasrClient = (RasrClient)session.getData().get("rasrClient");
        rasrClient.sendEnd();
        log.info("onStopTranscription=====>{}", message);
    }

    @Override
    public void onBinaryMessage(SpeechSession session, ByteBuffer payload) throws Exception {
        RasrClient rasrClient = (RasrClient)session.getData().get("rasrClient");
        byte[] bytes = new byte[payload.remaining()];
        payload.get(bytes);
        rasrClient.sendByte(bytes);
    }

    @Override
    public void onDisConnected(SpeechSession session) throws Exception {
        RasrClient rasrClient = (RasrClient)session.getData().get("rasrClient");
        if (rasrClient!=null){
            rasrClient.close();
        }
    }

    @Override
    public void onRunSynthesis(SpeechSession session, SpeechMessage<SpeechRunSynthesisPayload> message) throws Exception {
        SisConfig sisConfig =  new SisConfig();
        // 设置连接超时，默认10000ms
        sisConfig.setConnectionTimeout(SisConstant.DEFAULT_CONNECTION_TIMEOUT);
        // 设置读取超时，默认10000ms
        sisConfig.setReadTimeout(SisConstant.DEFAULT_READ_TIMEOUT);
        // 设置websocket等待超时时间，默认20000ms
        sisConfig.setWebsocketWaitTimeout(SisConstant.DEFAULT_WEBSOCKET_WAIT_TIME);
        HuaweiSpeechSynthesizerListener ttsListener = new HuaweiSpeechSynthesizerListener(session,message.getHeader());
        session.getData().put("ttsListener",ttsListener);
        RttsClient ttsClient = new RttsClient(authInfo,ttsListener,sisConfig);
        RttsRequest request = (RttsRequest)session.getData().get("rttsRequest");
        request.setText(message.getPayload().getText());
        ttsClient.synthesis(request);
    }

    @Override
    public void onStartSynthesis(SpeechSession session, SpeechMessage<SpeechStartSynthesisPayload> message) throws Exception {
        log.info("onStartSynthesis=====>{}", message);
        RttsRequest request = new RttsRequest();
        request.setCommand("START");
        // 设置待合成文本，文本长度1-500字
        RttsRequest.Config config = new RttsRequest.Config();
        // 设置发音人属性，{language}_{speaker}_{domain}, 详见api文档 https://support.huaweicloud.com/api-sis/sis_03_0111.html, aliyun的voice可以voiceMap转换成华为云的语音
        String voice = configProperties.getVoiceMap().getOrDefault(message.getPayload().getVoice(),"chinese_xiaoyan_common");
        config.setPorperty(voice);
        // 设置合成音频格式，默认pcm
        config.setAudioFormat(message.getPayload().getFormat());
        // 设置合成音频采样率，当前支持8000和16000，默认8000
        if (message.getPayload().getSampleRate()!=null){
            config.setSampleRate(String.valueOf(message.getPayload().getSampleRate()));
        }
        if (message.getPayload().getVolume()>0){
            // 设置合成音频音量大小，取值0-100，默认50
            config.setVolume(message.getPayload().getVolume());
        }
        if (message.getPayload().getPitchRate()>0){
            // 设置合成音频音高大小，取值-500-500，默认0
            config.setPitch(message.getPayload().getPitchRate());
        }
        if (message.getPayload().getSpeechRate()>0){
            // 设置合成音频语速大小，取值-500-500，默认0
            config.setSpeed(message.getPayload().getSpeechRate());
        }
        request.setConfig(config);
        session.getData().put("rttsRequest",request);

        SpeechMessage<SpeechEmptyPayload> response =  new SpeechMessage<>();
        response.setHeader(SpeechMessageHeader.success(message.getHeader().getTaskId(),"SynthesisStarted",message.getHeader().getNamespace()));
        session.getSession().sendMessage(new TextMessage(JSON.toJSONString(response)));

        if (StringUtils.isNotEmpty(message.getPayload().getText())){
            SisConfig sisConfig =  new SisConfig();
            // 设置连接超时，默认10000ms
            sisConfig.setConnectionTimeout(SisConstant.DEFAULT_CONNECTION_TIMEOUT);
            // 设置读取超时，默认10000ms
            sisConfig.setReadTimeout(SisConstant.DEFAULT_READ_TIMEOUT);
            // 设置websocket等待超时时间，默认20000ms
            sisConfig.setWebsocketWaitTimeout(SisConstant.DEFAULT_WEBSOCKET_WAIT_TIME);
            HuaweiSpeechSynthesizerListener ttsListener = new HuaweiSpeechSynthesizerListener(session,message.getHeader());
            session.getData().put("ttsListener",ttsListener);
            RttsClient ttsClient = new RttsClient(authInfo,ttsListener,sisConfig);
            request.setText(message.getPayload().getText());
            ttsClient.synthesis(request);
        }

    }

    @Override
    public void onStopSynthesis(SpeechSession session, SpeechMessage<SpeechEmptyPayload> message) throws Exception {
        SpeechMessage<SpeechEmptyPayload> response = new SpeechMessage<>();
        response.setHeader(SpeechMessageHeader.success(message.getHeader().getTaskId(), "SynthesisCompleted",message.getHeader().getNamespace()));
        session.getSession().sendMessage(new TextMessage(JSON.toJSONString(response)));
        log.info("onStopSynthesis=====>{}", message);
    }
}
