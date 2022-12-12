package com.pandoaspen.mercury.bukkit.scheduler;

import com.pandoaspen.mercury.common.scheduler.IMercuryScheduler;
import com.pandoaspen.mercury.common.scheduler.IMercuryTask;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

@RequiredArgsConstructor
public class BukkitMercuryScheduler implements IMercuryScheduler {

    private final Plugin plugin;
    private final BukkitScheduler scheduler;

    @Override
    public IMercuryTask runTask(Runnable task) {
        return new BukkitMercuryTask(scheduler.runTask(plugin, task));
    }

    @Override
    public IMercuryTask runTaskAsynchronously(Runnable task) {
        return new BukkitMercuryTask(scheduler.runTaskAsynchronously(plugin, task));
    }

    @Override
    public IMercuryTask runTaskLater(Runnable task, long delayMs) {
        return new BukkitMercuryTask(scheduler.runTaskLater(plugin, task, delayMs ));
    }

    @Override
    public IMercuryTask runTaskLaterAsynchronously(Runnable task, long delayMs) {
        return new BukkitMercuryTask(scheduler.runTaskLaterAsynchronously(plugin, task, delayMs));
    }

    @Override
    public IMercuryTask runTaskTimer(Runnable task, long delayMs, long periodMs) {
        return new BukkitMercuryTask(scheduler.runTaskTimer(plugin, task, delayMs, periodMs));
    }

    @Override
    public IMercuryTask runTaskTimerAsynchronously(Runnable task, long delayMs, long periodMs) {
        return new BukkitMercuryTask(scheduler.runTaskTimerAsynchronously(plugin, task, delayMs, periodMs));
    }

}
