package com.pandoaspen.mercury.bukkit.services.staffchat.model;

import lombok.Data;

import java.util.UUID;

@Data
public final class StaffChatMessage {
    private final String serverId;
    private final UUID playerUUID;
    private final String playerName;
    private final String message;
}
