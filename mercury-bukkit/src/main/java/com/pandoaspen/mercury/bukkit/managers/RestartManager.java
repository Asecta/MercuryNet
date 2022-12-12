package com.pandoaspen.mercury.bukkit.managers;

import com.pandoaspen.mercury.bukkit.MercuryBukkitPlugin;
import com.pandoaspen.mercury.bukkit.configuration.LangConfig;
import com.pandoaspen.mercury.bukkit.configuration.MercuryConfig;
import com.pandoaspen.mercury.bukkit.utils.MessageUtils;
import com.pandoaspen.mercury.bukkit.utils.TimeUnit;
import com.pandoaspen.mercury.common.api.Startable;
import com.pandoaspen.mercury.common.service.healthcheck.AbstractHealthCheckService;
import com.pandoaspen.mercury.common.service.healthcheck.model.ServerStatus;
import com.pandoaspen.mercury.common.service.redirect.RedirectService;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class RestartManager implements Startable {
    private final MercuryBukkitPlugin plugin;
    private final LangConfig.RestartLang lang;
    private final MercuryConfig.AutoRestartConf config;
    private final RedirectService redirectService;

    public RestartManager(MercuryBukkitPlugin plugin) {
        this.plugin = plugin;
        this.lang = plugin.getLangConfig().getRestartLang();
        this.config = plugin.getMercuryConfig().getAutoRestart();
        this.redirectService = plugin.getMercuryService().getSubService(RedirectService.class);
    }

    private BukkitTask scheduledRestart;
    private BukkitTask scheduledCommands;
    private List<BukkitTask> scheduledNotifies = new ArrayList<BukkitTask>();
    private int finalCountdown;
    private List<Integer> minutes = new ArrayList<Integer>();
    private boolean notifyChat;
    private boolean notifyTitle;
    private long commandsTime;
    private List<String> commands;
    private long randomizedInterval;
    private boolean restarting;
    private BukkitTask restartCounter;
    private int restartCount;

    private String redirectGroup;

    private long redirectTimeout;

    private AbstractHealthCheckService healthCheckService;

    private long restartOn;

    @Override
    public void startup() {
        reload();
    }

    @Override
    public void shutdown() {
        if (this.scheduledRestart != null && plugin.getServer().getScheduler().isCurrentlyRunning(this.scheduledRestart.getTaskId())) {
            this.scheduledRestart.cancel();
        }
        if (this.scheduledCommands != null && plugin.getServer().getScheduler().isCurrentlyRunning(this.scheduledCommands.getTaskId())) {
            this.scheduledCommands.cancel();
        }
    }

    public void reload() {
        this.minutes.clear();
        this.minutes.addAll(config.getReminder().getMinutes());
        this.notifyChat = config.getReminder().isChat();
        this.notifyTitle = config.getReminder().isTitle();
        this.finalCountdown = config.getReminder().getSeconds();
        this.commandsTime = TimeUnit.toTicks(config.getCommands().getTime());
        this.commands = config.getCommands().getCommands();
        long interval = TimeUnit.toTicks(config.getScheduler().getInterval());
        double randomize = TimeUnit.toTicks(config.getScheduler().getRandomize());
        this.randomizedInterval = (long) (Math.random() * randomize * 2.0 - randomize) + interval;

        this.redirectGroup = config.getRedirect().getGroup();

        this.redirectTimeout = TimeUnit.toTicks(config.getRedirect().getTimeout());
        if (config.getScheduler().isEnabled()) {
            this.reschedule(this.randomizedInterval);
        }
    }

    public void reschedule(long ticks) {
        if (this.scheduledRestart != null) {
            this.scheduledRestart.cancel();
        }
        if (this.scheduledCommands != null) {
            this.scheduledCommands.cancel();
        }
        this.scheduledNotifies.forEach(BukkitTask::cancel);
        this.scheduledNotifies.clear();
        BukkitScheduler s = Bukkit.getScheduler();
        this.scheduledRestart = s.runTaskLater(this.plugin, this::startRestart, ticks);
        this.scheduledCommands = s.runTaskLater(this.plugin, this::runCommands, ticks - this.commandsTime + 200L);
        for (int minute : this.minutes) {
            if (ticks - (long) (minute * 60 * 20) < 0L) continue;
            this.scheduledNotifies.add(s.runTaskLater(this.plugin, () -> this.notify(minute), ticks - (long) (minute * 60 * 20)));
        }
        this.restartOn = System.currentTimeMillis() + ticks * 50L;
    }

    public void notify(int minute) {
        //        System.out.println(MessageUtils.parseMessage(lang.getNotify().getChat(), "minutes", minute + "", "s", minute == 1 ? "" : "s"));
        if (this.notifyChat) {
            MessageUtils.broadcast(lang.getNotify().getChat(), "minutes", minute + "", "s", minute == 1 ? "" : "s");
        }
        if (this.notifyTitle) {
            MessageUtils.broadcastTitle(MessageUtils.parseMessageToString(lang.getNotify().getTitle(), "minutes", minute + "", "s", minute == 1 ? "" : "s"),
                    MessageUtils.parseMessageToString(lang.getNotify().getSubTitle(), "minutes", minute + "", "s", minute == 1 ? "" : "s"));
        }
    }

    public void startRestart() {
        if (this.restarting) {
            return;
        }
        this.restarting = true;
        plugin.getMercuryService().setServerStatus(ServerStatus.SHUTDOWN);
        this.restartCounter = Bukkit.getScheduler().runTaskTimer(this.plugin, this::restartTick, 20L, 20L);
        this.restartCount = this.finalCountdown;
    }

    public void stopRestart() {
        if (!this.restarting) {
            return;
        }
        this.restarting = false;
        plugin.getMercuryService().setServerStatus(ServerStatus.RUNNING);
        this.restartCounter.cancel();
        this.reschedule(this.randomizedInterval);
    }

    public void restartTick() {

        String message = MessageUtils.parseMessageToString(lang.getCountdown().getChat(), "seconds", this.restartCount + "", "s", this.restartCount == 1 ? "" : "s");

        if (this.notifyChat) {
            Bukkit.broadcastMessage(message);
        } else {
            System.out.println(message);
        }

        if (this.notifyTitle) {
            MessageUtils.broadcastTitle(MessageUtils.parseMessageToString(lang.getCountdown().getTitle(), "seconds", this.restartCount + "", "s", this.restartCount == 1 ? "" : "s"),
                    MessageUtils.parseMessageToString(lang.getCountdown().getSubTitle(), "seconds", this.restartCount + "", "s", this.restartCount == 1 ? "" : "s"));
        }
        if (this.restartCount <= 1) {
            this.restart();
        } else {
            --this.restartCount;
        }
    }

    private void restart() {
        this.restartCounter.cancel();
        this.redirectAllPlayers(this.redirectGroup);
        Bukkit.getScheduler().runTaskTimer(this.plugin, () -> {
            if (Bukkit.getOnlinePlayers().size() == 0) {
                Bukkit.shutdown();
            }
        }, 1L, 1L);
        Bukkit.getScheduler().runTaskLater(this.plugin, Bukkit::shutdown, this.redirectTimeout);
    }

    private void redirectAllPlayers(String group) {
        UUID[] uuids = Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).toArray(UUID[]::new);
        redirectService.redirect(new UUID(0, 0), group, uuids);
//         for (Player player : Bukkit.getOnlinePlayers()) {
//             player.kickPlayer("The server was not able to redirect you");
//             this.plugin.getLogger().warning("Couldn't find server for: " + player.getName());
//         }
    }

    public void runCommands() {
        for (String command : this.commands) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }
    }

    public boolean isRestarting() {
        return this.restarting;
    }

    public long getRestartOn() {
        return this.restartOn;
    }
}
