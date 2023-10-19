package io.github.yuazer.zpokezombiesurge.Listener;

import catserver.api.bukkit.event.ForgeEvent;
import com.pixelmonmod.pixelmon.api.events.BeatTrainerEvent;
import com.pixelmonmod.pixelmon.api.events.LostToTrainerEvent;
import com.pixelmonmod.pixelmon.api.events.battles.BattleEndEvent;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.PixelmonWrapper;
import com.pixelmonmod.pixelmon.entities.npcs.NPCTrainer;
import com.pixelmonmod.pixelmon.enums.battle.BattleResults;
import io.github.yuazer.zpokezombiesurge.Main;
import io.github.yuazer.zpokezombiesurge.Utils.PlayerUtils;
import io.github.yuazer.zpokezombiesurge.Utils.PokeUtils;
import io.github.yuazer.zpokezombiesurge.Utils.YamlUtils;
import jdk.nashorn.internal.ir.annotations.Immutable;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.io.File;
import java.io.IOException;

public class PokeEvent implements Listener {
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Main.getPlayerSurge().remove(e.getPlayer().getName());
    }

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
                //int before = Main.getPlayerKill().getOrDefault(player.getName(), 0);
                int playerbefore = Main.getPlayerKill().getOrDefault(player.getName(), 0);
                //Main.getSurgeKill().put(surgeName, ++before);
                Main.getPlayerKill().put(player.getName(), ++playerbefore);
                rewardPlayer(player, surgeName);
            }
        }
//        } else if (event.getForgeEvent() instanceof BattleEndEvent) {
//            BattleEndEvent e = (BattleEndEvent) event.getForgeEvent();
//            com.google.common.collect.ImmutableMap<BattleParticipant, BattleResults> i = e.results;
//            for (BattleParticipant bp:i.keySet()){
//                for (PixelmonWrapper pokemon:bp.getOpponentPokemon()){
//                    Player player = Bukkit.getPlayer(pokemon.getOwnerName());
//                    if (player==null){
//                        continue;
//                    }
//                    System.out.println("step1");
//                    System.out.println(pokemon.getPokemonName());
//                    if (i.get(bp).equals(BattleResults.DEFEAT)&&Main.getPlayerSurge().containsKey(player.getName())&&!
//                            PokeUtils.getSurgeConf(Main.getPlayerSurge().get(player.getName())).getBoolean("allowRun")){
//                        e.setCanceled(true);
//                        return;
//                    }else {
//                        System.out.println(i.get(bp));
//                        System.out.println(Main.getPlayerSurge().containsKey(player.getName()));
//                        System.out.println( PokeUtils.getSurgeConf(Main.getPlayerSurge().get(player.getName())).getBoolean("allowRun"));
//                    }
//                }
//            }
//        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) throws IOException {
        try {
            Player player = event.getPlayer();
            if (event.getHand().equals(EquipmentSlot.HAND) && Main.getNPCSaver().get(player.getUniqueId())) {
                Entity entity = event.getRightClicked();
                net.minecraft.entity.Entity nmsEntity = bkToNmsEntity(entity);
                if (nmsEntity instanceof NPCTrainer) {
                    File file = new File("plugins/ZPokeZombieSurge/trainer/" + nmsEntity.func_110124_au() + ".zns");
                    PokeUtils.setNPCTrainerInFile_NBT((NPCTrainer) bkToNmsEntity(entity), file);
                    player.sendMessage("§aNPC保存成功!文件名为:" + nmsEntity.func_110124_au() + ".zns");
                }
            }
        } catch (NullPointerException ignored) {
        }
    }

    public static net.minecraft.entity.Entity bkToNmsEntity(Entity entity) {
        net.minecraft.entity.Entity nmsEntity = ((CraftEntity) entity).getHandle();
        return nmsEntity;
    }

    public void rewardPlayer(Player player, String surgeName) {
        if (!Main.getPlayerSurge().get(player.getName()).equalsIgnoreCase(surgeName)) {
            return;
        }
        YamlConfiguration conf = PokeUtils.getSurgeConf(surgeName);
        for (String num : conf.getConfigurationSection("Reward.normal").getKeys(false)) {
            int amount = Integer.parseInt(num);
            if (Main.getPlayerKill().get(player.getName()) == amount) {
                for (String cmd : conf.getStringList("Reward.normal." + amount)) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", player.getName()));
                }
                return;
            }
        }
        if (Main.getPlayerKill().get(player.getName()) == conf.getInt("amount")) {
            for (String cmd : conf.getStringList("Reward.boss")) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", player.getName()));
            }
            //各打各玩法
            player.sendMessage(YamlUtils.getConfigMessage("Message.successQuit").replace("%surge%", Main.getPlayerSurge().get(player.getName())));
            Main.getPlayerSurge().remove(player.getName());
            Main.getPlayerKill().remove(player.getName());
            //一人击败boss全体结束玩法
            //PokeUtils.endSurge(surgeName);
        }
    }
}

