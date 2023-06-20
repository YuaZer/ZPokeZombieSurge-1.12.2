package io.github.yuazer.zpokezombiesurge;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class SurgeLoad {
    public static void Load(JavaPlugin plugin, CommandExecutor cmd, Listener listener) {
        Bukkit.getPluginCommand("zpokezombiesurge").setExecutor(cmd);
        Bukkit.getPluginManager().registerEvents(listener, plugin);
        Bukkit.getLogger().info(String.format("§e[§b%s§e] §f已加载", plugin.getName()));
        Bukkit.getLogger().info("§b作者:§eZ菌[QQ:1109132]");
        Bukkit.getLogger().info("§b版本:§e" + plugin.getDescription().getVersion());
    }
}
