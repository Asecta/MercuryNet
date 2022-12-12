package com.pandoaspen.mercury.bukkit.command;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import com.pandoaspen.mercury.bukkit.MercuryBukkitPlugin;
import com.pandoaspen.mercury.bukkit.configuration.LangConfig;
import org.bukkit.command.CommandSender;

@CommandAlias("whereami")
public class WhereAmICommand extends AbstractMercuryCommand {

    private LangConfig.WhereAmILang lang;

    public WhereAmICommand(MercuryBukkitPlugin plugin) {
        super(plugin);
        this.lang = plugin.getLangConfig().getWhereAmILang();
    }

    @Default
    public void defaultCommand(CommandSender sender) {
        String server = getHealthCheckService().getServerId();
        sendMessage(sender, lang.getWhereami(), "server", server);
    }
}