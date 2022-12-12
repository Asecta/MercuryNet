package com.pandoaspen.mercury.common.service.redirect.model;

import lombok.*;

import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class RedirectMessage {

    private final UUID requestUUID;

    private UUID senderUUID;
    private UUID[] sendingUUIDs;
    private String destinationGroup;

}
