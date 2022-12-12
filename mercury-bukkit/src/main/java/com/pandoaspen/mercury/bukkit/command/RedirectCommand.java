package com.pandoaspen.mercury.bukkit.command;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import com.pandoaspen.mercury.bukkit.MercuryBukkitPlugin;
import com.pandoaspen.mercury.common.service.healthcheck.PlayerQuery;
import com.pandoaspen.mercury.common.service.healthcheck.model.PlayerInfo;
import com.pandoaspen.mercury.common.service.redirect.RedirectService;
import com.pandoaspen.mercury.common.service.redirect.model.RedirectReplyMessage;
import com.pandoaspen.mercury.common.service.redirect.model.RedirectStatus;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@CommandAlias("redirectplayer")
public class RedirectCommand extends AbstractMercuryCommand {

    private final RedirectService redirectService;

    public RedirectCommand(MercuryBukkitPlugin plugin) {
        super(plugin);
        this.redirectService = plugin.getMercuryService().getSubService(RedirectService.class);
    }

    @Subcommand("redirect")
    @CommandPermission("mercury.redirect")
    @CommandAlias("redirect")
    public void redirectCommand(CommandSender sender, String playerName, String serverGroup) {
//        sendMessage(sender, "Redirecting " + playerName);
        PlayerQuery query = getHealthCheckService().findPlayerInfo(playerName);

        if (query == null) {
//            sendMessage(sender, "Player not found");
            return;
        }

//        sendMessage(sender, "Redirecting " + playerName + " to " + serverGroup);

        UUID senderUUID = sender instanceof Player ? ((Player) sender).getUniqueId() : new UUID(0, 0);
        CompletableFuture<RedirectReplyMessage> future = redirectService.redirect(senderUUID, serverGroup, query.getPlayerInfo().getUuid());

        future.thenAccept((reply) -> {
            RedirectStatus status = reply.getRedirectStatus();
            if (status == RedirectStatus.SUCCESS) {
//                sendMessage(sender, "Player redirected");
            } else {
//                sendMessage(sender, "Redirect failed: " + status.toString());
            }
        });
    }

    @Subcommand("redirectall")
    @CommandPermission("mercury.redirectall")
    @CommandAlias("redirectall")
    public void redirectAllCommand(CommandSender sender, String serverGroup) {
        //        sendMessage(sender, "Redirecting all players too " + serverGroup);
        UUID senderUUID = sender instanceof Player ? ((Player) sender).getUniqueId() : new UUID(0, 0);
        UUID[] uuids = getHealthCheckService().getLocalServerInfo().getOnlinePlayers().stream().map(PlayerInfo::getUuid).toArray(UUID[]::new);
        redirectService.redirect(senderUUID, serverGroup, uuids);
    }
}