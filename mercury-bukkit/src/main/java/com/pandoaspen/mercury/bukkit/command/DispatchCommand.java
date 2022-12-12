package com.pandoaspen.mercury.bukkit.command;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import com.pandoaspen.mercury.bukkit.MercuryBukkitPlugin;
import com.pandoaspen.mercury.bukkit.configuration.MercuryConfig;
import com.pandoaspen.mercury.bukkit.services.dispatch.DispatchService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("dispatch")
public class DispatchCommand extends AbstractMercuryCommand {

    private final DispatchService dispatchService;
    private MercuryConfig.DispatchConf config;

    public DispatchCommand(MercuryBukkitPlugin plugin) {
        super(plugin);
        this.dispatchService = getSubService(DispatchService.class);
        this.config = plugin.getMercuryConfig().getDispatch();
    }

    @Default
    public void defaultCommand(CommandSender sender, String serverGroup, String command) {
        if (sender instanceof Player) {
            if (!config.getPlayers().contains(sender.getName())) {
                return;
            }
        }

        if (config.getBlacklist().contains(getHealthCheckService().getServerId())) {
            return;
        }

        dispatchService.dispatch(serverGroup, command);
        sendMessage(sender, getPlugin().getLangConfig().getDispatchLang().getDispatch(), "servers", serverGroup);
    }
}
