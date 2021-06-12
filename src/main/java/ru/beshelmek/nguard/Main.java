package ru.beshelmek.nguard;

import com.google.common.io.ByteStreams;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Iterator;
import java.util.List;

public class Main extends JavaPlugin {

    public UserLogger userLogger;
    public ConfigurationSection configurationSection;

    public void onEnable() {
        getLogger().info("[nGuard] nGuard enabled! C0d3 by Beshelmek, MoonshineBucket, DreamSmoke");

        getCommand("mscreen").setExecutor((commandSender, command, s, args) -> {
            if(commandSender.hasPermission("nGuard.screen")) {
                if(args.length == 0) {
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "&9/mscreen <player>&7: Getting screenshot from player."));
                    return false;
                }

                Player player = getServer().getPlayer(args[0]);
                if(player.isOnline()) {
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            String.format("&6[nGuard] &rGetting screenshot from %s", player.getName())));
                    player.sendPluginMessage(this, "nGuard", String.format("%s;screen",
                            commandSender.getName()).getBytes());
                    return false;
                }

                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        String.format("&6[nGuard] &cPlayer %s is offline!")));
            }

            return false;
        });

        getCommand("shistory").setExecutor((commandSender, command, s, args) -> {
            if(commandSender.hasPermission("nGuard.history")) {
                if(args.length == 0) {
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "&9/shistory <player>&7: Getting screenshot history from player."));
                    return false;
                }

                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        String.format("&6[nGuard] &rGetting history from %s", args[0])));

                List<String> list = userLogger.getLogs(args[0]);
                if(list == null || list.isEmpty()) {
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            String.format("&6[nGuard] &cScreenshots not found!")));
                    return false;
                }

                Iterator<String> iterator = list.iterator();
                while(iterator.hasNext()) {
                    String string = iterator.next();
                    if(string == null || string.isEmpty()) continue;

                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            String.format("&6[nGuard] &r%s", string)));
                }
            }

            return false;
        });

        getServer().getMessenger().registerOutgoingPluginChannel(this, "nGuard");
        getServer().getMessenger().registerIncomingPluginChannel(this, "nGuard", (channel, player, bytes) -> {
            if(channel.equals("nGuard")) {
                try {
                    String[] data = ByteStreams.newDataInput(bytes).readUTF().split(";");
                    if(data[0].equals("srvAuth")) {
                        if(player.isOnline() && player.hasPermission("nGuard.screen.view"))
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    String.format("&6[nGuard] &rYour screenshot - %s", data[1])));
                        userLogger.addLog(player.getName(), "no", data[1]);
                    } else {
                        Player messageReceiver = getServer().getPlayer(data[0]);
                        if(messageReceiver.isOnline())
                            messageReceiver.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    String.format("&6[nGuard] &rScreenshot from %s - %s", player.getName(), data[1])));
                        userLogger.addLog(player.getName(), data[0], data[1]);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        saveDefaultConfig();
        userLogger = new UserLogger(new File(getDataFolder(), (configurationSection = getConfig().getRoot())
                .getString("ServerName")));
    }

}