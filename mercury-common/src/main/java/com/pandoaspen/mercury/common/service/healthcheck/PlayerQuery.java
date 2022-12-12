package com.pandoaspen.mercury.common.service.healthcheck;

import com.pandoaspen.mercury.common.service.healthcheck.model.PlayerInfo;
import com.pandoaspen.mercury.common.service.healthcheck.model.ServerInfo;
import lombok.Data;

@Data
public class PlayerQuery {

    private final PlayerInfo playerInfo;
    private final ServerInfo serverInfo;

}
