package io.github.yuazer.zpokezombiesurge.Utils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class PlayerUtils {
    public static int getItemOriginal(Player player, String originalName, boolean... all) {
        int count = 0;
        int max = all.length > 0 && all[0] ? player.getInventory().getSize() : 36;
        for (int i = 0; i < max; ++i) {
            ItemStack itemStack = player.getInventory().getItem(i);
            if (itemStack != null && itemStack.getType() != Material.AIR && itemStack.getType().name().equalsIgnoreCase(originalName)) {
                count += itemStack.getAmount();
            }
        }
        return count;
    }
    public static boolean checkChance(int n) {
        //创建一个Random对象
        Random random = new Random();
        //生成一个随机数，范围是0到100
        int randomNumber = random.nextInt(101);
        //判断随机数是否大于参数
        return randomNumber > n;
    }
    public static boolean isPlayerInRange(Player player, Location minLocation, Location maxLocation) {
        Location entityLocation = player.getLocation();

        double entityX = entityLocation.getX();
        double entityY = entityLocation.getY();
        double entityZ = entityLocation.getZ();

        double minX = Math.min(minLocation.getX(), maxLocation.getX());
        double minY = Math.min(minLocation.getY(), maxLocation.getY());
        double minZ = Math.min(minLocation.getZ(), maxLocation.getZ());

        double maxX = Math.max(minLocation.getX(), maxLocation.getX());
        double maxY = Math.max(minLocation.getY(), maxLocation.getY());
        double maxZ = Math.max(minLocation.getZ(), maxLocation.getZ());

        return entityLocation.getWorld().getName().equalsIgnoreCase(maxLocation.getWorld().getName()) &&
                entityLocation.getWorld().getName().equalsIgnoreCase(minLocation.getWorld().getName()) &&
                entityX >= minX && entityX <= maxX &&
                entityY >= minY && entityY <= maxY &&
                entityZ >= minZ && entityZ <= maxZ;
    }
    /**
     * 获取玩家EMP对象
     *
     * @param name 名称
     * @return EMP对象
     */
    public static EntityPlayerMP getEntityPlayerMP(String name) {
        return getEntityPlayerMP(Bukkit.getPlayer(name));
    }


    /**
     * 获取玩家EMP对象
     *
     * @param player 玩家对象
     * @return EMP对象
     */
    public static EntityPlayerMP getEntityPlayerMP(Player player) {
        return ((CraftPlayer) player).getHandle();
    }

    /**
     * 获取玩家Player对象
     *
     * @param playerMP 玩家EMP对象
     * @return Player对象
     */
    public static Player getPlayer(EntityPlayerMP playerMP) {
        return Bukkit.getPlayer(playerMP.getPersistentID());
    }

    /**
     * 获取玩家Player对象
     *
     * @param player EntityPlayer
     * @return Player对象
     */
    public static Player getPlayer(EntityPlayer player) {
        return player == null ? null : Bukkit.getPlayer(player.getPersistentID());
    }
}
