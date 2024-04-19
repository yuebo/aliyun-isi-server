package com.eappcat.isi.server;

import com.alibaba.nls.client.AccessToken;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class AliyunIsiServerApplicationTests {

    @Test
    void contextLoads() throws Exception {
        tts();
    }

    void asr(){
        String appKey = System.getenv("APP_KEY");
        String id = System.getenv("PROJECT_ID");
        String secret =  System.getenv("APP_SECRET");
        String url = System.getenv().getOrDefault("NLS_GATEWAY_URL", "ws://localhost:8080/ws/v1");

        //本案例使用本地文件模拟发送实时流数据。您在实际使用时，可以实时采集或接收语音流并发送到ASR服务端。
        String filepath = "nls-sample-16k.wav";
        SpeechTranscriberDemo demo = new SpeechTranscriberDemo(appKey, id, secret, url);
        demo.process(filepath);
        demo.shutdown();
    }

    void tts() throws Exception{
        String appKey = System.getenv("APP_KEY");
        String id = System.getenv("PROJECT_ID");
        String secret =  System.getenv("APP_SECRET");
        String url = System.getenv().getOrDefault("NLS_GATEWAY_URL", "ws://localhost:8080/ws/v1");

        String[] textArray = {"百草堂与三", "味书屋 鲁迅 \n我家的后面有一个很", "大的园，相传叫作百草园。现在是早已并屋子一起卖", "给朱文公的子孙了，连那最末次的相见也已经",
                "隔了七八年，其中似乎确凿只有一些野草；但那时却是我的乐园。\n不必说碧绿的菜畦，光滑的石井栏，高大的皂荚树，紫红的桑葚；也不必说鸣蝉在树叶里长吟，肥胖的黄蜂伏在菜花",
                "上，轻捷的叫天子(云雀)忽然从草间直窜向云霄里去了。\n单是周围的短短的泥墙根一带，就有无限趣味。油蛉在这里低唱，蟋蟀们在这里弹琴。翻开断砖来，有时会遇见蜈蚣；还有斑",
                "蝥，倘若用手指按住它的脊梁，便会啪的一声，\n从后窍喷出一阵烟雾。何首乌藤和木莲藤缠络着，木莲有莲房一般的果实，何首乌有臃肿的根。有人说，何首乌根是有像人形的，吃了",
                "便可以成仙，我于是常常拔它起来，牵连不断地拔起来，\n也曾因此弄坏了泥墙，却从来没有见过有一块根像人样! 如果不怕刺，还可以摘到覆盆子，像小珊瑚珠攒成的小球，又酸又甜，",
                "色味都比桑葚要好得远......"};
        AccessToken accessToken = new AccessToken(id, secret);
        accessToken.apply();
        FlowingSpeechSynthesizerDemo demo = new FlowingSpeechSynthesizerDemo(appKey,accessToken.getToken() , url);
        demo.process(textArray);
        demo.shutdown();
    }

}
