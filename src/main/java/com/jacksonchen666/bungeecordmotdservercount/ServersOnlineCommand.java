package com.jacksonchen666.bungeecordmotdservercount;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class ServersOnlineCommand extends Command {
    private BungeecordMOTDServerCount plugin;

    public ServersOnlineCommand(String name) {
        super(name);
    }

    public ServersOnlineCommand(BungeecordMOTDServerCount plugin) {
        //noinspection SpellCheckingInspection
        this("serversonline");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length >= 1 && args[0].equals("reload")) {
            if (sender.hasPermission("BungeecordMOTDServerCount.reload")) {
                plugin.onLoad();
                sender.sendMessage(new TextComponent(ChatColors.color(plugin.getConfig().getString("messages.reload_finished"))));
            }
            else {
                sender.sendMessage(new TextComponent(ChatColors.color(plugin.getConfig().getString("messages.no_perms"))));
            }
            return;
        }
        sender.sendMessage(new TextComponent(plugin.getCountManager().getMessage(plugin.getConfig().getString("messages.command"))));
    }
}
