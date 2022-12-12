package com.pandoaspen.mercury.bukkit.command;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import com.pandoaspen.mercury.bukkit.MercuryBukkitPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Locale;

import static java.lang.Math.*;

@CommandAlias("mercury")
@CommandPermission("mercury.op")
public class MercuryCommand extends AbstractMercuryCommand {


    public MercuryCommand(MercuryBukkitPlugin plugin) {
        super(plugin);
    }

    @Subcommand("phr")
    public void writeHealthRecords() {
        System.out.println(getHealthCheckService().getServerMapJson(null));
    }


    public static Vector randomLocationWithin(Vector center, double distance) {
        double t = 2 * Math.PI * Math.random();
        double r = Math.sqrt(Math.random());
        double x = r * Math.cos(t) * distance;
        double z = r * Math.sin(t) * distance;
        return center.clone().add(new Vector(x, 0, z));
    }


    @Subcommand("spread")
    public void spreadCommand(Player sender, double distance) {
        World world = sender.getWorld();
        Player[] players = Bukkit.getOnlinePlayers().stream().filter(p -> p != sender).toArray(Player[]::new);

        final double theta = 2 * PI * (1 + sqrt(5d)) / 2d;

        double cX = sender.getLocation().getX();
        double cY = sender.getLocation().getY();
        double cZ = sender.getLocation().getZ();

        for (int i = 0; i < players.length; i++) {
            double dst = pow(i / (players.length - 1d), .5);
            double angle = i * theta;

            double x = dst * cos(angle) * distance;
            double z = dst * sin(angle) * distance;

            players[i].teleport(new Location(world, cX + x, cY, cZ + z));
        }


    }

    @Subcommand("stop")
    public void stop(CommandSender sender) {
        if (!sender.isOp()) return;
        String pid = ManagementFactory.getRuntimeMXBean().getName();
        pid = pid.split("@")[0];

        System.out.println(pid);

        try {
            Runtime.getRuntime().exec("taskkill /F /PID " + pid);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
