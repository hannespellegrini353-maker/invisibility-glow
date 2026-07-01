package com.example.invisglow.client;

import com.example.invisglow.command.GlowCommands;
import com.example.invisglow.config.GlowConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

/**
 * Haupt-Einstiegspunkt der Mod auf der Client-Seite.
 * <p>
 * Diese Mod besitzt bewusst KEINEN gemeinsamen ({@code ModInitializer}) Einstiegspunkt
 * und läuft ausschließlich clientseitig (siehe {@code "environment": "client"} in
 * der fabric.mod.json). Sie ist damit auch auf Vanilla-Servern ohne
 * Server-seitige Fabric-API installierbar.
 * <p>
 * Die eigentliche Logik (Erkennung + Darstellung) findet NICHT hier statt,
 * sondern in {@link com.example.invisglow.mixin.EntityGlowMixin}, da dort direkt
 * in die vorhandene Vanilla-Render-Entscheidung eingegriffen wird
 * ({@code Entity#isGlowing()}). Dadurch ist keine eigene Tick- oder
 * Render-Schleife notwendig – die Erkennung "huckepack" auf dem ohnehin
 * pro Frame stattfindenden Aufruf von {@code isGlowing()}.
 */
public final class InvisibilityGlowClient implements ClientModInitializer {

    public static final String MOD_ID = "invisglow";

    @Override
    public void onInitializeClient() {
        // Konfiguration früh laden, damit sie beim ersten Render-Aufruf bereits verfügbar ist.
        GlowConfig.getInstance();

        // Registrierung des optionalen Chat-Kommandos zum Umschalten der Funktion zur Laufzeit.
        ClientCommandRegistrationCallback.EVENT.register(GlowCommands::register);
    }
}
