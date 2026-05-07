package you.jass.betterhitreg.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import you.jass.betterhitreg.hitreg.Hitreg;
import you.jass.betterhitreg.settings.Settings;
import you.jass.betterhitreg.settings.Toggle;
import you.jass.betterhitreg.utility.PacketProcessor;
import you.jass.betterhitreg.utility.PingSound;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(at = @At("HEAD"), method = "jump")
    private void onJump(CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if ((Object) this == mc.player) {
            Hitreg.jumpReset.lastJumpAge = mc.player.age;

            if (!Toggle.JUMP_RESET_PING.toggled()) return;
            if (!Hitreg.fight.wasMovingForward || !Hitreg.jumpReset.wasOnGroundWhenHit) return;

            long jumpTime = System.currentTimeMillis();
            long timeSinceHit = jumpTime - PacketProcessor.tookDamageTimestamp;
            if (timeSinceHit < 0) return; // jumped before the hit packet arrived

            // get one-way ping (ms): the jump packet takes this long to reach the server
            int ping = 0;
            if (mc.getNetworkHandler() != null) {
                PlayerListEntry entry = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
                if (entry != null) ping = entry.getLatency();
            }

            // server sees: timeSinceHit + ping (your reaction + travel time of your jump packet)
            long serverGap = timeSinceHit + ping;
            long windowMs = Settings.getInt("jr_window") * 50L;

            if (serverGap <= windowMs) {
                PingSound.playJumpReset();
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "onDamaged")
    private void onDamaged(CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if ((Object) this == mc.player) {
            Hitreg.jumpReset.hurtAge = mc.player.age;
            Hitreg.jumpReset.wasOnGroundWhenHit = mc.player.isOnGround();
        }
    }
}
