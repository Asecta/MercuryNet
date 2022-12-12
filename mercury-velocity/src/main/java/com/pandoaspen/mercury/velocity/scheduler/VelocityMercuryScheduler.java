package com.pandoaspen.mercury.velocity.scheduler;

import com.pandoaspen.mercury.common.scheduler.IMercuryScheduler;
import com.pandoaspen.mercury.common.scheduler.IMercuryTask;
import com.pandoaspen.mercury.velocity.MercuryPlugin;
import com.velocitypowered.api.scheduler.Scheduler;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class VelocityMercuryScheduler implements IMercuryScheduler {

    private final MercuryPlugin plugin;
    private final Scheduler scheduler;

    private final ExecutorService pool = Executors.newCachedThreadPool();

    @Override
    public IMercuryTask runTask(Runnable task) {
        return new VelocityMercuryTask(scheduler.buildTask(plugin, task).schedule());
    }

    @Override
    public IMercuryTask runTaskAsynchronously(Runnable task) {
        return new AsyncVelocityMercuryTask(pool.submit(task));
    }

    @Override
    public IMercuryTask runTaskLater(Runnable task, long delay) {
        return new VelocityMercuryTask(scheduler.buildTask(plugin, task).delay(delay * 50, TimeUnit.MILLISECONDS).schedule());
    }

    @Override
    public IMercuryTask runTaskLaterAsynchronously(Runnable task, long delay) {
        return new AsyncVelocityMercuryTask(pool.submit(wrapTask(task, delay)));
    }

    @Override
    public IMercuryTask runTaskTimer(Runnable task, long delay, long period) {
        return new VelocityMercuryTask(scheduler.buildTask(plugin, task)
                .delay(delay * 50, TimeUnit.MILLISECONDS)
                .repeat(period * 50, TimeUnit.MILLISECONDS)
                .schedule());
    }

    @Override
    public IMercuryTask runTaskTimerAsynchronously(Runnable task, long delay, long period) {
        return new AsyncVelocityMercuryTask(pool.submit(wrapTask(task, delay, period)));
    }

    private Runnable wrapTask(Runnable task, long delay) {
        return () -> {
            try {
                Thread.sleep(delay * 50);
                task.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

    private Runnable wrapTask(Runnable task, long delay, long period) {
        return () -> {
            try {
                Thread.sleep(delay * 50);
                while (true) {
                    task.run();
                    Thread.sleep(period * 50);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

}
