package com.pandoaspen.mercury.bukkit.services.dispatch;

import com.pandoaspen.mercury.common.serializer.HzGsonSerializer;

public class DispatchMessageSerializer extends HzGsonSerializer<DispatchMessage> {
    public DispatchMessageSerializer() {
        super(10, DispatchMessage.class);
    }
}
