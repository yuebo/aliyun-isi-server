package com.eappcat.isi.server.vo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class SpeechNetwork {

    @JSONField(name = "upgrade_cost")
    private Integer upgradeCost;
    @JSONField(name = "connect_cost")
    private Integer connectCost;
}
