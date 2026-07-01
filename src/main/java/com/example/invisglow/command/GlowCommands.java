package com.example.invisglow.command;

import com.example.invisglow.config.GlowConfig;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.BoolArgumentType.getBool;

/**
 * Registriert das clientseitige Kommando {@code /invisglow}, mit dem sich die
 * Mod zur Laufzeit konfigurieren lässt, ohne die Config-Datei manuell
 * bearbeiten zu müssen.
 * <p>
 * Nutzung:
 * <pre>
 *   /invisglow                     -> zeigt aktuellen Status
 *   /invisglow enabled &lt;true|false&gt;      -> Feature global an/aus
 *   /invisglow includeSelf &lt;true|false&gt;  -> eigenen Spieler mit einbeziehen
 * </pre>
 * <p>
 * Dies ist rein optional und dient der Erweiterbarkeit / Komfort - die
 * Kernfunktion der Mod (siehe {@code EntityGlowMixin}) funktioniert auch
 * ganz ohne dieses Kommando über die JSON-Konfigurationsdatei.
 */
public final class GlowCommands {

    private GlowCommands() {
    }

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher,
                                 CommandRegistryAccess registryAccess) {

        dispatcher.register(literal("invisglow")
                .executes(GlowCommands::showStatus)
                .then(literal("enabled")
                        .then(argument("value", bool())
                                .executes(GlowCommands::setEnabled)))
                .then(literal("includeSelf")
                        .then(argument("value", bool())
                                .executes(GlowCommands::setIncludeSelf))));
    }

    private static int showStatus(CommandContext<FabricClientCommandSource> ctx) {
        GlowConfig config = GlowConfig.getInstance();
        ctx.getSource().sendFeedback(Text.literal(
                "[InvisibilityGlow] aktiviert=" + config.isEnabled()
                        + ", eigenerSpieler=" + config.isGlowLocalPlayer()));
        return 1;
    }

    private static int setEnabled(CommandContext<FabricClientCommandSource> ctx) {
        boolean value = getBool(ctx, "value");
        GlowConfig.getInstance().setEnabled(value);
        ctx.getSource().sendFeedback(Text.literal("[InvisibilityGlow] aktiviert = " + value));
        return 1;
    }

    private static int setIncludeSelf(CommandContext<FabricClientCommandSource> ctx) {
        boolean value = getBool(ctx, "value");
        GlowConfig.getInstance().setGlowLocalPlayer(value);
        ctx.getSource().sendFeedback(Text.literal("[InvisibilityGlow] eigenerSpieler = " + value));
        return 1;
    }
}
