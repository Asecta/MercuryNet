package com.pandoaspen.mercury.common.service.redirect.model;

import lombok.Data;

import java.util.UUID;

@Data
public class RedirectReplyMessage {

    private final UUID requestUUID;

    private final UUID senderUUID;
    private final UUID targetUUID;
    private final String serverId;
    private final RedirectStatus redirectStatus;

}
