package com.pandoaspen.mercury.common.scheduler;

public interface IMercuryScheduler {


    IMercuryTask runTask(Runnable task);

    IMercuryTask runTaskAsynchronously(Runnable task);

    IMercuryTask runTaskLater(Runnable task, long delay);

    IMercuryTask runTaskLaterAsynchronously(Runnable task, long delay);

    IMercuryTask runTaskTimer(Runnable task, long delay, long period);

    IMercuryTask runTaskTimerAsynchronously(Runnable task, long delay, long period);

}
