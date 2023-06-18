package io.github.yuazer.zpokezombiesurge;

import io.github.yuazer.zpokezombiesurge.Commands.MainCommand;
import io.github.yuazer.zpokezombiesurge.Runnable.SurgeJoin;
import io.github.yuazer.zpokezombiesurge.RunnableUtils.BukkitRunnableManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

public class Main extends JavaPlugin {
    private static Main instance;

    public static Main getInstance() {
        return instance;
    }

    /**
     * 尸潮击杀计数器
     */

    private static HashMap<String, Integer> surgeKill = new HashMap<>();

    public static HashMap<String, Integer> getSurgeKill() {
        return surgeKill;
    }

    /**
     * 玩家所加入的尸潮
     */
    private static HashMap<String, String> playerSurge = new HashMap<>();

    public static HashMap<String, String> getPlayerSurge() {
        return playerSurge;
    }
    /**
     * 玩家个人击杀数
     * */
    private static HashMap<String,Integer> playerKill = new HashMap<>();

    public static HashMap<String, Integer> getPlayerKill() {
        return playerKill;
    }

    /**
     * 尸潮状态
     */
    private static HashMap<String, Boolean> surgeState = new HashMap<>();

    public static HashMap<String, Boolean> getSurgeState() {
        return surgeState;
    }

    private static BukkitRunnableManager runnableManager;

    public static BukkitRunnableManager getRunnableManager() {
        return runnableManager;
    }

    @Override
    public void onLoad() {
        saveDefaultConfig();
        prepareFolder();
    }

    @Override
    public void onEnable() {
        instance = this;
        Bukkit.getPluginCommand("zpokezombiesurge").setExecutor(new MainCommand());
        runnableManager = new BukkitRunnableManager(this);
        reloadRunnable();
        logLoaded(this);
    }

    public void prepareFolder() {
        File surgeDataFolder = new File("plugins/ZPokeZombieSurge/Surge");
        File pokeDataFolder = new File("plugins/ZPokeZombieSurge/pokes");
        if (!surgeDataFolder.exists()) {
            surgeDataFolder.mkdir();
        }
        if (!pokeDataFolder.exists()) {
            pokeDataFolder.mkdir();
        }
        for (String filename : Arrays.stream(surgeDataFolder.listFiles()).map(File::getName).collect(Collectors.toList())) {
            filename = filename.replace(".yml", "");
            if (!surgeState.containsKey(filename)) {
                surgeState.put(filename, false);
            }
        }
    }

    public void reloadRunnable() {
        File surgeDataFolder = new File("plugins/ZPokeZombieSurge/Surge");
        if (surgeDataFolder.listFiles()!=null){
            for (String filename : Arrays.stream(surgeDataFolder.listFiles()).map(File::getName).collect(Collectors.toList())) {
                filename = filename.replace(".yml", "");
                runnableManager.addRunnable(filename, new SurgeJoin(filename));
            }
        }
    }

    @Override
    public void onDisable() {
        logDisable(this);
        surgeKill.clear();
        playerSurge.clear();
        surgeState.clear();
    }

    public static void logLoaded(JavaPlugin plugin) {
        Bukkit.getLogger().info(String.format("§e[§b%s§e] §f已加载", plugin.getName()));
        Bukkit.getLogger().info("§b作者:§eZ菌[QQ:1109132]");
        Bukkit.getLogger().info("§b版本:§e" + plugin.getDescription().getVersion());
    }

    public static void logDisable(JavaPlugin plugin) {
        Bukkit.getLogger().info(String.format("§e[§b%s§e] §c已卸载", plugin.getName()));
    }
}
