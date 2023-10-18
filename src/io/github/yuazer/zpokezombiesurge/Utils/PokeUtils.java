package io.github.yuazer.zpokezombiesurge.Utils;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.battles.BattleRegistry;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.TrainerParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.WildPixelmonParticipant;
import com.pixelmonmod.pixelmon.battles.rules.BattleRules;
import com.pixelmonmod.pixelmon.entities.npcs.NPCTrainer;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import io.github.yuazer.zpokezombiesurge.Main;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PokeUtils {
    //将宝可梦存为NBT文件
    public static void setPokemonInFile_NBT(Pokemon pokemon, File file) throws IOException {
        NBTTagCompound nbt = new NBTTagCompound();
        pokemon.setUUID(UUID.randomUUID());
        pokemon.writeToNBT(nbt);
        CompressedStreamTools.func_74795_b(nbt, file);
    }

    //从文件中的NBT获取宝可梦
    public static Pokemon getPokemonInFile_NBT(File file) throws IOException {
        Pokemon pokemon = Pixelmon.pokemonFactory.create(CompressedStreamTools.func_74797_a(file));
        pokemon.setUUID(UUID.randomUUID());
        return pokemon;
    }

    public static void setNPCTrainerInFile_NBT(NPCTrainer trainer, File file) throws IOException {
        NBTTagCompound nbt = new NBTTagCompound();
        trainer.func_70014_b(nbt);
        CompressedStreamTools.func_74795_b(nbt, file);
    }

    public static NPCTrainer getNPCTrainerInFile_NBT(File file) throws IOException {
        NPCTrainer npcTrainer = new NPCTrainer(NMSUtils.bkToNmsWorld(Bukkit.getWorld("world")));
        NBTTagCompound nbt = CompressedStreamTools.func_74797_a(file);
        npcTrainer.func_70037_a(nbt);
        return npcTrainer;
    }

    //发起玩家和指定精灵的单打对战(Wild)
    public static void battlePokemon_Wild(Player player, File file) {
        try {
            Pokemon pokemon = getPokemonInFile_NBT(file);
            BattleParticipant[] bp = {
                    new PlayerParticipant(PlayerUtils.getEntityPlayerMP(player),
                            Pixelmon.storageManager.getParty(player.getUniqueId()).getAndSendOutFirstAblePokemon(PlayerUtils.getEntityPlayerMP(player)))};
            BattleParticipant[] tp = {new WildPixelmonParticipant(pokemon.getOrSpawnPixelmon(NMSUtils.bkToNmsWorld(player.getWorld()), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()))};
            BattleRegistry.startBattle(tp, bp, new BattleRules());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //发起玩家和指定List<Pokemon>的单打对战(NPCTrainer)
    public static void battlePokemon(Player player, List<Pokemon> pokemons) {
        if (BattleRegistry.getBattle(PlayerUtils.getEntityPlayerMP(player)) != null && !BattleRegistry.getBattle(PlayerUtils.getEntityPlayerMP(player)).battleEnded) {
            return;
        }
        NPCTrainer npcTrainer = new NPCTrainer(NMSUtils.bkToNmsWorld(player.getWorld()));
//        for (int i = 0; i <= pokemons.size() - 1; i++) {
//            npcTrainer.getPokemonStorage().set(i, pokemons.get(i));
//        }
        for (Pokemon pokemon : pokemons) {
            npcTrainer.getPokemonStorage().add(pokemon);
        }
        BattleParticipant[] bp =
                {new PlayerParticipant(PlayerUtils.getEntityPlayerMP(player),
                        Pixelmon.storageManager.getParty(player.getUniqueId()).getAndSendOutFirstAblePokemon(PlayerUtils.getEntityPlayerMP(player)))};
        BattleParticipant[] tp = {new TrainerParticipant(npcTrainer, 1)};
        BattleRegistry.startBattle(tp, bp, new BattleRules());
    }

    //发起玩家和指定Pokemon的单打对战(NPCTrainer)
    public static void battlePokemon(Player player, Pokemon pokemon) {
        if (BattleRegistry.getBattle(PlayerUtils.getEntityPlayerMP(player)) != null && !BattleRegistry.getBattle(PlayerUtils.getEntityPlayerMP(player)).battleEnded) {
            return;
        }
        NPCTrainer npcTrainer = new NPCTrainer(NMSUtils.bkToNmsWorld(player.getWorld()));
        npcTrainer.getPokemonStorage().add(pokemon);
        BattleParticipant[] bp =
                {new PlayerParticipant(PlayerUtils.getEntityPlayerMP(player),
                        Pixelmon.storageManager.getParty(player.getUniqueId()).getAndSendOutFirstAblePokemon(PlayerUtils.getEntityPlayerMP(player)))};
        BattleParticipant[] tp = {new TrainerParticipant(npcTrainer, 1)};
        BattleRegistry.startBattle(tp, bp, new BattleRules());
    }

    public static void battleTrainer(Player player, NPCTrainer trainer) {
        if (BattleRegistry.getBattle(PlayerUtils.getEntityPlayerMP(player)) != null && !BattleRegistry.getBattle(PlayerUtils.getEntityPlayerMP(player)).battleEnded) {
            return;
        }
        BattleParticipant[] bp =
                {new PlayerParticipant(PlayerUtils.getEntityPlayerMP(player),
                        Pixelmon.storageManager.getParty(player.getUniqueId()).getAndSendOutFirstAblePokemon(PlayerUtils.getEntityPlayerMP(player)))};
        BattleParticipant[] tp = {new TrainerParticipant(trainer, 1)};
        BattleRegistry.startBattle(tp, bp, new BattleRules());
    }

    public static Pokemon getPokemon(String type, String s) {
        String poke = s.replace("[local]", "").replace("[name]", "").replace("[npc]", "");
        try {
            if (type.equalsIgnoreCase("local")) {
                return getPokemonInFile_NBT(new File("plugins/ZPokeZombieSurge/pokes/" + poke + ".zps"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Pixelmon.pokemonFactory.create(EnumSpecies.getFromName(poke).get());
    }

    //假设str是一个字符串，包含方括号
    public static String getTextBetweenBrackets(String str) {
        //创建一个正则表达式，匹配方括号中的内容
        Pattern pattern = Pattern.compile("\\[(.*?)\\]");
        //创建一个Matcher对象，用来匹配字符串
        Matcher matcher = pattern.matcher(str);
        //判断是否有匹配的结果
        if (matcher.find()) {
            //如果有，返回第一个匹配的子串，去掉方括号
            return matcher.group(1);
        } else {
            //如果没有，返回空字符串
            return "";
        }
    }

    public static String getRandomString(List<String> list) {
        //创建一个Random对象
        Random random = new Random();
        //生成一个随机的索引，范围是0到list的大小
        int index = random.nextInt(list.size());
        //获取对应的元素
        return list.get(index);
    }

    public static YamlConfiguration getSurgeConf(String surgeName) {
        return YamlConfiguration.loadConfiguration(new File("plugins/ZPokeZombieSurge/Surge/" + surgeName + ".yml"));
    }

    public static void endSurge(String surgeName) {
        YamlConfiguration conf = getSurgeConf(surgeName);
        if (!Main.getPlayerSurge().keySet().isEmpty()) {
            Iterator<String> iterator = Main.getPlayerSurge().keySet().iterator();
            while (iterator.hasNext()){
                if (Main.getPlayerSurge().get(iterator.next()) != null && Main.getPlayerSurge().get(iterator.next()).equalsIgnoreCase(surgeName)) {
                    Main.getPlayerKill().put(iterator.next(), 0);
                    Main.getPlayerSurge().remove(iterator.next());
                }
            }
        }
        Main.getPrivateSurgeMap().remove(surgeName);
        Main.getRunnableManager().stopRunnable(surgeName);
        Main.getRunnableManager().removeRunnable(surgeName);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), conf.getString("endbroadcast").replace("%surge%", surgeName));
        Main.getSurgeState().put(surgeName, Boolean.FALSE);
        Main.getSurgeKill().remove(surgeName);
    }
}
