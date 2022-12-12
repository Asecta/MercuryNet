package com.pandoaspen.mercury.common.service.redirect;

import com.pandoaspen.mercury.common.service.redirect.model.RedirectStatus;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface IRedirectHandler {
    CompletableFuture<RedirectStatus> redirect(UUID uuid, String server);
    boolean isConnected(UUID uuid);
}
