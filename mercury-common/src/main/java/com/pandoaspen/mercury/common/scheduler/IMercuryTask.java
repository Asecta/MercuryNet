package com.pandoaspen.mercury.common.scheduler;

public interface IMercuryTask {

    public boolean isSync();

    public void cancel();
}
