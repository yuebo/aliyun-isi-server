智能语音交互（Intelligent Speech Interaction）自定义网关
-----------------------------------------------------

此服务用于实现阿里云的ASR和TTS服务的接口，用于aliyun-mcrp服务器对接实时语音解析, 从而利用阿里云提供的sdm镜像实现自定义的三方语音解析和合成服务。

此服务主要实现两个接口：

1. 流式文本语音合成(https://help.aliyun.com/zh/isi/developer-reference/interface-description)
2. 实时语音识别(https://help.aliyun.com/zh/isi/developer-reference/api-reference)

## 接口描述
参考接口`com.eappcat.isi.server.provider.SpeechProvider`

## 实时语音识别
协议详情：
```text
request->
{"payload":{"sample_rate":16000,"format":"pcm","enable_intermediate_result":false,"enable_inverse_text_normalization":false,"enable_punctuation_prediction":true},"context":{"sdk":{"name":"nls-sdk-java","version":"2.2.13"},"network":{"upgrade_cost":49,"connect_cost":122}},"header":{"namespace":"SpeechTranscriber","name":"StartTranscription","message_id":"6d30f73967ef49a6b543b648fb5c5c4a","appkey":"7LWI6OabGIPty0Fr","task_id":"73b50bc1a6684072b9e117a3307fcf42"}}
<-response
{"header":{"namespace":"SpeechTranscriber","name":"TranscriptionStarted","status":20000000,"message_id":"aaa76961edf94f46b646b0b0cf98222f","task_id":"73b50bc1a6684072b9e117a3307fcf42","status_text":"Gateway:SUCCESS:Success."}}
<-response
{"header":{"namespace":"SpeechTranscriber","name":"SentenceBegin","status":20000000,"message_id":"278061b843c54235af19616fe1dac49a","task_id":"73b50bc1a6684072b9e117a3307fcf42","status_text":"Gateway:SUCCESS:Success."},"payload":{"index":1,"time":880}}
<-response
{"header":{"namespace":"SpeechTranscriber","name":"SentenceEnd","status":20000000,"message_id":"105705062ddf466794adfc166aa5334e","task_id":"73b50bc1a6684072b9e117a3307fcf42","status_text":"Gateway:SUCCESS:Success."},"payload":{"index":1,"time":2520,"result":"åäº¬çå¤©æ°ã","confidence":0.872,"words":[],"status":0,"gender":"","begin_time":880,"fixed_result":"","unfixed_result":"","stash_result":{"sentenceId":2,"beginTime":2520,"text":"","fixedText":"","unfixedText":"","currentTime":2520,"words":[]},"audio_extra_info":"","sentence_id":"5495a7dd10e0405fbd3424e4d0249b55","gender_score":0.0}}
request->
{"header":{"namespace":"SpeechTranscriber","name":"StopTranscription","message_id":"9ad8a4bc41464d839e76a2bb25f4e961","task_id":"0aad1b3f8f5e4092a55d5d0d2942b666","appkey":"7LWI6OabGIPty0Fr"}}
<-response
{"header":{"namespace":"SpeechTranscriber","name":"TranscriptionCompleted","status":20000000,"message_id":"e828829df5fb4211bf4850c2396df698","task_id":"73b50bc1a6684072b9e117a3307fcf42","status_text":"Gateway:SUCCESS:Success."}}
```

## 流式文本语音合成

```text
request->
{"payload":{"volume":50,"voice":"siyue","pitch_rate":0,"sample_rate":16000,"format":"wav","speech_rate":0},"context":{"sdk":{"name":"nls-sdk-java","version":"2.2.13"},"network":{"upgrade_cost":49,"connect_cost":440}},"header":{"namespace":"FlowingSpeechSynthesizer","name":"StartSynthesis","session_id":"a6f62468795441a6922bad689a505e5d","message_id":"a1dd2d514d78431facd60faeb5e9aa17","appkey":"7LWI6OabGIPty0Fr","task_id":"fac66fb46a334f7aa99150a4330148cc"}}
<-response
{"header":{"namespace":"FlowingSpeechSynthesizer","name":"SynthesisStarted","status":20000000,"message_id":"2f75a542e617498c861d7c928f7c92bf","task_id":"fac66fb46a334f7aa99150a4330148cc","status_text":"Gateway:SUCCESS:Success."}}
request->
{"payload":{"text":"百草堂与三"},"context":{"sdk":{"name":"nls-sdk-java","version":"2.2.13"}},"header":{"namespace":"FlowingSpeechSynthesizer","name":"RunSynthesis","message_id":"4973a3bc7bef45628640863e2e7d1946","task_id":"fac66fb46a334f7aa99150a4330148cc","appkey":"7LWI6OabGIPty0Fr"}}
request->
{"payload":{"text":"味书屋"},"context":{"sdk":{"name":"nls-sdk-java","version":"2.2.13"}},"header":{"namespace":"FlowingSpeechSynthesizer","name":"RunSynthesis","message_id":"15c5d894d769451ca8954a5c6aa13292","task_id":"fac66fb46a334f7aa99150a4330148cc","appkey":"7LWI6OabGIPty0Fr"}}
<-response
{"header":{"namespace":"FlowingSpeechSynthesizer","name":"SentenceBegin","status":20000000,"message_id":"8a44ee8eadb74c67ba24bce1b2fb5a7c","task_id":"fac66fb46a334f7aa99150a4330148cc","status_text":"Gateway:SUCCESS:Success."},"payload":{"index":0}}
<-response
{"header":{"namespace":"FlowingSpeechSynthesizer","name":"SentenceSynthesis","status":20000000,"message_id":"56b5e7d0d4244f0098a8a87ac9459f1d","task_id":"fac66fb46a334f7aa99150a4330148cc","status_text":"Gateway:SUCCESS:Success."},"payload":{"subtitles":[]}}
<-response
{"header":{"namespace":"FlowingSpeechSynthesizer","name":"SentenceEnd","status":20000000,"message_id":"ce8b064ba6884955ba9e6f7804773c3b","task_id":"fac66fb46a334f7aa99150a4330148cc","status_text":"Gateway:SUCCESS:Success."},"payload":{"subtitles":[]}}.~..{"header":{"namespace":"FlowingSpeechSynthesizer","name":"SynthesisCompleted","status":20000000,"message_id":"8ff31f9d7e1a459a9307cc81f985b6ee","task_id":"fac66fb46a334f7aa99150a4330148cc","status_text":"Gateway:SUCCESS:Success."}}
request->
{"header":{"namespace":"FlowingSpeechSynthesizer","name":"StopSynthesis","message_id":"9ad8a4bc41464d839e76a2bb25f4e961","task_id":"0aad1b3f8f5e4092a55d5d0d2942b666","appkey":"7LWI6OabGIPty0Fr"}}
<-response
{"header":{"namespace":"FlowingSpeechSynthesizer","name":"SynthesisCompleted","status":20000000,"message_id":"e828829df5fb4211bf4850c2396df698","task_id":"73b50bc1a6684072b9e117a3307fcf42","status_text":"Gateway:SUCCESS:Success."}}

```


## 阿里云sdm安装
可以使用以下命令安装阿里云的sdm服务
```
docker run -d --name sdm -it --privileged  -v /root/sdm/logs:/home/admin/logs -v /root/sdm/data:/home/admin/disk registry.cn-shanghai.aliyuncs.com/nls-cloud/sdm:latest standalone
```

### 配置ASR和TTS服务
service-asr.json
```json
{
    "url": "ws://192.168.1.100:8080/ws/v1",
    "appkey": "test",
    "format": "pcm",
    "sample_rate": 8000,
    "enable_intermediate_result": 0,
    "enable_punctuation_prediction": 1,
    "enable_inverse_text_normalization": 1,
    "enable_semantic_sentence_detection": 0,
    "enable_ignore_sentence_timeout": 1,
    "enable_ner": 0,
    "ner_name": "aca",
    "enbale_gender_detect": 0,
    "desc": {
        "notice": "此处仅仅是描述信息，无需修改",
        "url": "访问ASR的url, 如果是调用公有云ASR，无需变动；如果是调用私有云ASR，需根据现场部署情况进行设置，一般为:vip://gateway-ip.vipserver_1:port_1,gateway-ip.vipserver_2:port_2/vipTargetDomain，其中vipServerIp:port可以有一个或者多个，用逗号分割，端口不指定默认为80，最后指定vipServer的Domain",
        "appkey": "appkey, 调用公有云ASR时需要在阿里云官网获取, 调用私有云ASR时固定设置为(全小写):default",
        "format": "编码格式，pcm或opu, 保持pcm不变",
        "sample_rate": "采样率, 8000或16000, 保持8000不变",
        "enable_intermediate_result": "设置是否返回中间识别结果, 可选参数. 服务端默认0",
        "enable_punctuation_prediction": "设置是否在识别结果中添加标点, 可选参数. 服务端默认0",
        "enable_inverse_text_normalization": "设置是否在识别结果中执行文本正则化/数字转换, 比如'一千六百五十'会返回'1650', 可选参数. 服务端默认0",
        "enable_semantic_sentence_detection": "设置是否使用语义断句, 可选参数. 服务端默认0, 保持配置0不变",
        "customization_id": "设置热词定制模型id, 默认不设置，除非已经训练了定制模型且通过本配置生效，则需要在上面json格式上增加",
        "vocabulary_id": "设置热词id, 默认不设置，除非已经训练了定制热词且通过本配置生效，则需要在上面json格式上增加",
        "class_vocabulary_id": "设置类热词id, 默认不设置，除非已经训练了定制类热词且通过本配置生效，则需要在上面json格式上增加",
        "als_am_id": "在am混部时候设置的am模型id",
        "enable_ner": "启动ner、地址解析功能，默认不启用",
        "enbale_gender_detect": "是否开启性别识别，默认不启用",
        "notice": "此处仅仅是描述信息，无需修改"
    }
}
```


service-tts.json

```json
{
  "url": "ws://192.168.1.100:8080/ws/v1",
  "appkey": "test",
  "format": "pcm",
  "sample_rate": 8000,
  "voice": "xiaoyun",
  "volume": 50,
  "speech_rate": 0,
  "pitch_rate": 0,
  "method": 0,
  "desc": {
    "url": "访问TTS的url, 如果是调用公有云TTS，无需变动；如果是调用私有云TTS，需根据现场部署情况进行设置，一般为:vip://gateway-ip.vipserver_1:port_1,gateway-ip.vipserver_2:port_2/vipTargetDomain，其中vipServerIp:port可以有一个或者多个，用逗号分割，端口不指定默认为80，最后指定vipServer的Domain",
    "appkey": "appkey, 调用公有云TTS时需要在阿里云官网获取, 调用私有云TTS时固定设置为(全小写):default",
    "format": "编码格式，支持pcm, wav, mp3, 保持pcm不变",
    "sample_rate": "采样率, 8000或16000, 保持8000不变",
    "volume": "音量, 范围是0~100, 可选参数, 默认50",
    "voice": "发音人, 支持xiaoyun(女), xiaogang(男)等",
    "speech_rate": "语速, 范围是-500~500, 可选参数, 默认是0",
    "pitch_rate": "语调, 范围是-500~500, 可选参数, 默认是0",
    "method": "合成方法, 可选参数, 默认是0. 参数含义0:不带录音的参数合成; 1:带录音的拼接合成; 2:不带录音的拼接合成; 3:带录音的参数合成",
    "notice": "此处仅仅是描述信息，无需修改"
  }
}
```

