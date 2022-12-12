package com.pandoaspen.mercury.bukkit.scheduler;

import com.pandoaspen.mercury.common.scheduler.IMercuryTask;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import org.bukkit.scheduler.BukkitTask;

@RequiredArgsConstructor
public class BukkitMercuryTask implements IMercuryTask {

    @Delegate private final BukkitTask bukkitTask;

}
