package com.pandoaspen.mercury.velocity.config;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class MercuryVelocityConfig {

    @SerializedName("disable-velocity-server-command") private boolean disableVelocityServerCommand;

}
