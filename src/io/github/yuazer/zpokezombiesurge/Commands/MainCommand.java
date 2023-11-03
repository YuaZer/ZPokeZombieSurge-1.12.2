package io.github.yuazer.zpokezombiesurge.Commands;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.battles.controller.participants.PixelmonWrapper;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import io.github.yuazer.zpokezombiesurge.Listener.PokeEvent;
import io.github.yuazer.zpokezombiesurge.Main;
import io.github.yuazer.zpokezombiesurge.Runnable.SurgeJoin;
import io.github.yuazer.zpokezombiesurge.Utils.PokeUtils;
import io.github.yuazer.zpokezombiesurge.Utils.YamlUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.stream.Collectors;

public class MainCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String o, String[] args) {
        if (command.getName().equalsIgnoreCase("zpokezombiesurge")) {
            if (args.length == 0) {
                sender.sendMessage("§b/zpokezombiesurge §a-> §b/zpzs");
                sender.sendMessage("§a/zpokezombiesurge join 尸潮名 §b加入指定尸潮");
                sender.sendMessage("§a/zpokezombiesurge quit 尸潮名 §b退出指定尸潮");
                if (sender.isOp()) {
                    sender.sendMessage("§a/zpokezombiesurge reload §b重载config.yml");
                    sender.sendMessage("§a/zpokezombiesurge start 尸潮名 §b开启指定尸潮");
                    sender.sendMessage("§a/zpokezombiesurge end 尸潮名 §b强制结束指定尸潮");
                    sender.sendMessage("§a/zpokezombiesurge save 背包槽位 文件名 §b将背包指定槽位的精灵存入pokes文件夹");
                    sender.sendMessage("§a/zpokezombiesurge npcsaver §b切换训练师保存模式,该模式下右键训练师将会存入trainer文件夹");
                    sender.sendMessage("§a/zpokezombiesurge startprivate 尸潮名 §b开启指定私人尸潮");
                    sender.sendMessage("§a/zpokezombiesurge checkmove 精灵槽位 §b查看精灵技能信息");
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("reload") && sender.isOp()) {
                Main.getInstance().reloadConfig();
                Main.getInstance().prepareFolder();
                sender.sendMessage(YamlUtils.getConfigMessage("Message.reload"));
                return true;
            }
            if (args[0].equalsIgnoreCase("checkmove") && sender.isOp() && (sender instanceof Player)) {
                Player player = (Player) sender;
                int slot = Integer.parseInt(args[1]) - 1;
                PlayerPartyStorage pps = Pixelmon.storageManager.getParty(player.getUniqueId());
                if (pps.get(slot) == null) {
                    player.sendMessage("§a这个槽位没有精灵哦");
                    return true;
                }
                Pokemon pokemon = pps.get(slot);
                player.sendMessage(pokemon.getMoveset().toString());
                return true;
            }
            if ("npcsaver".equalsIgnoreCase(args[0]) && sender.isOp()) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    Main.getNPCSaver().put(player.getUniqueId(), !Main.getNPCSaver().getOrDefault(player.getUniqueId(), false));
                    player.sendMessage("§a您的NPC保存模式状态:§b" + Main.getNPCSaver().get(player.getUniqueId()));
                } else {
                    sender.sendMessage("§c请以玩家身份使用该指令");
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("start") && sender.isOp() && args.length == 2) {
                String surgeName = args[1];
                if (Main.getSurgeState().containsKey(surgeName)) {
                    if (!Main.getSurgeState().get(surgeName)) {
                        YamlConfiguration surgeConf = YamlConfiguration.loadConfiguration(new File("plugins/ZPokeZombieSurge/Surge/" + surgeName + ".yml"));
                        Main.getRunnableManager().addRunnable(surgeName, new SurgeJoin(surgeName));
                        Main.getRunnableManager().startRunnable(surgeName, 0L, surgeConf.getInt("checkTime") * 20L);
                        Main.getSurgeState().put(surgeName, Boolean.TRUE);
                        YamlUtils.getConfigStringList("Handler.StartCommands").forEach(c -> {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), c.replace("%surge%", surgeName));
                        });
                        sender.sendMessage(YamlUtils.getConfigMessage("Message.successStart").replace("%surge%", surgeName));
                    } else {
                        sender.sendMessage(YamlUtils.getConfigMessage("Message.alreadyStart").replace("%surge%", surgeName));
                    }
                } else {
                    sender.sendMessage(YamlUtils.getConfigMessage("Message.noSurge").replace("%surge%", surgeName));
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("startprivate") && sender.isOp() && (sender instanceof Player)) {
                String surgeName = args[1];
                if (Main.getSurgeState().containsKey(surgeName)) {
                    if (!Main.getSurgeState().get(surgeName)) {
                        YamlConfiguration surgeConf = YamlConfiguration.loadConfiguration(new File("plugins/ZPokeZombieSurge/Surge/" + surgeName + ".yml"));
                        Main.getRunnableManager().addRunnable(surgeName, new SurgeJoin(surgeName));
                        Main.getRunnableManager().startRunnable(surgeName, 0L, surgeConf.getInt("checkTime") * 20L);
                        Main.getSurgeState().put(surgeName, Boolean.TRUE);
                        Main.getPrivateSurgeMap().put(surgeName, sender.getName());
                        sender.sendMessage(YamlUtils.getConfigMessage("Message.successStart").replace("%surge%", surgeName));
                    } else {
                        sender.sendMessage(YamlUtils.getConfigMessage("Message.alreadyStart").replace("%surge%", surgeName));
                    }
                } else {
                    sender.sendMessage(YamlUtils.getConfigMessage("Message.noSurge").replace("%surge%", surgeName));
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("end") && sender.isOp() && args.length == 2) {
                String surgeName = args[1];
                if (Main.getSurgeState().containsKey(surgeName)) {
                    if (Main.getSurgeState().get(surgeName)) {
                        PokeUtils.endSurge(surgeName);
                        sender.sendMessage(YamlUtils.getConfigMessage("Message.successEnd").replace("%surge%", surgeName));
                    } else {
                        sender.sendMessage(YamlUtils.getConfigMessage("Message.alreadyEnd").replace("%surge%", surgeName));
                    }
                } else {
                    sender.sendMessage(YamlUtils.getConfigMessage("Message.noSurge").replace("%surge%", surgeName));
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("join") && (sender instanceof Player)) {
                Player player = (Player) sender;
                if (!checkPerm(player)) {
                    player.sendMessage(YamlUtils.getConfigMessage("Message.blackList"));
                    return true;
                }
                if (Main.getSurgeState().containsKey(args[1])) {
                    if (Main.getSurgeState().getOrDefault(args[1], Boolean.FALSE)) {
                        Main.getPlayerSurge().put(player.getName(), args[1]);
                        Main.getPlayerKill().put(player.getName(), 0);
                        player.sendMessage(YamlUtils.getConfigMessage("Message.successJoin").replace("%surge%", args[1]));
                    } else {
                        player.sendMessage(YamlUtils.getConfigMessage("Message.surgeNoStart").replace("%surge%", args[1]));
                    }
                } else {
                    player.sendMessage(YamlUtils.getConfigMessage("Message.noSurge").replace("%surge%", args[1]));
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("quit") && (sender instanceof Player)) {
                Player player = (Player) sender;
                if (Main.getPlayerSurge().containsKey(player.getName())) {
                    player.sendMessage(YamlUtils.getConfigMessage("Message.successQuit").replace("%surge%", Main.getPlayerSurge().get(player.getName())));
                    Main.getPlayerSurge().remove(player.getName());
                    Main.getPlayerKill().remove(player.getName());
                } else {
                    player.sendMessage(YamlUtils.getConfigMessage("Message.noJoin"));
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("save") && sender.isOp() && args.length == 3) {
                Player player = (Player) sender;
                int slot = Integer.parseInt(args[1]) - 1;
                String fileName = args[2];
                File file = new File("plugins/ZPokeZombieSurge/pokes/" + fileName + ".zps");
                if (file.exists()) {
                    sender.sendMessage(YamlUtils.getConfigMessage("Message.zpsExist"));
                } else {
                    try {
                        if (!file.getParentFile().exists()) {
                            Files.createDirectory(file.getParentFile().toPath());
                        }
                        file.createNewFile();
                        Pokemon pokemon = Pixelmon.storageManager.getParty(player.getUniqueId()).get(slot);
                        if (pokemon != null && !pokemon.isEgg()) {
                            PokeUtils.setPokemonInFile_NBT(pokemon, file);
                            sender.sendMessage(YamlUtils.getConfigMessage("Message.successSave"));
                        } else {
                            sender.sendMessage("§c该精灵是蛋或不存在!");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean checkPerm(Player player) {
        PlayerPartyStorage pps = Pixelmon.storageManager.getParty(player.getUniqueId());
        for (Pokemon pokemon : pps.getTeam()) {
            if (!Main.getInstance().getConfig().getConfigurationSection("banAttack").getKeys(false).contains(pokemon.getSpecies().getPokemonName())) {
                continue;
            }
            for (String atk : YamlUtils.getConfigStringList("banAttack." + pokemon.getSpecies().getPokemonName())) {
                if (pokemon.getMoveset().hasAttack(atk)) {
                    return false;
                }
            }
        }
        return true;
    }
}
