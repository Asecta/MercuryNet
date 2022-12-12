package com.pandoaspen.mercury.common.service;

import com.hazelcast.config.SerializerConfig;
import com.hazelcast.core.HazelcastInstance;
import com.pandoaspen.mercury.common.api.Startable;
import com.pandoaspen.mercury.common.scheduler.IMercuryScheduler;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.logging.Logger;

@RequiredArgsConstructor
public abstract class MercurySubService implements Startable {

    @Getter private final IMercuryService mercuryService;
    private Logger logger;

    public HazelcastInstance getHazelcast() {
        return mercuryService.getHazelcast();
    }

    public String getServerId() {
        return mercuryService.getServerId();
    }

    public Logger getLogger() {
        if (logger == null) {
            logger = getLogger(getName(), mercuryService.getLogger());
        }
        return logger;
    }

    public IMercuryScheduler getSchedular() {
        return mercuryService.getScheduler();
    }

    public abstract List<SerializerConfig> getSerializerConfigs();

    public abstract String getName();

    public static Logger getLogger(String name, Logger parent) {
        return Logger.getLogger(parent.getName() + "] [" + name);
    }

}
