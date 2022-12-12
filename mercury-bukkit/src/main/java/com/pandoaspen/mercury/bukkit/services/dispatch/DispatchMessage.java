package com.pandoaspen.mercury.bukkit.services.dispatch;

import lombok.Data;

import java.util.UUID;

@Data
public class DispatchMessage {

    private final String serverGroup;
    private final String command;

}
