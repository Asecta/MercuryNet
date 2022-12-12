package com.pandoaspen.mercury.bukkit.command;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import com.pandoaspen.mercury.bukkit.MercuryBukkitPlugin;
import com.pandoaspen.mercury.bukkit.configuration.LangConfig;
import com.pandoaspen.mercury.common.service.healthcheck.PlayerQuery;
import org.bukkit.command.CommandSender;

@CommandAlias("find")
public class FindCommand extends AbstractMercuryCommand {

    public FindCommand(MercuryBukkitPlugin plugin) {
        super(plugin);
    }

    @Default
    public void defaultCommand(CommandSender sender, String queryTarget) {
        LangConfig.FindLang findLang = getLangConfig().getFindLang();

        if (sender.getName().equalsIgnoreCase(queryTarget)) {
            sendMessage(sender, findLang.getSelf());
            return;
        }

        PlayerQuery result = getHealthCheckService().findPlayerInfo(queryTarget);
        if (result == null || !result.getPlayerInfo().isVisible()) {
            sendMessage(sender, findLang.getNotFound(), "player", queryTarget);
            return;
        }

        sendMessage(sender, findLang.getOnline(), "server", result.getServerInfo().getServerId(), "player", result.getPlayerInfo().getName());
    }
}
