package com.pandoaspen.mercury.common.service.healthcheck.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode
public class PlayerInfo {
    private final UUID uuid;
    private final String name;

    private final long joinTime;

    private long ping = -1;
    private boolean visible = true;
    private boolean staff = false;
}
