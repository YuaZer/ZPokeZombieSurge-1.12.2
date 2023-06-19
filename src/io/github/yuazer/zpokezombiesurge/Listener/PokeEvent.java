package io.github.yuazer.zpokezombiesurge.Listener;

import catserver.api.bukkit.event.ForgeEvent;
import com.pixelmonmod.pixelmon.api.events.BeatTrainerEvent;
import io.github.yuazer.zpokezombiesurge.Main;
import io.github.yuazer.zpokezombiesurge.Utils.PlayerUtils;
import io.github.yuazer.zpokezombiesurge.Utils.PokeUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.File;

public class PokeEvent implements Listener {

    @EventHandler
    public void onForge(ForgeEvent event) {
        if (event.getForgeEvent() instanceof BeatTrainerEvent) {
            BeatTrainerEvent e = (BeatTrainerEvent) event.getForgeEvent();
            Player player = PlayerUtils.getPlayer(e.player);
            if (Main.getPlayerSurge().get(player.getName()) != null) {
                //TODO
                // 胜利后，尸潮全局击杀数+1,玩家个人击杀数+1,进行奖励
                // 检测是否是最后一只精灵，如果是，结束尸潮，并对玩家进行BOSS奖励
                String surgeName = Main.getPlayerSurge().get(player.getName());
                int before = Main.getSurgeKill().getOrDefault(surgeName, 0);
                int playerbefore = Main.getPlayerKill().getOrDefault(player.getName(), 0);
                Main.getSurgeKill().put(surgeName, ++before);
                Main.getPlayerKill().put(player.getName(), ++playerbefore);
                rewardPlayer(player, surgeName);
            }
        }
    }


    public void rewardPlayer(Player player, String surgeName) {
        if (Main.getPlayerSurge().get(player.getName()).equalsIgnoreCase(surgeName)) {
            YamlConfiguration conf = PokeUtils.getSurgeConf(surgeName);
            for (String num : conf.getConfigurationSection("Reward.normal").getKeys(false)) {
                int amount = Integer.parseInt(num);
                if (Main.getPlayerKill().get(player.getName()) == amount) {;
                    for (String cmd : conf.getStringList("Reward.normal." + amount)) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", player.getName()));
                    }
                    return;
                }
            }
            if (Main.getSurgeKill().get(surgeName) == conf.getInt("amount")) {
                for (String cmd : conf.getStringList("Reward.boss")) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", player.getName()));
                }
                PokeUtils.endSurge(surgeName);
            }
        }
    }

}
