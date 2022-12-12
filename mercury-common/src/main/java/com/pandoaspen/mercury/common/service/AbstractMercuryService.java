package com.pandoaspen.mercury.common.service;

import com.hazelcast.config.Config;
import com.hazelcast.config.SerializerConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.instance.impl.DefaultNodeContext;
import com.hazelcast.instance.impl.DefaultNodeExtension;
import com.hazelcast.instance.impl.HazelcastInstanceFactory;
import com.hazelcast.instance.impl.Node;
import com.hazelcast.instance.impl.NodeContext;
import com.hazelcast.instance.impl.NodeExtension;
import com.pandoaspen.mercury.common.config.DistributionConfig;
import com.pandoaspen.mercury.common.service.healthcheck.model.ServerStatus;
import com.pandoaspen.mercury.common.utils.AddressUtils;
import com.pandoaspen.mercury.common.utils.ConfigLoader;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.File;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@RequiredArgsConstructor
public abstract class AbstractMercuryService implements IMercuryService {

    @Getter private final String serverId;
    @Getter private final Logger logger;
    @Getter private final File distributionConfigFile;

    @Getter private HazelcastInstance hazelcast;
    @Getter private boolean isRunning;
    @Getter private Map<String, MercurySubService> subServices = new LinkedHashMap<>();

    @Getter private Thread hazelcastThread;

    @Getter
    @Setter
    private ServerStatus serverStatus = ServerStatus.RUNNING;

    @Getter
    private DistributionConfig distributionConfig;

    @Override
    public void startup() {
        this.distributionConfig = ConfigLoader.load(distributionConfigFile, DistributionConfig.class);

        Config config = new Config();
        config.setInstanceName(getServerId());
        config.setClusterName(distributionConfig.getCluster());

        logger.info("Server ID detected as " + serverId);
        logger.info("Using cluster " + distributionConfig.getCluster());

        //        config.setProperty("hazelcast.logging.type", "none");
        String address = AddressUtils.findLocalNetwork().getHostAddress();
        config.getNetworkConfig().setPublicAddress(address);

        config.getNetworkConfig().getInterfaces().clear();
        config.getNetworkConfig().getInterfaces().addInterface(address.substring(0, address.lastIndexOf('.')) + ".*");

        config.getNetworkConfig().getRestApiConfig().setEnabled(false);
        config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(true);

        config.setProperty("hazelcast.multicast.so ket.set.interface", "true");
        config.setProperty("hazelcast.socket.bind.any", "false");

        config.getNetworkConfig().getInterfaces().setEnabled(true);
        try {
            NetworkInterface iface = getMulticastInterface();
            InterfaceAddress addr = iface.getInterfaceAddresses().stream()
                    .filter(add -> !add.getAddress().getHostAddress().contains(":")).findAny().get();
            String ifaceAddr = addr.getAddress().getHostAddress();
            logger.info("Using interface " + iface);
            logger.info("attaching to address " + ifaceAddr);
            config.getNetworkConfig().getInterfaces().clear();
            config.getNetworkConfig().getInterfaces().addInterface(ifaceAddr);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        startup(config);
    }

    @Override
    public void startup(Config config) {
        getLogger().info("Mercury service starting...");

        getLogger().info("Registering subservice adapters");
        for (MercurySubService subService : subServices.values()) {
            List<SerializerConfig> serializerConfigs = subService.getSerializerConfigs();

            if (serializerConfigs == null) {
                continue;
            }

            for (SerializerConfig serializerConfig : serializerConfigs) {
                config.getSerializationConfig().addSerializerConfig(serializerConfig);
            }
        }

        getLogger().info("Starting distribution service");

        NodeContext nodeContext = new DefaultNodeContext() {
            @Override
            public NodeExtension createNodeExtension(Node node) {
                return new DefaultNodeExtension(node) {
                    @Override public void printNodeInfo() {}
                    @Override public void beforeStart() {}
                    @Override public void afterStart() {}
                };
            }
        };

        this.hazelcastThread = new Thread(() -> {
            this.hazelcast =
                    HazelcastInstanceFactory.newHazelcastInstance(config, config.getInstanceName(), nodeContext);
            getLogger().info("Mercury service started");
            getLogger().info("Starting subservics...");

            for (MercurySubService subService : subServices.values()) {
                startSubService(subService);
            }

            getLogger().info("Done!");
            this.isRunning = true;
        }, "Mercury-Distribution-Thread");

        this.hazelcastThread.run();

        getLogger().warning("Pausing main thread until mercury is started...");
        while (!isRunning) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static NetworkInterface getMulticastInterface() throws SocketException {
        Enumeration<NetworkInterface> interfaceEnumeration = NetworkInterface.getNetworkInterfaces();
        while (interfaceEnumeration.hasMoreElements()) {
            NetworkInterface networkInterface = interfaceEnumeration.nextElement();
            if (!networkInterface.isUp()) continue;
            if (!networkInterface.supportsMulticast()) continue;
            if (networkInterface.isLoopback()) continue;

            if (networkInterface.getName().startsWith("guardian")) continue;
            if (networkInterface.getName().equals("eno1")) continue;

            return networkInterface;
        }
        return null;
    }

    public void startSubService(MercurySubService subService) {
        getLogger().info("Starting subservice " + subService.getName());
        subService.startup();
    }

    @Override
    public void shutdown() {
        getLogger().info("Stopping mercury subservices...");
        for (MercurySubService subService : subServices.values()) {
            getLogger().info("Stopping subservice " + subService.getName());
            subService.shutdown();
        }

        getLogger().info("Mercury service stopping...");
        this.hazelcast.getLifecycleService().shutdown();
        this.hazelcast.shutdown();

        getLogger().info("Mercury service stopped!");
    }

    @Override
    public <T extends MercurySubService> T addSubservice(T service) {
        subServices.put(service.getName(), service);
        return service;
    }

    @Override
    public <T extends MercurySubService> T getSubService(String name) {
        return (T) subServices.get(name);
    }

    @Override
    public <T extends MercurySubService> T getSubService(Class<T> clazz) {
        return subServices.values().stream().filter(ss -> clazz.isAssignableFrom(ss.getClass())).findAny()
                .map(ss -> (T) ss).orElse(null);
    }
}
