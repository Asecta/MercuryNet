package com.pandoaspen.mercury.velocity.scheduler;

import com.pandoaspen.mercury.common.scheduler.IMercuryTask;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.Future;

@RequiredArgsConstructor
public class AsyncVelocityMercuryTask implements IMercuryTask {

    private final Future<?> future;

    @Override
    public boolean isSync() {
        return true;
    }

    @Override
    public void cancel() {
        future.cancel(true);
    }
}
