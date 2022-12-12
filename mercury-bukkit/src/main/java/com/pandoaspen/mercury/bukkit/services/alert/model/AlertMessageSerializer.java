package com.pandoaspen.mercury.bukkit.services.alert.model;

import com.pandoaspen.mercury.common.serializer.HzGsonSerializer;

public class AlertMessageSerializer extends HzGsonSerializer<AlertMessage> {
    public AlertMessageSerializer() {
        super(5, AlertMessage.class);
    }
}
