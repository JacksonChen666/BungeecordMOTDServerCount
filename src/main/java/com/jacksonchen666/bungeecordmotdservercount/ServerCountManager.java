package com.jacksonchen666.bungeecordmotdservercount;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerCountManager implements Listener {
    private final Plugin plugin;
    private LocalTime lastCheckTime = LocalTime.now();
    private int lastCheck = -1;

    public ServerCountManager(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPing(ProxyPingEvent e) {
        ServerPing ping = e.getResponse();
        int serverAmount = plugin.getProxy().getServers().size();
        int onlineAmount = serversOnline();
        float percentOnline = onlineAmount / serverAmount;
        char online = ChatColor.GREEN.toString().charAt(0);
        char half = ChatColor.YELLOW.toString().charAt(0);
        char offline = ChatColor.DARK_RED.toString().charAt(0);
        ping.setDescription(BungeecordMOTDServerCount.config.getString("messages.motd")
                .replace("${motd}", ping.getDescription())
                .replace("${total}", String.valueOf(serverAmount))
                .replace("${online}", String.valueOf(onlineAmount))
                .replace("${color}", ChatColor.translateAlternateColorCodes('&', "&" + (percentOnline >= 0.5 ? online : percentOnline > 0 ? half : offline)))
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