package io.github.yuazer.zpokezombiesurge.Listener;

import catserver.api.bukkit.event.ForgeEvent;
import com.pixelmonmod.pixelmon.api.events.BeatTrainerEvent;
import io.github.yuazer.zpokezombiesurge.Main;
import io.github.yuazer.zpokezombiesurge.Utils.PlayerUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

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
            }
        }
    }
}
