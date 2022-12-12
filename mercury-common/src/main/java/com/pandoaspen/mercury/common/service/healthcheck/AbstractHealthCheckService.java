package com.pandoaspen.mercury.common.service.healthcheck;

import com.google.gson.GsonBuilder;
import com.hazelcast.config.SerializerConfig;
import com.hazelcast.map.IMap;
import com.pandoaspen.mercury.common.service.IMercuryService;
import com.pandoaspen.mercury.common.service.MercurySubService;
import com.pandoaspen.mercury.common.service.healthcheck.model.*;
import lombok.Getter;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public abstract class AbstractHealthCheckService extends MercurySubService {

    @Getter private IMap<String, ServerInfo> serverInfoMap;

    private ServerInfo localServerInfo;

    private AbstractHealthcheckHeartbeat healthcheckHeartbeat;

    public AbstractHealthCheckService(IMercuryService mercuryService) {
        super(mercuryService);
    }

    @Override
    public String getName() {
        return "HealthCheck";
    }

    @Override
    public void startup() {
        this.serverInfoMap = getHazelcast().getMap("healthcheck");
        this.healthcheckHeartbeat = initializeHeartbeat();

        getSchedular().runTaskTimerAsynchronously(healthcheckHeartbeat, 40, 40);

        getSchedular().runTaskLater(() -> {
            getMercuryService().setServerStatus(ServerStatus.RUNNING);
        }, 20);
    }


    public ServerInfo getLocalServerInfo() {
        if (localServerInfo != null) {
            return localServerInfo;
        }

        getLogger().info("Local server info not found. Creating one.");

        String address = getMercuryService().getAddress();

        this.localServerInfo = new ServerInfo(getServerId(), getServerType(), address);
        this.localServerInfo.setServerStatus(ServerStatus.RUNNING);
        this.localServerInfo.setStartTime(System.currentTimeMillis());




        return localServerInfo;
    }

    @Override
    public void shutdown() {
        getMercuryService().setServerStatus(ServerStatus.SHUTDOWN);
        this.healthcheckHeartbeat.run(); // Do one last run with new status
        this.healthcheckHeartbeat.cancel();
    }

    @Override
    public List<SerializerConfig> getSerializerConfigs() {
        return Arrays.asList(
                new SerializerConfig().setTypeClass(ServerInfo.class).setImplementation(new ServerInfoHzSerializer()));
    }

    public List<ServerInfo> getServersInGroup(String group) {
        return getServerInfoMap().values().stream().filter(si -> isInGroup(si, group)).collect(Collectors.toList());
    }

    public boolean isLocalServerInGroup(String group) {
        return isInGroup(getLocalServerInfo(), group);
    }

    public boolean isInGroup(ServerInfo serverInfo, String group) {
        if (group == null) return false;
        if (group.equalsIgnoreCase("ALL")) return true;
        if (group.equalsIgnoreCase("*")) return true;
        if (serverInfo.getServerId().equalsIgnoreCase(group)) return true;
        Set<String> groups = serverInfo.getGroups();
        if (group == null || group.isEmpty()) return false;
        return groups.contains(group);
    }

    public PlayerQuery findPlayerInfo(String targetName) {
        Collection<ServerInfo> serverInfos = getServerInfoMap().values();

        for (ServerInfo serverInfo : serverInfos) {
            if (serverInfo.getServerType() != ServerType.BUKKIT) {
                continue;
            }

            for (PlayerInfo playerInfo : serverInfo.getOnlinePlayers()) {
                if (playerInfo.getName().equalsIgnoreCase(targetName)) {
                    return new PlayerQuery(playerInfo, serverInfo);
                }
            }
        }

        return null;
    }

    public PlayerQuery findProxyPlayerInfo(UUID targetUUID) {
        Collection<ServerInfo> serverInfos = getServerInfoMap().values();
        for (ServerInfo serverInfo : serverInfos) {
            if (serverInfo.getServerType() != ServerType.VELOCITY) {
                continue;
            }
            for (PlayerInfo playerInfo : serverInfo.getOnlinePlayers()) {
                if (playerInfo.getUuid().equals(targetUUID)) {
                    return new PlayerQuery(playerInfo, serverInfo);
                }
            }
        }
        return null;
    }

    public PlayerQuery findPlayerInfo(UUID targetUUID) {
        Collection<ServerInfo> serverInfos = getServerInfoMap().values();
        for (ServerInfo serverInfo : serverInfos) {
            if (serverInfo.getServerType() != ServerType.BUKKIT) {
                continue;
            }
            for (PlayerInfo playerInfo : serverInfo.getOnlinePlayers()) {
                if (playerInfo.getUuid().equals(targetUUID)) {
                    return new PlayerQuery(playerInfo, serverInfo);
                }
            }
        }
        return null;
    }

    public String getServerMapJson(Predicate<ServerInfo> filter) {
        TreeMap<String, ServerInfo> treeMap = new TreeMap<>(getServerInfoMap());
        if (filter != null) treeMap.values().removeIf(filter);
        return new GsonBuilder().create().toJson(treeMap);
    }

    public abstract ServerType getServerType();

    protected abstract void forceShutdown();

    protected abstract AbstractHealthcheckHeartbeat initializeHeartbeat();


}
