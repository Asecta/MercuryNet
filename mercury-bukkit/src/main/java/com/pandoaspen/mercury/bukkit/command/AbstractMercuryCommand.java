package com.pandoaspen.mercury.bukkit.command;

import co.aikar.commands.BaseCommand;
import com.pandoaspen.mercury.bukkit.MercuryBukkitPlugin;
import com.pandoaspen.mercury.bukkit.configuration.LangConfig;
import com.pandoaspen.mercury.bukkit.utils.MessageUtils;
import com.pandoaspen.mercury.common.service.IMercuryService;
import com.pandoaspen.mercury.common.service.MercurySubService;
import com.pandoaspen.mercury.common.service.healthcheck.AbstractHealthCheckService;
import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collection;

public class AbstractMercuryCommand extends BaseCommand {

    @Getter private final MercuryBukkitPlugin plugin;
    @Getter private final IMercuryService mercuryService;
    @Getter private final AbstractHealthCheckService healthCheckService;

    @Getter private final LangConfig langConfig;

    private final BukkitScheduler scheduler;

    public AbstractMercuryCommand(MercuryBukkitPlugin plugin) {
        this.plugin = plugin;
        this.mercuryService = plugin.getMercuryService();
        this.healthCheckService = mercuryService.getSubService(AbstractHealthCheckService.class);
        this.scheduler = plugin.getServer().getScheduler();
        this.langConfig = plugin.getLangConfig();
    }

    public final <T extends MercurySubService> T getSubService(Class<T> clazz) {
        return mercuryService.getSubService(clazz);
    }

    public final String format(String template, Object... args) {
        return ChatColor.translateAlternateColorCodes('&', String.format(template, args));
    }

    // Scheduler stuff

    public BukkitTask runTask(Runnable task) {
        return scheduler.runTask(plugin, task);
    }

    public BukkitTask runTaskAsynchronously(Runnable task) {
        return scheduler.runTaskAsynchronously(plugin, task);
    }

    public BukkitTask runTaskLater(Runnable task, long delay) {
        return scheduler.runTaskLater(plugin, task, delay);
    }

    public BukkitTask runTaskLaterAsynchronously(Runnable task, long delay) {
        return scheduler.runTaskLaterAsynchronously(plugin, task, delay);
    }

    public BukkitTask runTaskTimer(Runnable task, long delay, long period) {
        return scheduler.runTaskTimer(plugin, task, delay, period);
    }

    public BukkitTask runTaskTimerAsynchronously(Runnable task, long delay, long period) {
        return scheduler.runTaskTimerAsynchronously(plugin, task, delay, period);
    }

    public void sendMessage(CommandSender receiver, String msg, String... args) {
        MessageUtils.sendMessage(receiver, msg, args);
    }

    public void sendMessage(CommandSender receiver, BaseComponent[] message) {
        MessageUtils.sendMessage(receiver, message);
    }

    public void sendMessage(CommandSender receiver, Collection<BaseComponent[]> message) {
        MessageUtils.sendMessage(receiver, message);
    }

    public BaseComponent[] parseMessage(String msg, String... args) {
        return MessageUtils.parseMessage(msg, args);
    }


    public String colorize(String input) {
        return MessageUtils.colorize(input);
    }

    public BaseComponent[] parseMessage(String msg, Object... args) {
        return MessageUtils.parseMessage(msg, args);
    }

    public String combine(String msg, String... args) {
        return MessageUtils.combine(msg, args);
    }

    public BaseComponent[] flattenMessage(Collection<BaseComponent[]> collection) {
        return MessageUtils.flattenMessage(collection);
    }
}
