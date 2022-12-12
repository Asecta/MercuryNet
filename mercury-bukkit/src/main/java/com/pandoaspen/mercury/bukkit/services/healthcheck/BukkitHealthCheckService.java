package com.pandoaspen.mercury.bukkit.services.healthcheck;

import com.pandoaspen.mercury.bukkit.MercuryBukkitPlugin;
import com.pandoaspen.mercury.common.service.IMercuryService;
import com.pandoaspen.mercury.common.service.healthcheck.AbstractHealthCheckService;
import com.pandoaspen.mercury.common.service.healthcheck.AbstractHealthcheckHeartbeat;
import com.pandoaspen.mercury.common.service.healthcheck.model.ServerType;
import org.bukkit.Bukkit;

public class BukkitHealthCheckService extends AbstractHealthCheckService {

    private final MercuryBukkitPlugin plugin;

    public BukkitHealthCheckService(IMercuryService mercuryService, MercuryBukkitPlugin plugin) {
        super(mercuryService);
        this.plugin = plugin;
    }

    @Override
    public ServerType getServerType() {
        return ServerType.BUKKIT;
    }

    @Override
    protected void forceShutdown() {
        Bukkit.getServer().shutdown();
    }

    @Override
    protected AbstractHealthcheckHeartbeat initializeHeartbeat() {
        return new BukkitHealthCheckHeartbeat(this, plugin);
    }
}
