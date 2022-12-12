package com.pandoaspen.mercury.common.service;

import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.pandoaspen.mercury.common.api.Startable;
import com.pandoaspen.mercury.common.scheduler.IMercuryScheduler;
import com.pandoaspen.mercury.common.service.healthcheck.model.ServerStatus;

import java.util.Map;
import java.util.logging.Logger;

public interface IMercuryService extends Startable {
    Logger getLogger();

    String getServerId();

    IMercuryScheduler getScheduler();

    <T extends MercurySubService> T addSubservice(T service);

    <T extends MercurySubService> T getSubService(String name);

    <T extends MercurySubService> T getSubService(Class<T> clazz);

    HazelcastInstance getHazelcast();

    Map<String, MercurySubService> getSubServices();

    ServerStatus getServerStatus();

    void setServerStatus(ServerStatus serverStatus);

    boolean isEnabled();

    String getAddress();

    void startup(Config config);

}
