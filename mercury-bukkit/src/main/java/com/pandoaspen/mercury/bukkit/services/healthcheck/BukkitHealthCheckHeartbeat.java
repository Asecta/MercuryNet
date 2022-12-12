package com.pandoaspen.mercury.bukkit.services.healthcheck;

import com.pandoaspen.mercury.bukkit.MercuryBukkitPlugin;
import com.pandoaspen.mercury.bukkit.configuration.MercuryConfig;
import com.pandoaspen.mercury.common.service.healthcheck.AbstractHealthCheckService;
import com.pandoaspen.mercury.common.service.healthcheck.AbstractHealthcheckHeartbeat;
import com.pandoaspen.mercury.common.service.healthcheck.model.PlayerInfo;
import com.pandoaspen.mercury.common.service.healthcheck.model.ServerMetrics;
import com.sun.management.OperatingSystemMXBean;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class BukkitHealthCheckHeartbeat extends AbstractHealthcheckHeartbeat {

    private final MercuryBukkitPlugin plugin;
    private final OperatingSystemMXBean operatingSystemMXBean;
    private final MemoryMXBean memoryMXBean;

    public BukkitHealthCheckHeartbeat(AbstractHealthCheckService healthCheckService, MercuryBukkitPlugin plugin) {
        super(healthCheckService);
        this.plugin = plugin;
        this.operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        this.memoryMXBean = ManagementFactory.getMemoryMXBean();
        this.getLocalServerInfo().setGroups(getLocalServerGroups());
    }

    private Set<String> getLocalServerGroups() {
        Set<String> localGroups = new HashSet<>();
        plugin.getMercuryConfig().getGroups().forEach((groupName, groupConf) -> {
            if (!groupConf.getServers().contains(getServerId())) return;
            localGroups.add(groupName);
            localGroups.addAll(groupConf.getAliases());
        });
        return localGroups;
    }

    @Override
    protected void updateLocalServerInfo(long currentTime) {
        super.updateLocalServerInfo(currentTime);
        this.getLocalServerInfo().setWhitelisted(Bukkit.hasWhitelist());
    }

    @Override
    protected void doUpdateTick(long currentTime) {
        super.doUpdateTick(currentTime);
        this.updateMetrics();
    }

    private void updateMetrics() {
        ServerMetrics serverMetrics = getLocalServerInfo().getServerMetrics();

        ServerMetrics.OSMetrics osMetrics = serverMetrics.getSystemMetrics();
        ServerMetrics.MemoryMetrics onHeap = serverMetrics.getOnHeapMemoryMetrics();
        ServerMetrics.MemoryMetrics offHeap = serverMetrics.getOffHeapMemoryMetrics();

        osMetrics.setFreePhysicalMemory(operatingSystemMXBean.getFreePhysicalMemorySize());
        osMetrics.setCommittedVirtualMemory(operatingSystemMXBean.getCommittedVirtualMemorySize());
        osMetrics.setFreeSwapSpaceSize(operatingSystemMXBean.getFreeSwapSpaceSize());
        osMetrics.setProcessCpuLoad(operatingSystemMXBean.getProcessCpuLoad());
        osMetrics.setProcessCpuTime(operatingSystemMXBean.getProcessCpuTime());
        osMetrics.setSystemCpuLoad(operatingSystemMXBean.getSystemCpuLoad());
        osMetrics.setTotalPhysicalMemorySize(operatingSystemMXBean.getFreePhysicalMemorySize());
        osMetrics.setTotalSwapSpaceSize(operatingSystemMXBean.getTotalSwapSpaceSize());

        MemoryUsage onHeapUsage = memoryMXBean.getHeapMemoryUsage();
        MemoryUsage offHeapUsage = memoryMXBean.getNonHeapMemoryUsage();

        onHeap.setCommitted(onHeapUsage.getCommitted());
        onHeap.setInitial(onHeapUsage.getInit());
        onHeap.setMax(onHeapUsage.getMax());
        onHeap.setUsed(onHeapUsage.getUsed());

        offHeap.setCommitted(offHeapUsage.getCommitted());
        offHeap.setInitial(offHeapUsage.getInit());
        offHeap.setMax(offHeapUsage.getMax());
        offHeap.setUsed(offHeapUsage.getUsed());
    }


    @Override
    protected boolean updateOnlinePlayers() {
        Set<UUID> onlineUUIDS = Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).collect(Collectors.toSet());
        Set<UUID> trackedUUIDS = this.getLocalServerInfo().getOnlinePlayers().stream().map(PlayerInfo::getUuid)
                .collect(Collectors.toSet());

        boolean hasUpdated = false;

        for (PlayerInfo playerInfo : this.getLocalServerInfo().getOnlinePlayers()) {
            if (!onlineUUIDS.contains(playerInfo.getUuid())) {
                this.getLocalServerInfo().getOnlinePlayers().remove(playerInfo);
                hasUpdated = true;
            }
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!trackedUUIDS.contains(player.getUniqueId())) {
                PlayerInfo playerInfo =
                        new PlayerInfo(player.getUniqueId(), player.getName(), System.currentTimeMillis());
                playerInfo.setStaff(player.hasPermission("mercury.staff"));
                this.getLocalServerInfo().getOnlinePlayers().add(playerInfo);
                hasUpdated = true;
            }
        }

        return hasUpdated;
    }
}
