package com.eappcat.isi.server;

import com.cloud.sdk.util.StringUtils;
import com.huawei.sis.bean.AuthInfo;
import com.huawei.sis.bean.RttsListener;
import com.huawei.sis.bean.SisConfig;
import com.huawei.sis.bean.SisConstant;
import com.huawei.sis.bean.request.RttsRequest;
import com.huawei.sis.bean.response.RttsDataResponse;
import com.huawei.sis.bean.response.StateResponse;
import com.huawei.sis.client.RttsClient;
import com.huawei.sis.util.JsonUtils;
import com.eappcat.isi.server.utils.AudioUtils;


import java.io.*;

/**
 * 实时语音合成Demo
 *
 * Copyright 2021 Huawei Technologies Co.,Ltd.
 */
public class RttsDemo {
    // 认证用的ak和sk硬编码到代码中或者明文存储都有很大的安全风险，建议在配置文件或者环境变量中密文存放，使用时解密，确保安全；
    // 本示例以ak和sk保存在环境变量中来实现身份验证为例，运行本示例前请先在本地环境中设置环境变量HUAWEICLOUD_SDK_AK和HUAWEICLOUD_SDK_SK。
    private String ak=System.getenv("APP_KEY");
    private String sk=System.getenv("APP_SECRET");


    private String region = "cn-east-3";         // 区域，如cn-north-1、cn-north-4
    private String projectId=System.getenv("PROJECT_ID");      // 项目id，在我的凭证查看。参考https://support.huaweicloud.com/api-sis/sis_03_0008.html

    private static String text = "你好,这里是某某客服中心";            // 待合成的文本
    private static String path = "test.pcm";            // 合成音频存储的路径


    public static void main(String[] args) {
        RttsDemo rttsDemo = new RttsDemo();
        rttsDemo.process();
        AudioUtils.pcmToWav(new File(path), new File(path + ".wav"), 1, 16000, 16);
        System.exit(0);
    }

    /**
     * 实时语音合成参数设置，所有参数设置均为可选，均有默认值。用户根据需求设置参数。
     */
    private RttsRequest getRttsRequest() {
        RttsRequest request = new RttsRequest();
        request.setCommand("START");
        // 设置待合成文本，文本长度1-500字
        request.setText(text);
        RttsRequest.Config config = new RttsRequest.Config();
        // 设置发音人属性，{language}_{speaker}_{domain}, 详见api文档
        config.setPorperty("chinese_xiaoyan_common");
        // 设置合成音频格式，默认pcm
        config.setAudioFormat("pcm");
        // 设置合成音频采样率，当前支持8000和16000，默认8000
        config.setSampleRate("16000");
        // 设置合成音频音量大小，取值0-100，默认50
        config.setVolume(50);
        // 设置合成音频音高大小，取值-500-500，默认0
        config.setPitch(0);
        // 设置合成音频语速大小，取值-500-500，默认0
        config.setSpeed(0);
        request.setConfig(config);
        return request;
    }

    /**
     * 定义config，所有参数可选，设置超时时间等。
     *
     * @return SisConfig
     */
    private SisConfig getConfig() {
        SisConfig config = new SisConfig();
        // 设置连接超时，默认10000ms
        config.setConnectionTimeout(SisConstant.DEFAULT_CONNECTION_TIMEOUT);
        // 设置读取超时，默认10000ms
        config.setReadTimeout(SisConstant.DEFAULT_READ_TIMEOUT);
        // 设置websocket等待超时时间，默认20000ms
        config.setWebsocketWaitTimeout(SisConstant.DEFAULT_WEBSOCKET_WAIT_TIME);
        // 设置代理, 一定要确保代理可用才启动此设置。 代理初始化也可用不加密的代理，new ProxyHostInfo(host, port);
        // ProxyHostInfo proxy = new ProxyHostInfo(host, port, username, password);
        // config.setProxy(proxy);
        return config;
    }


    private void printResponse(Object response) {
        try {
            System.out.println(JsonUtils.obj2Str(response, true));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 实时语音转写SDK的工作流程
     * 1. RttsClient只能发送一次文本，如有多个文本需发送，需要多次新建RttsClient实例
     * 2. 实时语音合成会多次收到音频响应，默认格式为pcm。在demo中会把多次返回的结果拼接起来，存入文件中。
     * 3. 当服务端完成合成任务后，会返回end响应。
     */
    private void process() {
        // 1. 实现监听器接口RttsListener，用户自定义收到响应的处理逻辑。
        RttsListener rttsListener = new MyRttsListener(path);

        // 2. 初始化RttsClient,每个client只能发送一次text，如需发送多次text，需要建立多个client
        AuthInfo authInfo = new AuthInfo(ak, sk, region, projectId);
        RttsClient rttsClient = new RttsClient(authInfo, rttsListener, getConfig());

        // 3. 配置参数
        // audioFormat为支持格式、property为属性字符串，具体填写请详细参考api文档
        RttsRequest request = getRttsRequest();

        // 4. 发送待合成文本，等待结果
        try {
            rttsClient.synthesis(request);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public class MyRttsListener implements RttsListener {
        private String path;
        private FileOutputStream fos = null;

        public MyRttsListener() {
            super();
        }

        public MyRttsListener(String path) {
            this.path = path;
        }

        @Override
        public void onTranscriptionResponse(RttsDataResponse rttsDataResponse) {
            System.out.println("receive binary data " + rttsDataResponse.getData().length);
            if (fos == null) {
                return;
            }
            try {
                fos.write(rttsDataResponse.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onTranscriptionBegin(StateResponse response) {
            printResponse(response);
            try {
                if (StringUtils.isNullOrEmpty(path)) {
                    return;
                }
                File f = new File(path);
                fos = new FileOutputStream(f);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onSTranscriptionEnd(StateResponse response) {
            printResponse(response);
            close();
        }

        @Override
        public void onTranscriptionFail(StateResponse response) {
            printResponse(response);
            close();

        }

        private void close() {
            if (fos == null) {
                return;
            }
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                fos = null;
            }
        }
    }



}
