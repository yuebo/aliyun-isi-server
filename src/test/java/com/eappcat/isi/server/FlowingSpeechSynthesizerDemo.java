package com.eappcat.isi.server;
import com.alibaba.nls.client.protocol.NlsClient;
import com.alibaba.nls.client.protocol.OutputFormatEnum;
import com.alibaba.nls.client.protocol.SampleRateEnum;
import com.alibaba.nls.client.protocol.tts.FlowingSpeechSynthesizer;
import com.alibaba.nls.client.protocol.tts.FlowingSpeechSynthesizerListener;
import com.alibaba.nls.client.protocol.tts.FlowingSpeechSynthesizerResponse;
import com.eappcat.isi.server.utils.AudioUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 此示例演示了：
 *      流入实时语音合成API调用。
 */
public class FlowingSpeechSynthesizerDemo {
    private static final Logger logger = LoggerFactory.getLogger(FlowingSpeechSynthesizerDemo.class);
    private static long startTime;
    private String appKey;
    private static final File f=new File("flowingTts.pcm");

    NlsClient client;
    public FlowingSpeechSynthesizerDemo(String appKey, String token, String url) {
        this.appKey = appKey;
        //创建NlsClient实例应用全局创建一个即可。生命周期可和整个应用保持一致，默认服务地址为阿里云线上服务地址。
        if(url.isEmpty()) {
            client = new NlsClient(token);
        } else {
            client = new NlsClient(url, token);
        }
    }
    private static FlowingSpeechSynthesizerListener getSynthesizerListener() {
        FlowingSpeechSynthesizerListener listener = null;
        try {
            listener = new FlowingSpeechSynthesizerListener() {
                FileOutputStream fout = new FileOutputStream(f);
                private boolean firstRecvBinary = true;

                //流入语音合成开始
                public void onSynthesisStart(FlowingSpeechSynthesizerResponse response) {
                    System.out.println("name: " + response.getName() +
                            ", status: " + response.getStatus());
                }
                //服务端检测到了一句话的开始
                public void onSentenceBegin(FlowingSpeechSynthesizerResponse response) {
                    System.out.println("name: " + response.getName() +
                            ", status: " + response.getStatus());
                    System.out.println("Sentence Begin");
                }
                //服务端检测到了一句话的结束，获得这句话的起止位置和所有时间戳
                public void onSentenceEnd(FlowingSpeechSynthesizerResponse response) {
                    System.out.println("name: " + response.getName() +
                            ", status: " + response.getStatus() + ", subtitles: " + response.getObject("subtitles"));

                }
                //流入语音合成结束
                @Override
                public void onSynthesisComplete(FlowingSpeechSynthesizerResponse response) {
                    // 调用onSynthesisComplete时，表示所有TTS数据已经接收完成，所有文本都已经合成音频并返回。
                    System.out.println("name: " + response.getName() + ", status: " + response.getStatus()+", output file :"+f.getAbsolutePath());
                }
                //收到语音合成的语音二进制数据
                @Override
                public void onAudioData(ByteBuffer message) {
                    try {
                        if(firstRecvBinary) {
                            // 此处计算首包语音流的延迟，收到第一包语音流时，即可以进行语音播放，以提升响应速度（特别是实时交互场景下）。
                            firstRecvBinary = false;
                            long now = System.currentTimeMillis();
                            logger.info("tts first latency : " + (now - FlowingSpeechSynthesizerDemo.startTime) + " ms");
                        }
                        byte[] bytesArray = new byte[message.remaining()];
                        message.get(bytesArray, 0, bytesArray.length);
                        System.out.println("write array:" + bytesArray.length);
                        fout.write(bytesArray);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //收到语音合成的增量音频时间戳
                @Override
                public void onSentenceSynthesis(FlowingSpeechSynthesizerResponse response) {
                    System.out.println("name: " + response.getName() +
                            ", status: " + response.getStatus() + ", subtitles: " + response.getObject("subtitles"));
                }
                @Override
                public void onFail(FlowingSpeechSynthesizerResponse response){
                    // task_id是调用方和服务端通信的唯一标识，当遇到问题时，需要提供此task_id以便排查。
                    System.out.println(
                            "session_id: " + getFlowingSpeechSynthesizer().getCurrentSessionId() +
                                    ", task_id: " + response.getTaskId() +
                                    //状态码
                                    ", status: " + response.getStatus() +
                                    //错误信息
                                    ", status_text: " + response.getStatusText());
                }
            };
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listener;
    }
    public void process(String[] textArray) {
        FlowingSpeechSynthesizer synthesizer = null;
        try {
            //创建实例，建立连接。
            synthesizer = new FlowingSpeechSynthesizer(client, getSynthesizerListener());
            synthesizer.setAppKey(appKey);
            //设置返回音频的编码格式。
            //设置返回音频的采样率。
            synthesizer.setSampleRate(SampleRateEnum.SAMPLE_RATE_16K);
            //发音人。注意Java SDK不支持调用超高清场景对应的发音人（例如"zhiqi"），如需调用请使用restfulAPI方式。
            synthesizer.setVoice("siyue");
            //音量，范围是0~100，可选，默认50。
            synthesizer.setVolume(50);
            //语调，范围是-500~500，可选，默认是0。
            synthesizer.setPitchRate(0);
            //语速，范围是-500~500，默认是0。
            synthesizer.setSpeechRate(0);
            synthesizer.setFormat(OutputFormatEnum.PCM);
            //此方法将以上参数设置序列化为JSON发送给服务端，并等待服务端确认。
            long start = System.currentTimeMillis();
            synthesizer.start();
            logger.info("tts start latency " + (System.currentTimeMillis() - start) + " ms");
            FlowingSpeechSynthesizerDemo.startTime = System.currentTimeMillis();
            //设置连续两次发送文本的最小时间间隔（毫秒），如果当前调用send时距离上次调用时间小于此值，则会阻塞并等待直到满足条件再发送文本
            synthesizer.setMinSendIntervalMS(100);
            for(String text :textArray) {
                //发送流入文本数据。
                synthesizer.send(text);
            }
            //通知服务端流入文本数据发送完毕，阻塞等待服务端处理完成。
            synthesizer.stop();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭连接
            if (null != synthesizer) {
                synthesizer.close();
            }
            AudioUtils.pcmToWav(f,new File(f.getName()+".wav"),1,16000,16);
        }
    }
    public void shutdown() {
        client.shutdown();
    }
}
