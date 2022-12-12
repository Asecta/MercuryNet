package com.pandoaspen.mercury.velocity.service.healthcheck;

import com.pandoaspen.mercury.common.service.IMercuryService;
import com.pandoaspen.mercury.common.service.healthcheck.AbstractHealthCheckService;
import com.pandoaspen.mercury.common.service.healthcheck.AbstractHealthcheckHeartbeat;
import com.pandoaspen.mercury.common.service.healthcheck.model.ServerType;
import com.pandoaspen.mercury.velocity.MercuryPlugin;

public class VelocityHealthCheckService extends AbstractHealthCheckService {

    private final MercuryPlugin plugin;

    public VelocityHealthCheckService(IMercuryService mercuryService, MercuryPlugin plugin) {
        super(mercuryService);
        this.plugin = plugin;
    }

    @Override
    public ServerType getServerType() {
        return ServerType.VELOCITY;
    }

    @Override
    protected void forceShutdown() {
        plugin.getServer().shutdown();
    }

    @Override
    protected AbstractHealthcheckHeartbeat initializeHeartbeat() {
        return new VelocityHealthCheckHeartbeat(this, plugin);
    }
}
