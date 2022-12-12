package com.pandoaspen.mercury.velocity.scheduler;

import com.pandoaspen.mercury.common.scheduler.IMercuryTask;
import com.velocitypowered.api.scheduler.ScheduledTask;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VelocityMercuryTask implements IMercuryTask {

    private final ScheduledTask scheduledTask;

    @Override
    public boolean isSync() {
        return false;
    }

    @Override
    public void cancel() {

    }
}
