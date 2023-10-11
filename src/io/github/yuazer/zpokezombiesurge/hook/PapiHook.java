package io.github.yuazer.zpokezombiesurge.hook;

import io.github.yuazer.zpokezombiesurge.Main;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class PapiHook extends PlaceholderExpansion {
    @Override
    public String getIdentifier() {
        return "zpokezombiesurge";
    }

    @Override
    public String getAuthor() {
        return "Z菌";
    }

    @Override
    public String getVersion() {
        return Main.getInstance().getDescription().getVersion();
    }
    @Override
    public String onPlaceholderRequest(Player p, String indentifier){
        if (p==null){
            return "";
        }
        String key = indentifier.split("_")[0];
        String surge = indentifier.split("_")[1];
        if (key.equalsIgnoreCase("isopen")){
            return Main.getSurgeState().get(surge).toString();
        }
        return "error";
    }
}
