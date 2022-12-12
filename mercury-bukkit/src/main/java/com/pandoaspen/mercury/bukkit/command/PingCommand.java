package com.pandoaspen.mercury.bukkit.command;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import com.pandoaspen.mercury.bukkit.MercuryBukkitPlugin;
import com.pandoaspen.mercury.bukkit.utils.TimeUnit;
import com.pandoaspen.mercury.common.service.healthcheck.PlayerQuery;
import org.bukkit.entity.Player;

@CommandAlias("ping|latency")
public class PingCommand extends AbstractMercuryCommand {

    public PingCommand(MercuryBukkitPlugin mercuryBukkitPlugin) {
        super(mercuryBukkitPlugin);
    }

    @Default
    public void defaultCmd(Player sender) {
        PlayerQuery query = getHealthCheckService().findProxyPlayerInfo(sender.getUniqueId());
        if (query == null) return;
        sendMessage(sender, getPlugin().getLangConfig().getPingLang().getPing(), "ping", Long.toString(query.getPlayerInfo().getPing()));
    }
}
