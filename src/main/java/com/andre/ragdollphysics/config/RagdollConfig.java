package com.andre.ragdollphysics.config;

import com.andre.ragdollphysics.RagdollPhysics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;

/** Small dependency-free configuration file. */
public final class RagdollConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("ragdollphysics.json");

    private static RagdollConfig instance = new RagdollConfig();

    /** Accepted values: COMPLETE or ACTIVE. */
    public String physicsMode = "ACTIVE";

    private RagdollConfig() {
    }

    public static RagdollConfig get() {
        return instance;
    }

    public RagdollMode getPhysicsMode() {
        return RagdollMode.fromConfig(physicsMode);
    }

    public static void load() {
        if (Files.notExists(CONFIG_PATH)) {
            save();
            return;
        }

        try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
            RagdollConfig loaded = GSON.fromJson(reader, RagdollConfig.class);
            instance = loaded != null ? loaded : new RagdollConfig();
            String mode = instance.physicsMode;
            if (!"ACTIVE".equalsIgnoreCase(mode) && !"COMPLETE".equalsIgnoreCase(mode)) {
                instance.physicsMode = "ACTIVE";
            } else {
                instance.physicsMode = mode.trim().toUpperCase(Locale.ROOT);
            }
        } catch (IOException | JsonParseException exception) {
            RagdollPhysics.LOGGER.error("Could not read config {}; using defaults.", CONFIG_PATH, exception);
            instance = new RagdollConfig();
        }
    }

    public static void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            Path temporaryPath = CONFIG_PATH.resolveSibling(CONFIG_PATH.getFileName() + ".tmp");

            try (Writer writer = Files.newBufferedWriter(temporaryPath)) {
                GSON.toJson(instance, writer);
            }

            try {
                Files.move(temporaryPath, CONFIG_PATH,
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException atomicMoveException) {
                Files.move(temporaryPath, CONFIG_PATH, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            RagdollPhysics.LOGGER.error("Could not save config {}.", CONFIG_PATH, exception);
        }
    }
}
