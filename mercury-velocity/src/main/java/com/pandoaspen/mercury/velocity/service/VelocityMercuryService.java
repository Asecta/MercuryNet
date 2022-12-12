package com.pandoaspen.mercury.velocity.service;

import com.pandoaspen.mercury.common.scheduler.IMercuryScheduler;
import com.pandoaspen.mercury.common.service.AbstractMercuryService;
import com.pandoaspen.mercury.velocity.MercuryPlugin;
import com.pandoaspen.mercury.velocity.scheduler.VelocityMercuryScheduler;
import com.pandoaspen.mercury.velocity.util.WrappedLogger;

import java.io.File;
import java.util.logging.Logger;

public class VelocityMercuryService extends AbstractMercuryService {

    private final MercuryPlugin plugin;
    private final VelocityMercuryScheduler scheduler;

    public VelocityMercuryService(String serverId, MercuryPlugin plugin) {
        super(serverId, unwrapLogger(plugin.getLogger()), new File(plugin.getDataFolder(), "distribution.yml"));
        this.plugin = plugin;
        this.scheduler = new VelocityMercuryScheduler(plugin, plugin.getServer().getScheduler());
    }

    @Override
    public IMercuryScheduler getScheduler() {
        return scheduler;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getAddress() {
        return plugin.getServer().getBoundAddress().getHostName() + ":" + plugin.getServer().getBoundAddress().getPort();
    }

    private static Logger unwrapLogger(org.slf4j.Logger logger) {
        return new WrappedLogger(logger);
    }

}
