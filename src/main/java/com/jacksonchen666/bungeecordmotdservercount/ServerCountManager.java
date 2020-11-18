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
        ping.setDescription(getMessage(plugin.getConfig().getString("messages.motd")).replace("${motd}", ping.getDescription()).replaceAll("\\\\n", "\n"));
        e.setResponse(ping);
    }

    public final String getMessage(String toReplace) {
        int serverAmount = plugin.getProxy().getServers().size();
        int onlineAmount = serversOnline();
        int percentOnline = (int) (Float.parseFloat(String.valueOf(onlineAmount)) / Float.parseFloat(String.valueOf(serverAmount)) * 100);
        String chosen = plugin.getColorsConfig().stream().filter(setting -> percentOnline >= setting.start && percentOnline <= setting.end).findFirst().map(setting -> setting.color).orElse(ChatColor.WHITE.toString());
        return ChatColors.color(toReplace.replace("${total}", String.valueOf(serverAmount)).replace("${online}", String.valueOf(onlineAmount)).replace("${color}", chosen));
    }

    public final int serversOnline() {
        if (lastCheckTime.isAfter(LocalTime.now().plusSeconds(plugin.getConfig().getInt("settings.cache_time"))) && lastCheck != -1) {
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
