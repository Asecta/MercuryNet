package com.pandoaspen.mercury.common.service.healthcheck;

import com.pandoaspen.mercury.common.service.healthcheck.model.PlayerInfo;
import com.pandoaspen.mercury.common.service.healthcheck.model.ServerInfo;
import com.pandoaspen.mercury.common.service.healthcheck.model.ServerStatus;
import com.pandoaspen.mercury.common.service.healthcheck.model.ServerType;
import com.pandoaspen.mercury.common.utils.TPSUtil;
import lombok.Getter;

import java.util.Map;
import java.util.Set;

public abstract class AbstractHealthcheckHeartbeat implements Runnable {

    private static final long UPDATE_OTHER_FREQUENCY = 5 * 1000;
    @Getter private final AbstractHealthCheckService healthCheckService;
    @Getter private final ServerInfo localServerInfo;
    @Getter private final String serverId;
    private final TPSUtil tpsUtil;

    @Getter private long lastUpdate;
    @Getter private boolean running;

    public AbstractHealthcheckHeartbeat(AbstractHealthCheckService healthCheckService) {
        this.healthCheckService = healthCheckService;
        this.localServerInfo = healthCheckService.getLocalServerInfo();
        this.serverId = healthCheckService.getServerId();
        this.tpsUtil = new TPSUtil();
        this.lastUpdate = System.currentTimeMillis();
        this.running = true;
        healthCheckService.getSchedular().runTaskTimer(tpsUtil, 20, 1);
    }

    @Override
    public final void run() {
        if (!running) return;
        this.doUpdateTick(System.currentTimeMillis());
        healthCheckService.getServerInfoMap().putAsync(serverId, localServerInfo);
    }

    protected void doUpdateTick(long currentTime) {
        this.updateLocalServerInfo(currentTime);
        this.updateOnlinePlayers();
        this.checkOtherServerPlayers();

        if (currentTime - lastUpdate > UPDATE_OTHER_FREQUENCY) {
            this.lastUpdate = currentTime;
            this.checkForDeadServers();
        }
    }

    protected void updateLocalServerInfo(long currentTime) {
        this.localServerInfo.setTps(this.tpsUtil.getTPS());
        this.localServerInfo.setServerStatus(healthCheckService.getMercuryService().getServerStatus());
        this.localServerInfo.setSystemTime(currentTime);
        this.localServerInfo.setWhitelisted(false);
    }

    public void cancel() {
        this.running = false;
    }

    protected void checkOtherServerPlayers() {
        Set<PlayerInfo> onlinePlayers = this.localServerInfo.getOnlinePlayers();

        for (Map.Entry<String, ServerInfo> entry : healthCheckService.getServerInfoMap().entrySet()) {
            ServerInfo remoteServerInfo = entry.getValue();

            if (remoteServerInfo.getServerStatus() == ServerStatus.STOPPED) {
                continue;
            }

            if (remoteServerInfo.getServerType() != ServerType.BUKKIT) {
                continue;
            }

            if (entry.getKey().equals(serverId)) {
                continue;
            }

            boolean updated = remoteServerInfo.getOnlinePlayers().removeIf(onlinePlayers::contains);

            if (updated) {
                healthCheckService.getServerInfoMap().putAsync(entry.getKey(), entry.getValue());
            }
        }
    }

    protected void checkForDeadServers() {
        this.localServerInfo.setSystemTime(System.currentTimeMillis());

        for (Map.Entry<String, ServerInfo> entry : healthCheckService.getServerInfoMap().entrySet()) {

            if (entry.getKey().equals(serverId)) {
                continue;
            }

            if (entry.getValue().getServerStatus() == ServerStatus.STOPPED) {
                continue;
            }

            long lastSeen = entry.getValue().getSystemTime();
            if (lastUpdate - lastSeen < 1000 * 10) {
                continue;
            }

            entry.getValue().setServerStatus(ServerStatus.STOPPED);
            entry.getValue().setTps(0);
            entry.getValue().getOnlinePlayers().clear();

            healthCheckService.getServerInfoMap().putAsync(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, ServerInfo> entry : healthCheckService.getServerInfoMap().entrySet()) {

            if (entry.getKey().equals(serverId)) {
                continue;
            }

            if (entry.getValue().getServerStatus() != ServerStatus.STOPPED) {
                continue;
            }

            long lastSeen = entry.getValue().getSystemTime();
            if (lastUpdate - lastSeen < 1000 * 60) {
                continue;
            }

            healthCheckService.getServerInfoMap().removeAsync(entry.getKey());
        }
    }

    protected abstract boolean updateOnlinePlayers();

}
