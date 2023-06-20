package io.github.yuazer.zpokezombiesurge.Utils;

import io.github.yuazer.zpokezombiesurge.Main;
import org.bukkit.Bukkit;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class URLCrypto {
    public static String decryptURL(String encryptedURL) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedURL.getBytes(StandardCharsets.UTF_8));
            return new String(decodedBytes, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            System.out.println("§a您的机械码:§e" + Main.getmac());
            System.out.println("§b联系Z菌[QQ:1109132]获取授权");
            Bukkit.getPluginManager().disablePlugin(Main.getInstance());
        }
        return "false";
    }
}
