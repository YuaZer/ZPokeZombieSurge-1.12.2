package io.github.yuazer.zpokezombiesurge.Runnable;

import com.pixelmonmod.pixelmon.battles.BattleRegistry;
import io.github.yuazer.zpokezombiesurge.Main;
import io.github.yuazer.zpokezombiesurge.Utils.PlayerUtils;
import io.github.yuazer.zpokezombiesurge.Utils.PokeUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;

public class SurgeJoin extends BukkitRunnable {
    private String surgename;
    private YamlConfiguration conf;
    private Location minLoc;
    private Location maxLoc;
    private int offlineCount = 0;

    public SurgeJoin(String surgename) {
        this.surgename = surgename;
        loadConf();
    }

    private void loadConf() {
//        conf = YamlConfiguration.loadConfiguration(new File("plugins/ZPokeZombieSurge/Surge/" + surgename + ".yml"));
//        String[] xyz1 = conf.getString("x1y1z1").split("//");
//        String[] xyz2 = conf.getString("x2y2z2").split("//");
//        World world = Bukkit.getWorld(conf.getString("world"));
//        minLoc = new Location(world, Double.parseDouble(xyz1[0]), Double.parseDouble(xyz1[1]), Double.parseDouble(xyz1[2]));
//        maxLoc = new Location(world, Double.parseDouble(xyz2[0]), Double.parseDouble(xyz2[1]), Double.parseDouble(xyz2[2]));
        this.conf = Main.getSurgeLocationMap().get(surgename).getConf();
        this.minLoc = Main.getSurgeLocationMap().get(surgename).getMinLoc();
        this.maxLoc = Main.getSurgeLocationMap().get(surgename).getMaxLoc();
    }


    @Override
    public void run() {
        boolean hasPlayer = false;
        if (Main.getPlayerSurge().keySet().isEmpty()) {
            checkOffline(hasPlayer);
            return;
        }
        for (String pname : Main.getPlayerSurge().keySet()) {
            Player player = Bukkit.getPlayer(pname);
            if (player == null) {
                continue;
            }
            if (Main.getPrivateSurgeMap().containsKey(surgename) && !Main.getPrivateSurgeMap().get(surgename).equalsIgnoreCase(player.getName())) {
                continue;
            }
            if (Main.getPlayerSurge().get(player.getName()) == null || (!Main.getPlayerSurge().get(player.getName()).equalsIgnoreCase(surgename))) {
                continue;
            }
            if (BattleRegistry.getBattle(PlayerUtils.getEntityPlayerMP(player)) != null) {
                continue;
            }
            if (PlayerUtils.isPlayerInRange(player, minLoc, maxLoc) && PlayerUtils.checkChance(conf.getInt("chance"))) {
                if (Main.getPlayerKill().getOrDefault(player.getName(), 0) + 1 < conf.getInt("amount")) {
                    String s = PokeUtils.getRandomString(conf.getStringList("Pokemon"));
                    String type = PokeUtils.getTextBetweenBrackets(s);
                    String poke = s.replace("[local]", "").replace("[name]", "").replace("[npc]", "");
                    if (!type.equalsIgnoreCase("npc")) {
                        PokeUtils.battlePokemon(player, PokeUtils.getPokemon(type, s));
                    } else {
                        try {
                            PokeUtils.battleTrainer(player, PokeUtils.getNPCTrainerInFile_NBT(new File("plugins/ZPokeZombieSurge/trainer/" + poke + ".zns")));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } else {
                    String s = conf.getString("Boss");
                    String type = PokeUtils.getTextBetweenBrackets(s);
                    String poke = s.replace("[local]", "").replace("[name]", "").replace("[npc]", "");
                    if (!type.equalsIgnoreCase("npc")) {
                        PokeUtils.battlePokemon(player, PokeUtils.getPokemon(type, s));
                    } else {
                        try {
                            PokeUtils.battleTrainer(player, PokeUtils.getNPCTrainerInFile_NBT(new File("plugins/ZPokeZombieSurge/trainer/" + poke + ".zns")));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                offlineCount = 0;
                hasPlayer = true;
            }
        }
        checkOffline(hasPlayer);
    }

    private void checkOffline(boolean hasPlayer) {
        if (!hasPlayer) {
            offlineCount++;
        }
        if (conf.getInt("offlineCount") <= offlineCount) {
            PokeUtils.endSurge(surgename);
            for (String cmd : conf.getStringList("offlineCountCommands")) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%surge%", surgename));
            }
        }
    }
}
