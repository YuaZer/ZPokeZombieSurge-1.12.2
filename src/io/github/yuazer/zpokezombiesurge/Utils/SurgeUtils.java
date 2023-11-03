package io.github.yuazer.zpokezombiesurge.Utils;

import io.github.yuazer.zpokezombiesurge.Main;
import io.github.yuazer.zpokezombiesurge.object.SurgeLocation;
import org.bukkit.entity.Player;

public class SurgeUtils {
    public static boolean inInSurge(Player player, String surgeName) {
        SurgeLocation surgeLocation = Main.getSurgeLocationMap().get(surgeName);
        return PlayerUtils.isPlayerInRange(player, surgeLocation.getMinLoc(), surgeLocation.getMaxLoc());
    }
}
