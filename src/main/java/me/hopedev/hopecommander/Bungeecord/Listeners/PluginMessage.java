package me.hopedev.hopecommander.Bungeecord.Listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import me.hopedev.hopecommander.Bungeecord.BungeeMain;
import me.hopedev.hopecommander.utils.UniversalUsage;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class PluginMessage implements Listener {
    /*
    Made by Aurel (Hope) on 27/10/2020
     */
    @EventHandler
    public void onMessageReceive(PluginMessageEvent event) {
        if (event.getTag().equalsIgnoreCase("hope:hopecmdsend")) {
            BungeeMain bungeeMain = (BungeeMain) UniversalUsage.get().getPluginInstance();
            ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
            String commandToRun = in.readUTF();
            List<String> iplist = new ArrayList<>();
            InetSocketAddress address;
            if(event.getSender().getSocketAddress() instanceof InetSocketAddress) {
                address = (InetSocketAddress) event.getSender().getSocketAddress();
            } else {
                address = event.getSender().getAddress();
            }
            if(address != null) {
                String ip = address.getHostName();
                if(ip != null) {
                    bungeeMain.getProxy().getConsole().sendMessage(new ComponentBuilder("[HopeCommander] Command received from " + ip + ":" + address.getPort() + "» " + commandToRun).create());
                    try {
                        Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(bungeeMain.getDataFolder(), "config.yml"));
                        iplist = configuration.getStringList("whitelisted-ips");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (!iplist.contains(ip)) {
                        System.out.println("[HopeCommander][WARNING] Request IP " + ip + " IS NOT ON whitelisted-ips. Not Executing command!");
                        return;
                    }
                    bungeeMain.getProxy().getConsole().sendMessage(new ComponentBuilder("[HopeCommander] executing...").create());
                    //send
                    bungeeMain.getProxy().getPluginManager().dispatchCommand(bungeeMain.getProxy().getConsole(), commandToRun);
                } else {
                    System.out.println("[HopeCommander][WARNING] Cannot parse request IP! Not Executing command!");
                }
            } else {
                System.out.println("[HopeCommander][WARNING] Cannot parse request IP! Not Executing command!");
            }
        }
    }
}
