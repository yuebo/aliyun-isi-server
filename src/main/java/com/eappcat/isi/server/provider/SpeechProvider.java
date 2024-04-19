package com.eappcat.isi.server.provider;

import com.eappcat.isi.server.vo.*;

import java.nio.ByteBuffer;

/**
 * 流式语音识别和语音合成接口
 */
public interface SpeechProvider {
    /**
     * 连接创建事件
     * @param session 当前session
     * @throws Exception
     */
    void onConnected(SpeechSession session) throws Exception;

    /**
     * 语音识别开始事件
     * @param session 当前session
     * @param message 开始识别事件
     * @throws Exception
     */
    void onStartTranscription(SpeechSession session, SpeechMessage<SpeechStartTranscriptionPayload> message) throws Exception;

    /**
     * 语音识别停止事件
     * @param session 当前session
     * @param message 空事件
     * @throws Exception
     */
    void onStopTranscription(SpeechSession session, SpeechMessage<SpeechEmptyPayload> message) throws Exception;

    /**
     * 语音识别传输二进制数据
     * @param session 当前session
     * @param payload 二进制数据
     * @throws Exception
     */
    void onBinaryMessage(SpeechSession session, ByteBuffer payload) throws Exception;

    /**
     * 连接关闭事件
     * @param session 当前session
     * @throws Exception
     */
    void onDisConnected(SpeechSession session) throws Exception;

    /**
     * 语音合成的文本内容
     * @param session 当前session
     * @param message 语音合成文本事件
     * @throws Exception
     */

    void onRunSynthesis(SpeechSession session, SpeechMessage<SpeechRunSynthesisPayload> message) throws Exception;

    /**
     * 语音合成的开始事件
     * @param session 当前session
     * @param message 语音合成开始事件
     * @throws Exception
     */

    void onStartSynthesis(SpeechSession session, SpeechMessage<SpeechStartSynthesisPayload> message) throws Exception;

    /**
     * 语音合成的结束事件
     * @param session 当前session
     * @param message 空事件
     * @throws Exception
     */

    void onStopSynthesis(SpeechSession session, SpeechMessage<SpeechEmptyPayload> message) throws Exception;
}
