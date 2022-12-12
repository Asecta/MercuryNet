package com.pandoaspen.mercury.common.service.healthcheck.model;

import lombok.Data;
import lombok.ToString;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ToString(onlyExplicitlyIncluded = true)
@Data
public class ServerInfo {

    @ToString.Include private final String serverId;
    @ToString.Include private final ServerType serverType;
    @ToString.Include private final String address;

    private long startTime;
    private double tps;
    private boolean whitelisted;
    private long systemTime;

    private ServerMetrics serverMetrics = new ServerMetrics();

    @ToString.Include private ServerStatus serverStatus = ServerStatus.STOPPED;
    private Set<PlayerInfo> onlinePlayers = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private Set<String> groups = new HashSet<>();

}
