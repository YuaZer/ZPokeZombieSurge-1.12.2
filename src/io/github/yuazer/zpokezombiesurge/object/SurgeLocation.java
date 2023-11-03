package io.github.yuazer.zpokezombiesurge.object;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class SurgeLocation {
    private String surgename;
    private YamlConfiguration conf;
    private Location minLoc;
    private Location maxLoc;

    public SurgeLocation(String surgeName) {
        this.surgename = surgeName;
        loadConf();
    }

    private void loadConf() {
        this.conf = YamlConfiguration.loadConfiguration(new File("plugins/ZPokeZombieSurge/Surge/" + surgename + ".yml"));
        String[] xyz1 = conf.getString("x1y1z1").split("//");
        String[] xyz2 = conf.getString("x2y2z2").split("//");
        World world = Bukkit.getWorld(conf.getString("world"));
        this.minLoc = new Location(world, Double.parseDouble(xyz1[0]), Double.parseDouble(xyz1[1]), Double.parseDouble(xyz1[2]));
        this.maxLoc = new Location(world, Double.parseDouble(xyz2[0]), Double.parseDouble(xyz2[1]), Double.parseDouble(xyz2[2]));
    }

    public Location getMinLoc() {
        return minLoc;
    }

    public Location getMaxLoc() {
        return maxLoc;
    }

    public YamlConfiguration getConf() {
        return conf;
    }
}
