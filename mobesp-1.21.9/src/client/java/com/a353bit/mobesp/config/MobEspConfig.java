package com.a353bit.mobesp.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Handles persistent saving of ESP settings.
 * The file is written to .minecraft/config/mobesp.json
 */
public class MobEspConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("mobesp.json");

    // Saved data
    public String mobId = "minecraft:cow";
    public boolean enabled = false;

    private static MobEspConfig instance;

    public static MobEspConfig get() {
        if (instance == null) {
            instance = load();
        }
        return instance;
    }

    private static MobEspConfig load() {
        if (Files.exists(CONFIG_PATH)) {
            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                MobEspConfig loaded = GSON.fromJson(reader, MobEspConfig.class);
                if (loaded != null) {
                    return loaded;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new MobEspConfig();
    }

    public void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(this, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
