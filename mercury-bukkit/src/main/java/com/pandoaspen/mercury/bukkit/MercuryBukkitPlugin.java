package com.pandoaspen.mercury.bukkit;

import com.pandoaspen.mercury.bukkit.configuration.LangConfig;
import com.pandoaspen.mercury.bukkit.configuration.MercuryConfig;
import com.pandoaspen.mercury.bukkit.managers.RestartManager;
import com.pandoaspen.mercury.bukkit.managers.ScheduledMessageManager;
import com.pandoaspen.mercury.bukkit.placeholder.PlaceholderManager;
import com.pandoaspen.mercury.bukkit.services.BukkitMercuryService;
import com.pandoaspen.mercury.bukkit.services.alert.AlertService;
import com.pandoaspen.mercury.bukkit.services.dispatch.DispatchService;
import com.pandoaspen.mercury.bukkit.services.healthcheck.BukkitHealthCheckService;
import com.pandoaspen.mercury.bukkit.services.staffchat.StaffChatService;
import com.pandoaspen.mercury.common.service.IMercuryService;
import com.pandoaspen.mercury.common.service.MercurySubService;
import com.pandoaspen.mercury.common.service.redirect.RedirectService;
import com.pandoaspen.mercury.common.utils.ConfigLoader;
import com.pandoaspen.mercury.common.utils.ModuleHack;
import com.pandoaspen.mercury.common.utils.Preloader;
import lombok.Getter;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;

public final class MercuryBukkitPlugin extends JavaPlugin {

    @Getter private IMercuryService mercuryService;
    @Getter private MercuryConfig mercuryConfig;
    @Getter private LangConfig langConfig;
    @Getter private RestartManager restartManager;
    @Getter private ScheduledMessageManager scheduledMessageManager;
    @Getter private PlaceholderManager placeholderManager;

    @Getter private List<Function<IMercuryService, MercurySubService>> serviceConstructors;

    public MercuryBukkitPlugin() {
        ModuleHack.openModuleAccess();
        getLogger().info("Preloading classes...");
        Preloader.preloadClasses(getFile());
    }

    @Override
    public void onLoad() {
        this.serviceConstructors = new LinkedList<>();
        serviceConstructors.add(mercuryService -> new BukkitHealthCheckService(mercuryService, this));
        serviceConstructors.add(mercuryService -> new StaffChatService(mercuryService, this));
        serviceConstructors.add(mercuryService -> new AlertService(mercuryService, this));
        serviceConstructors.add(mercuryService -> new RedirectService(mercuryService, null));
        serviceConstructors.add(mercuryService -> new DispatchService(mercuryService));
    }

    @Override
    public void onEnable() {
        loadConfigs();
        initializeMercury();
        initializeManagers();
        initializeCommands();
        getServer().getServicesManager()
                .register(IMercuryService.class, this.mercuryService, this, ServicePriority.Normal);
    }

    @Override
    public void onDisable() {
        this.mercuryService.shutdown();
        this.scheduledMessageManager.shutdown();
        this.restartManager.shutdown();
        this.placeholderManager.shutdown();
    }

    private void initializeManagers() {
        this.restartManager = new RestartManager(this);
        this.restartManager.startup();

        this.scheduledMessageManager = new ScheduledMessageManager(this);
        this.scheduledMessageManager.startup();

        this.placeholderManager = new PlaceholderManager(this);
        this.placeholderManager.startup();
    }


    private void loadConfigs() {
        this.mercuryConfig = ConfigLoader.load(getDataFolder(), "config.yml", MercuryConfig.class);
        this.langConfig = ConfigLoader.load(getDataFolder(), "lang.yml", LangConfig.class);
    }

    private void initializeCommands() {
        MercuryCommandManager commandManager = new MercuryCommandManager(this);
        commandManager.initialize();
        getServer().getPluginManager().registerEvents(commandManager, this);
    }

    private void initializeMercury() {
        this.mercuryService =
                new BukkitMercuryService(getServerName(), this, new File(getDataFolder(), "distribution.yml"));

        serviceConstructors.forEach(sc -> this.mercuryService.addSubservice(sc.apply(mercuryService)));

        this.mercuryService.startup();
    }

    private String getServerName() {
        String mercuryId = System.getProperty("mercuryId");

        if (mercuryId != null) {
            return mercuryId;
        }

        Properties properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream("server.properties")) {
            properties.load(fileInputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String serverName = properties.getProperty("server-name");
        if (serverName != null && !serverName.isEmpty()) {
            return serverName;
        }

        return getServer().getIp() + ":" + getServer().getPort();
    }


    // Preloads all the classes in this plugin. This ensures we do not get NoClassDef exceptions if the plugin is
    // overwritten (if updated when the server is running).
    // Useful for NFS


}
