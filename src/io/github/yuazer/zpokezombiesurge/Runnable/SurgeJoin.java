package io.github.yuazer.zpokezombiesurge.Runnable;

import io.github.yuazer.zpokezombiesurge.Main;
import io.github.yuazer.zpokezombiesurge.Utils.PlayerUtils;
import io.github.yuazer.zpokezombiesurge.Utils.PokeUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
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

    public SurgeJoin(String surgename) {
        this.surgename = surgename;
        loadConf();
    }

    private void loadConf() {
        conf = YamlConfiguration.loadConfiguration(new File("plugins/ZPokeZombieSurge/Surge/" + surgename + ".yml"));
        String[] xyz1 = conf.getString("x1y1z1").split("//");
        String[] xyz2 = conf.getString("x2y2z2").split("//");
        World world = Bukkit.getWorld(conf.getString("world"));
        minLoc = new Location(world, Double.parseDouble(xyz1[0]), Double.parseDouble(xyz1[1]), Double.parseDouble(xyz1[2]));
        maxLoc = new Location(world, Double.parseDouble(xyz2[0]), Double.parseDouble(xyz2[1]), Double.parseDouble(xyz2[2]));
    }


    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (Main.getPlayerSurge().get(player.getName()) == null || (!Main.getPlayerSurge().get(player.getName()).equalsIgnoreCase(surgename))) {
                continue;
            }
            if (PlayerUtils.isPlayerInRange(player, minLoc, maxLoc) && PlayerUtils.checkChance(conf.getInt("chance"))) {
                if (Main.getSurgeKill().getOrDefault(surgename, 0)+1 < conf.getInt("amount")) {
                    String s = PokeUtils.getRandomString(conf.getStringList("Pokemon"));
                    String type = PokeUtils.getTextBetweenBrackets(s);
                    String poke = s.replace("[local]", "").replace("[name]", "").replace("[npc]", "");
                    if (!type.equalsIgnoreCase("npc")) {
                        PokeUtils.battlePokemon(player, PokeUtils.getPokemon(type, s));
                    }
                    try {
                        PokeUtils.battleTrainer(player, PokeUtils.getNPCTrainerInFile_NBT(new File("plugins/ZPokeZombieSurge/trainer/" + poke + ".zns")));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    String s = conf.getString("Boss");
                    String type = PokeUtils.getTextBetweenBrackets(s);
                    String poke = s.replace("[local]", "").replace("[name]", "").replace("[npc]", "");
                    if (!type.equalsIgnoreCase("npc")) {
                        PokeUtils.battlePokemon(player, PokeUtils.getPokemon(type, s));
                    }
                    try {
                        PokeUtils.battleTrainer(player, PokeUtils.getNPCTrainerInFile_NBT(new File("plugins/ZPokeZombieSurge/trainer/" + poke + ".zns")));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
}
