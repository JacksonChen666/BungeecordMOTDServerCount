package com.jacksonchen666.bungeecordmotdservercount;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerCountManager implements Listener {
    private final BungeecordMOTDServerCount plugin;
    private LocalTime lastCheckTime = LocalTime.now();
    private int lastCheck = -1;

    public ServerCountManager(BungeecordMOTDServerCount plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPing(ProxyPingEvent e) {
        ServerPing ping = e.getResponse();
        int serverAmount = plugin.getProxy().getServers().size();
        int onlineAmount = serversOnline();
        float percentOnline = Float.parseFloat(String.valueOf(onlineAmount)) / Float.parseFloat(String.valueOf(serverAmount));
        String chosen = (percentOnline > 0.5 ? ChatColor.GREEN : percentOnline == 0.5 ? ChatColor.YELLOW : ChatColor.DARK_RED).toString();
        ping.setDescription(plugin.getConfig().getString("messages.motd")
                .replace("${motd}", ping.getDescription())
                .replace("${total}", String.valueOf(serverAmount))
                .replace("${online}", String.valueOf(onlineAmount))
                .replace("${color}", ChatColor.translateAlternateColorCodes('&', chosen))
                .replaceAll("\\\\n", "\n"));
        e.setResponse(ping);
    }

    public final int serversOnline() {
        if (lastCheckTime.isAfter(LocalTime.now()) && lastCheck != -1) {
            return lastCheck;
        }
        AtomicInteger amountOnline = new AtomicInteger();
        plugin.getProxy().getServers().forEach((s, serverInfo) -> serverInfo.ping((result, error) -> {
            if (error != null) amountOnline.incrementAndGet();
        }));
        lastCheckTime = LocalTime.now();
        lastCheck = amountOnline.get();
        return lastCheck;
    }
}
