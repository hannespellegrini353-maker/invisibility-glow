package com.example.invisglow.mixin;

import com.example.invisglow.config.GlowConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Kern der Mod: Diese Mixin-Klasse hängt sich an {@link Entity#isGlowing()}.
 * <p>
 * <b>Warum diese Methode?</b><br>
 * {@code isGlowing()} wird vom Vanilla-Rendering (siehe {@code LivingEntityRenderer})
 * bei JEDEM Frame für jede potenziell sichtbare Entität abgefragt, um zu
 * entscheiden, ob zusätzlich zum normalen Modell (oder anstelle davon, falls
 * die Entität unsichtbar ist) ein Outline-Pass in den
 * {@code OutlineVertexConsumerProvider} gezeichnet werden soll. Das ist exakt
 * der Mechanismus, den auch der Vanilla-Zaubertrank "Leuchten" (Glowing)
 * nutzt, um z. B. unsichtbare, geglowte Mobs sichtbar zu machen.
 * <p>
 * Anstatt den echten {@code StatusEffects.GLOWING}-Effekt künstlich auf den
 * Spieler zu setzen (was serverseitige Synchronisation bräuchte und als
 * Status-Icon im HUD sichtbar wäre), wird hier rein clientseitig der
 * Rückgabewert von {@code isGlowing()} überschrieben, wenn:
 * <ol>
 *     <li>Die Mod aktiviert ist,</li>
 *     <li>die Entität ein {@link PlayerEntity} ist,</li>
 *     <li>dieser Spieler aktuell den Unsichtbarkeits-Effekt besitzt (Info, die der
 *         Server dem Client bereits über den normalen Status-Effekt-Sync mitteilt),</li>
 *     <li>und wir uns auf der Client-Welt befinden.</li>
 * </ol>
 * <p>
 * Da hierbei nur ein bereits vorhandener Getter minimal erweitert wird,
 * entsteht <b>kein zusätzlicher Tick- oder Scan-Loop</b> – die Erkennung
 * "reitet" auf dem ohnehin stattfindenden Render-Aufruf mit und ist damit
 * praktisch kostenlos (ein simpler HashMap-Lookup pro sichtbarer Entität
 * und Frame).
 */
@Mixin(Entity.class)
public abstract class EntityGlowMixin {

    /**
     * Injiziert am Ende von {@code isGlowing()} und überschreibt das Ergebnis
     * bei Bedarf. Der ursprüngliche Rückgabewert (z. B. echter Glowing-Effekt
     * oder Team-Glow) bleibt erhalten, falls unsere Bedingung nicht zutrifft –
     * wir schalten den Glow also nur zusätzlich EIN, nie aus.
     *
     * @param cir Callback-Info, über die der Rückgabewert überschrieben werden kann.
     */
    @Inject(method = "isGlowing", at = @At("RETURN"), cancellable = true)
    private void invisglow$glowInvisiblePlayers(CallbackInfoReturnable<Boolean> cir) {
        // Bereits glowing (z. B. echter Trank/Team) -> nichts zu tun.
        if (cir.getReturnValue()) {
            return;
        }

        Entity self = (Entity) (Object) this;

        // Nur auf der Client-Welt aktiv werden (rein clientseitige Darstellung).
        if (!self.getEntityWorld().isClient()) {
            return;
        }

        // Nur Spieler betreffen - andere Entitäten bleiben komplett unangetastet.
        if (!(self instanceof PlayerEntity player)) {
            return;
        }

        GlowConfig config = GlowConfig.getInstance();
        if (!config.isEnabled()) {
            return;
        }

        // Eigenen Spieler optional ausschließen (Standard: ausgeschlossen).
        if (!config.isGlowLocalPlayer() && isLocalPlayer(player)) {
            return;
        }

        // Kern der Erkennung: Besitzt der Spieler aktuell den Unsichtbarkeits-Effekt?
        // Diese Information erhält der Client bereits regulär vom Server über das
        // Vanilla-Statuseffekt-Paket - es ist also KEINE Server-Mod notwendig.
        if (player.hasStatusEffect(StatusEffects.INVISIBILITY)) {
            cir.setReturnValue(true);
        }
    }

    /**
     * Prüft, ob es sich bei der übergebenen Entität um den lokal
     * gesteuerten Spieler-Client handelt.
     */
    private static boolean isLocalPlayer(PlayerEntity player) {
        MinecraftClient client = MinecraftClient.getInstance();
        return client.player != null && client.player == player;
    }
}
