package io.github.yuazer.zpokezombiesurge.Utils;

import net.minecraft.world.World;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;

public class NMSUtils {
    public static World bkToNmsWorld(org.bukkit.World world) {
        return ((CraftWorld) world).getHandle();
    }
}
