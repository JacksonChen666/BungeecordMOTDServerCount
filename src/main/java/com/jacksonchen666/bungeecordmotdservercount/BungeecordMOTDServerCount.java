package com.jacksonchen666.bungeecordmotdservercount;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BungeecordMOTDServerCount extends Plugin {
    private final Map<Integer, Map<Integer, String>> colorsConfig = new HashMap<>();
    private Configuration config;
    private ServerCountManager countManager;

    @Override
    public void onLoad() {
        saveDefaultConfig();
        config = loadConfig();
        Configuration tempColorConfig = config.getSection("settings.colors");
        List<String> startings = tempColorConfig.getKeys().stream().filter(item -> item.contains("percent_")).collect(Collectors.toList());
        for (String start : startings) {
            String color = tempColorConfig.getString(start + ".color");
            try {
                colorsConfig.put(Integer.parseInt(start.replace("percent_", "")), new HashMap<Integer, String>() {{
                    put(tempColorConfig.getInt(start + ".end"), color.contains("&") && color.length() == 2 ? color : ChatColor.valueOf(color.toUpperCase()).toString());
                }});
            }
            catch (IllegalArgumentException e) {
                if (e.getMessage().equals("No enum constant net.md_5.bungee.api.ChatColor." + color)) {
                    System.err.println("Tried to get color from \"" + start + "\" but instead got \"" + color + "\" which does not exist.");
                }
                else {
                    throw e;
                }
            }
        }
    }

    @Override
    public void onEnable() {
        PluginManager pl = getProxy().getPluginManager();
        pl.registerListener(this, countManager = new ServerCountManager(this));
        pl.registerCommand(this, new ServersOnlineCommand(this));
    }

    @Override
    public void onDisable() {
        saveConfig();
        colorsConfig.clear();
    }

    private void saveDefaultConfig() {
        if (!getDataFolder().exists())
            //noinspection ResultOfMethodCallIgnored
            getDataFolder().mkdir();

        File file = new File(getDataFolder(), "config.yml");

        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void saveConfig() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, new File(getDataFolder(), "config.yml"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    Configuration loadConfig() {
        try {
            return ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ServerCountManager getCountManager() {
        return countManager;
    }

    Configuration getConfig() {
        return config;
    }

    public Map<Integer, Map<Integer, String>> getColorsConfig() {
        return colorsConfig;
    }
}
