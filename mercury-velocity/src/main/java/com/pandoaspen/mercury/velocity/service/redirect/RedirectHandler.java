package com.pandoaspen.mercury.velocity.service.redirect;

import com.pandoaspen.mercury.common.service.redirect.IRedirectHandler;
import com.pandoaspen.mercury.common.service.redirect.model.RedirectStatus;
import com.pandoaspen.mercury.velocity.MercuryPlugin;
import com.velocitypowered.api.proxy.ConnectionRequestBuilder;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class RedirectHandler implements IRedirectHandler {

    private final MercuryPlugin plugin;

    @Override
    public CompletableFuture<RedirectStatus> redirect(UUID uuid, String serverName) {
        Optional<Player> playerOptional = plugin.getServer().getPlayer(uuid);
        if (!playerOptional.isPresent()) return CompletableFuture.completedFuture(RedirectStatus.PLAYER_NOT_FOUND);

        Optional<RegisteredServer> serverOptional = plugin.getServer().getServer(serverName);
        if (!serverOptional.isPresent()) return CompletableFuture.completedFuture(RedirectStatus.SERVER_NOT_FOUND);

        Player player = playerOptional.get();
        RegisteredServer server = serverOptional.get();

        if (server.getServerInfo().equals(player.getCurrentServer().get().getServerInfo())) {
            return CompletableFuture.completedFuture(RedirectStatus.ALREADY_CONNECTED);
        }

        ConnectionRequestBuilder requestBuilder = player.createConnectionRequest(server);
        return requestBuilder.connect().thenApply(this::switchConnectResult);
    }

    private RedirectStatus switchConnectResult(ConnectionRequestBuilder.Result result) {
        switch (result.getStatus()) {
            case SUCCESS:
                return RedirectStatus.SUCCESS;
            case ALREADY_CONNECTED:
                return RedirectStatus.ALREADY_CONNECTED;
            case CONNECTION_CANCELLED:
                return RedirectStatus.CONNECTION_CANCELLED;
            case CONNECTION_IN_PROGRESS:
                return RedirectStatus.CONNECTION_IN_PROGRESS;
            case SERVER_DISCONNECTED:
                if (result.getReasonComponent().isPresent()) {
                    Component component = result.getReasonComponent().get();
                    if (component instanceof TextComponent) {
                        TextComponent textComponent = (TextComponent) component;
                        if (textComponent.content().toUpperCase().contains("WHITELIST")) {
                            return RedirectStatus.DISCONNECT_WHITELIST;
                        }
                    }
                }

                return RedirectStatus.SERVER_DISCONNECTED;

        }
        return RedirectStatus.UNKNOWN;
    }


    @Override
    public boolean isConnected(UUID uuid) {
        return plugin.getServer().getPlayer(uuid).isPresent();
    }
}
