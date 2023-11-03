package io.github.yuazer.zpokezombiesurge;

import io.github.yuazer.zpokezombiesurge.RunnableUtils.BukkitRunnableManager;
import io.github.yuazer.zpokezombiesurge.Utils.SocketClient;
import io.github.yuazer.zpokezombiesurge.hook.PapiHook;
import io.github.yuazer.zpokezombiesurge.object.SurgeLocation;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class Main extends JavaPlugin {
    private static Main instance;

    public static Main getInstance() {
        return instance;
    }

    /**
     * 尸潮击杀计数器
     */

//    private static HashMap<String, Integer> surgeKill = new HashMap<>();
//
//    public static HashMap<String, Integer> getSurgeKill() {
//        return surgeKill;
//    }

    /**
     * 玩家所加入的尸潮
     */
    private static final HashMap<String, String> playerSurge = new HashMap<>();

    public static HashMap<String, String> getPlayerSurge() {
        return playerSurge;
    }

    /**
     * 玩家个人击杀数
     */
    private static final HashMap<String, Integer> playerKill = new HashMap<>();

    public static HashMap<String, Integer> getPlayerKill() {
        return playerKill;
    }

    /**
     * 尸潮状态
     */
    private static final HashMap<String, Boolean> surgeState = new HashMap<>();

    public static HashMap<String, Boolean> getSurgeState() {
        return surgeState;
    }

    private static BukkitRunnableManager runnableManager;

    public static BukkitRunnableManager getRunnableManager() {
        return runnableManager;
    }

    private static final HashMap<UUID, Boolean> NPCSaver = new HashMap<>();

    public static HashMap<UUID, Boolean> getNPCSaver() {
        return NPCSaver;
    }

    /**
     * 私人尸潮
     */
    private static final HashMap<String, String> privateSurgeMap = new HashMap<>();

    public static HashMap<String, String> getPrivateSurgeMap() {
        return privateSurgeMap;
    }

    private static ConcurrentMap<String, SurgeLocation> surgeLocationMap = new ConcurrentHashMap<>();

    public static ConcurrentMap<String, SurgeLocation> getSurgeLocationMap() {
        return surgeLocationMap;
    }

    @Override
    public void onLoad() {
        saveDefaultConfig();
        prepareFolder();
    }

    @Override
    public void onEnable() {
        instance = this;
        runnableManager = new BukkitRunnableManager(this);
        PapiHook papiHook = new PapiHook();
        if (papiHook.canRegister()) {
            papiHook.register();
        }
        SocketClient socketClient = new SocketClient("s1.abrnya.com", 15562);
        socketClient.connect();
        socketClient.sendMessage("ZPokeZombieSurge//" + getmac());
        try {
            Thread.sleep(300L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        socketClient.disconnect();
    }

    public static String getmac() {
        try {
            InetAddress address = InetAddress.getLocalHost();
            NetworkInterface ni = NetworkInterface.getByInetAddress(address);
            ni.getInetAddresses().nextElement().getAddress();
            byte[] mac = ni.getHardwareAddress();
            String sIP = address.getHostAddress();
            String sMAC = "";
            Formatter formatter = new Formatter();
            for (int i = 0; i < mac.length; ++i) {
                sMAC = formatter.format(Locale.getDefault(), "%02X%s", mac[i], i < mac.length - 1 ? "-" : "").toString();
            }
            return sMAC;
        } catch (Exception e) {
            return "error";
        }
    }

    public void prepareFolder() {
        File surgeDataFolder = new File("plugins/ZPokeZombieSurge/Surge");
        File pokeDataFolder = new File("plugins/ZPokeZombieSurge/pokes");
        File trainerDataFolder = new File("plugins/ZPokeZombieSurge/trainer");
        if (!surgeDataFolder.exists()) {
            surgeDataFolder.mkdir();
        }
        if (!pokeDataFolder.exists()) {
            pokeDataFolder.mkdir();
        }
        if (!trainerDataFolder.exists()) {
            trainerDataFolder.mkdir();
        }
        for (String filename : Arrays.stream(surgeDataFolder.listFiles()).map(File::getName).collect(Collectors.toList())) {
            filename = filename.replace(".yml", "");
            surgeState.put(filename, false);
            surgeLocationMap.put(filename, new SurgeLocation(filename));
        }
    }

    @Override
    public void onDisable() {
        logDisable(this);
//        surgeKill.clear();
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
