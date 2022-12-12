package com.pandoaspen.mercury.common.service.healthcheck.model;

import com.pandoaspen.mercury.common.serializer.HzGsonSerializer;

public class ServerInfoHzSerializer extends HzGsonSerializer<ServerInfo> {
    public ServerInfoHzSerializer() {
        super(3, ServerInfo.class);
    }
}
