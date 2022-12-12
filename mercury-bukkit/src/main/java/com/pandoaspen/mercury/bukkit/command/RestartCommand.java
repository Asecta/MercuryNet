package com.pandoaspen.mercury.bukkit.command;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import com.pandoaspen.mercury.bukkit.MercuryBukkitPlugin;
import com.pandoaspen.mercury.bukkit.configuration.LangConfig;
import com.pandoaspen.mercury.bukkit.managers.RestartManager;
import com.pandoaspen.mercury.bukkit.utils.TimeUnit;
import org.bukkit.command.CommandSender;

@CommandAlias("restart|stop")
@CommandPermission("mercury.restart")
public class RestartCommand extends AbstractMercuryCommand {

    private final LangConfig.RestartLang restartLang;
    private final RestartManager restartManager;

    public RestartCommand(MercuryBukkitPlugin plugin) {
        super(plugin);
        this.restartManager = plugin.getRestartManager();
        this.restartLang = plugin.getLangConfig().getRestartLang();
    }

    @Default
    @CommandPermission("mercury.restart")
    public void defaultCommand(CommandSender sender) {
        this.inCommand(sender, "1s");
    }

    @Subcommand("in")
    @CommandPermission("mercury.restart")
    public void inCommand(CommandSender sender, String time) {
        sendMessage(sender, restartLang.getRescheduled());
        this.restartManager.reschedule(TimeUnit.toTicks(time));
    }

    @Subcommand("time")
    @CommandPermission("mercury.restart.time")
    public void timeCommand(CommandSender sender) {
        sendMessage(sender, restartLang.getTime(), "time",
                TimeUnit.longToString(getPlugin().getRestartManager().getRestartOn() - System.currentTimeMillis(),
                        true));
    }

    @Subcommand("stop")
    @CommandPermission("mercury.restart")
    public void stopCommand(CommandSender sender) {
        this.restartManager.stopRestart();
    }
}
