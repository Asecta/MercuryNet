package com.pandoaspen.mercury.bukkit.services.dispatch;

import com.hazelcast.config.SerializerConfig;
import com.hazelcast.topic.ITopic;
import com.pandoaspen.mercury.common.service.IMercuryService;
import com.pandoaspen.mercury.common.service.MercurySubService;
import com.pandoaspen.mercury.common.service.healthcheck.AbstractHealthCheckService;
import org.bukkit.Bukkit;

import java.util.Arrays;
import java.util.List;

public class DispatchService extends MercurySubService {

    private ITopic<DispatchMessage> dispatchTopic;
    private AbstractHealthCheckService healthCheckService;

    public DispatchService(IMercuryService mercuryService) {
        super(mercuryService);
    }

    @Override
    public String getName() {
        return "DispatchService";
    }

    @Override
    public void startup() {
        this.healthCheckService = getMercuryService().getSubService(AbstractHealthCheckService.class);
        this.dispatchTopic = getHazelcast().getTopic("dispatch");
        this.dispatchTopic.addMessageListener(msg -> onDispatchMessage(msg.getMessageObject()));
    }

    @Override
    public void shutdown() {

    }

    @Override
    public List<SerializerConfig> getSerializerConfigs() {
        return Arrays.asList(new SerializerConfig().setTypeClass(DispatchMessage.class).setImplementation(new DispatchMessageSerializer()));
    }

    public void onDispatchMessage(DispatchMessage message) {
        if (!healthCheckService.isLocalServerInGroup(message.getServerGroup())) {
            return;
        }
        String command = message.getCommand();
        getSchedular().runTask(() -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        });
    }

    public void dispatch(String serverGroup, String command) {
        dispatchTopic.publishAsync(new DispatchMessage(serverGroup, command));
    }
}
