package com.pandoaspen.mercury.common.config;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class DistributionConfig {

    @SerializedName("cluster") private String cluster;

}
