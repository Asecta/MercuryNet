package com.pandoaspen.mercury.bukkit.command;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import com.pandoaspen.mercury.bukkit.MercuryBukkitPlugin;
import com.pandoaspen.mercury.bukkit.configuration.LangConfig;
import com.pandoaspen.mercury.common.service.healthcheck.PlayerQuery;
import com.pandoaspen.mercury.common.service.redirect.RedirectService;
import com.pandoaspen.mercury.common.service.redirect.model.RedirectReplyMessage;
import com.pandoaspen.mercury.common.service.redirect.model.RedirectStatus;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

@CommandAlias("join")
public class JoinCommand extends AbstractMercuryCommand {

    private final RedirectService redirectService;
    private final LangConfig.JoinLang joinLang;

    public JoinCommand(MercuryBukkitPlugin plugin) {
        super(plugin);
        this.redirectService = getSubService(RedirectService.class);
        this.joinLang = getLangConfig().getJoinLang();
    }

    @Default
    public void defaultCommand(Player sender, String target) {

        sendMessage(sender, joinLang.getLookup(), "player", target);

        PlayerQuery query = getHealthCheckService().findPlayerInfo(target);
        if (query == null || !query.getPlayerInfo().isVisible()) {
            sendMessage(sender, joinLang.getNotFound());
            return;
        }

        String server = query.getServerInfo().getServerId();
        CompletableFuture<RedirectReplyMessage> future = redirectService.redirect(sender.getUniqueId(), server, sender.getUniqueId());
        future.thenAccept(reply -> handleReply(sender, target, reply));
    }


    private void handleReply(Player sender, String target, RedirectReplyMessage reply) {
        System.out.println("Received reply");
        RedirectStatus status = reply.getRedirectStatus();
        System.out.println("Status " + status);
        String statusMessage = getStatusMessage(status);
        System.out.println("StatusMessage:" + statusMessage);
        sendMessage(sender, statusMessage,
                "player", target,
                      "server", reply.getServerId());
    }

    private String getStatusMessage(RedirectStatus status) {
        switch (status) {
            case UNKNOWN:
            case CONNECTION_IN_PROGRESS:
            case SERVER_DISCONNECTED:
                return joinLang.getErrorLang().getErrorUnknown();
            case CONNECTION_CANCELLED:
            case SERVER_NOT_FOUND:
                return joinLang.getErrorLang().getErrorError();
            case ALREADY_CONNECTED:
                return joinLang.getErrorLang().getErrorSameServer();
            case DISCONNECT_WHITELIST:
                return joinLang.getErrorLang().getErrorWhitelist();
            case PLAYER_NOT_FOUND:
                return joinLang.getNotFound();
            case SUCCESS:
                return joinLang.getJoin();
        }
        return joinLang.getErrorLang().getErrorUnknown();
    }


}
