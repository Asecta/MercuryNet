package com.pandoaspen.mercury.bukkit.managers;

import com.pandoaspen.mercury.bukkit.MercuryBukkitPlugin;
import com.pandoaspen.mercury.bukkit.configuration.MercuryConfig;
import com.pandoaspen.mercury.bukkit.services.healthcheck.BukkitHealthCheckService;
import com.pandoaspen.mercury.bukkit.utils.MessageUtils;
import com.pandoaspen.mercury.common.api.Startable;
import com.pandoaspen.mercury.common.scheduler.IMercuryTask;
import com.pandoaspen.mercury.common.utils.Duration;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ScheduledMessageManager implements Startable, Runnable, Listener {

    private final MercuryBukkitPlugin plugin;

    private List<String> serverMessages;

    private long messageDelay = 0;
    private long chatMessageCountMax = 0;
    private long messageTimeout = 0;

    private int messageIndex = 0;
    private long lastMessageTime = 0;
    private long chatMessageCount = 0;

    private IMercuryTask task;

    @Override
    public void startup() {
        if (!reload()) return;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.task = plugin.getMercuryService().getScheduler().runTaskTimer(this, 200, 200);
    }

    public boolean reload() {
        MercuryConfig.ScheduledMessagesConf config = plugin.getMercuryConfig().getScheduledMessagesConfig();

        if (!config.isEnabled()) {
            return false;
        }

        this.messageDelay = Duration.parseDuration(config.getSchedulerConfig().getDelay());
        this.messageTimeout = Duration.parseDuration(config.getSchedulerConfig().getChatMessagesTimeout());
        this.chatMessageCountMax = config.getSchedulerConfig().getChatMessages();

        BukkitHealthCheckService healthCheckService =
                plugin.getMercuryService().getSubService(BukkitHealthCheckService.class);
        this.serverMessages = new ArrayList<>();
        for (MercuryConfig.ScheduledMessagesConf.MessageConf messageConfig : config.getMessages()) {
            if (!healthCheckService.isLocalServerInGroup(messageConfig.getGroup())) continue;
            this.serverMessages.addAll(messageConfig.getMessages());
        }

        this.messageTimeout = 100;
        return true;
    }

    @Override
    public void shutdown() {
        if (this.task != null) {
            this.task.cancel();
        }
    }

    public String getNextMessage() {
        if (this.serverMessages.isEmpty()) return null;
        String message = this.serverMessages.get(this.messageIndex++ % this.serverMessages.size());
        return message;
    }


    @EventHandler
    public void onChatMessage(AsyncPlayerChatEvent event) {
        this.chatMessageCount++;
    }

    @Override
    public void run() {
        long lastMessageDelta = System.currentTimeMillis() - lastMessageTime;
        if (lastMessageDelta >= messageTimeout ||
                (chatMessageCount >= chatMessageCountMax && lastMessageTime >= messageDelay)) {
            sendNextMessage();
            return;
        }
    }

    private void sendNextMessage() {
        String message = getNextMessage();

        for (Player player : Bukkit.getOnlinePlayers()) {
            BaseComponent[] baseComponents = MessageUtils.parseMessage(message, "player", player.getName());
            MessageUtils.sendMessage(player, baseComponents);
        }

        this.lastMessageTime = System.currentTimeMillis();
        this.chatMessageCount = 0;
    }
}
