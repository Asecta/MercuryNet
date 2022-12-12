package com.pandoaspen.mercury.velocity;

import com.google.inject.Inject;
import com.pandoaspen.mercury.common.dependency.LibraryHandler;
import com.pandoaspen.mercury.common.service.IMercuryService;
import com.pandoaspen.mercury.common.service.redirect.RedirectService;
import com.pandoaspen.mercury.common.utils.ModuleHack;
import com.pandoaspen.mercury.common.utils.Preloader;
import com.pandoaspen.mercury.velocity.config.MercuryVelocityConfig;
import com.pandoaspen.mercury.velocity.service.VelocityMercuryService;
import com.pandoaspen.mercury.velocity.service.discovery.DiscoveryService;
import com.pandoaspen.mercury.velocity.service.healthcheck.VelocityHealthCheckService;
import com.pandoaspen.mercury.velocity.service.redirect.RedirectHandler;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import org.slf4j.Logger;

import java.io.File;
import java.util.UUID;

@Plugin(id = "mercury-velocity", name = "mercury-velocity", version = "1.0",
        description = "Velocity Mercury Integration", authors = {"Asecta"})
@Getter
public class MercuryPlugin {

    private final ProxyServer server;
    private final Logger logger;

    private MercuryVelocityConfig mercuryConfig;

    private IMercuryService mercuryService;

    @Inject
    public MercuryPlugin(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;

        logger.info("Preloading classes");
        File file = Preloader.findPluginJarFile(new File("plugins"), this.getClass().getName());
        Preloader.preloadClasses(file);
        ModuleHack.openModuleAccess();

        logger.info("Mercury initializing");
        initializeMercury();

        

    }

    private void initializeMercury() {
        String serverId = "proxy-" + UUID.randomUUID().hashCode();
        logger.info("Assigning this proxy the id: " + serverId);
        this.mercuryService = new VelocityMercuryService(serverId, this);

        this.mercuryService.addSubservice(new VelocityHealthCheckService(mercuryService, this));
        this.mercuryService.addSubservice(new DiscoveryService(mercuryService, this));
        this.mercuryService.addSubservice(new RedirectService(mercuryService, new RedirectHandler(this)));
    }


    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        this.mercuryService.startup();
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        this.mercuryService.shutdown();
    }

    public File getDataFolder() {
        return new File("plugins", getClass().getAnnotation(Plugin.class).id());
    }
}