package com.example.invisglow.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Einfaches, leichtgewichtiges JSON-Konfigurationssystem.
 * <p>
 * Es wird bewusst auf externe Config-Libraries (z. B. Cloth Config) verzichtet,
 * damit die Mod ohne zusätzliche Abhängigkeiten funktioniert. Die Datei liegt
 * unter {@code config/invisglow.json} im Minecraft-Verzeichnis.
 * <p>
 * Die Konfiguration wird beim Start einmalig geladen und im Arbeitsspeicher
 * gehalten (kein wiederholtes Disk-IO zur Laufzeit / kein Tick-Loop nötig).
 */
public final class GlowConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = "invisglow.json";

    private static GlowConfig instance;

    // ---- Einstellungen -----------------------------------------------

    /** Globaler Schalter, um das gesamte Feature ein-/auszuschalten. */
    private boolean enabled = true;

    /**
     * Soll der eigene (lokale) Spieler ebenfalls geglowt werden,
     * wenn er sich selbst unsichtbar macht? Standardmäßig deaktiviert,
     * da man sich selbst i. d. R. ohnehin als transparentes Modell sieht.
     */
    private boolean glowLocalPlayer = false;

    /**
     * Soll die Outline auch dann angezeigt werden, wenn der Spieler zusätzlich
     * durch Wände sichtbar sein soll (rein kosmetisch, betrifft nur die Farbe/Optik –
     * für zukünftige Erweiterungen wie Team-Farben vorbereitet).
     */
    private boolean useTeamColor = true;

    // --------------------------------------------------------------------

    private GlowConfig() {
    }

    /**
     * Lädt die Konfiguration von der Festplatte oder erstellt eine neue
     * Standard-Konfiguration, falls noch keine existiert.
     */
    public static synchronized GlowConfig getInstance() {
        if (instance == null) {
            instance = load();
        }
        return instance;
    }

    private static Path configPath() {
        return FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
    }

    private static GlowConfig load() {
        Path path = configPath();

        if (Files.exists(path)) {
            try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                GlowConfig loaded = GSON.fromJson(reader, GlowConfig.class);
                if (loaded != null) {
                    return loaded;
                }
            } catch (IOException | com.google.gson.JsonSyntaxException e) {
                System.err.println("[InvisibilityGlow] Konnte Konfiguration nicht laden, verwende Standardwerte: " + e.getMessage());
            }
        }

        GlowConfig defaultConfig = new GlowConfig();
        defaultConfig.save();
        return defaultConfig;
    }

    /** Speichert die aktuelle Konfiguration persistent auf die Festplatte. */
    public void save() {
        Path path = configPath();
        try {
            Files.createDirectories(path.getParent());
            try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                GSON.toJson(this, writer);
            }
        } catch (IOException e) {
            System.err.println("[InvisibilityGlow] Konnte Konfiguration nicht speichern: " + e.getMessage());
        }
    }

    // ---- Getter / Setter -----------------------------------------------

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        save();
    }

    public boolean isGlowLocalPlayer() {
        return glowLocalPlayer;
    }

    public void setGlowLocalPlayer(boolean glowLocalPlayer) {
        this.glowLocalPlayer = glowLocalPlayer;
        save();
    }

    public boolean isUseTeamColor() {
        return useTeamColor;
    }

    public void setUseTeamColor(boolean useTeamColor) {
        this.useTeamColor = useTeamColor;
        save();
    }
}
