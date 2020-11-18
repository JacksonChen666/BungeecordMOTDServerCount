package com.jacksonchen666.bungeecordmotdservercount;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class BungeecordMOTDServerCount extends Plugin {
    static Configuration config;

    @Override
    public void onLoad() {
        saveDefaultConfig();
        config = loadConfig();
    }

    @Override
    public void onEnable() {
        getProxy().getPluginManager().registerListener(this, new ServerCountManager(this));
    }

    @Override
    public void onDisable() {
        saveConfig();
    }

    private void saveDefaultConfig() {
        if (!getDataFolder().exists())
            //noinspection ResultOfMethodCallIgnored
            getDataFolder().mkdir();

        File file = new File(getDataFolder(), "config.yml");

        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveConfig() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, new File(getDataFolder(), "config.yml"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Configuration loadConfig() {
        try {
            return ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}