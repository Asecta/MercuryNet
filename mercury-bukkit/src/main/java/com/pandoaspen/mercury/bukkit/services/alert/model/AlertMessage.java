package com.pandoaspen.mercury.bukkit.services.alert.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class AlertMessage {

    private final String senderName;
    private final UUID senderUUID;
    private final AlertType alertType;
    private final String title;

    private String subTitle;
    private String[] groups;
}
