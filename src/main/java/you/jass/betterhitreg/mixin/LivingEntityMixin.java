package you.jass.betterhitreg.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import you.jass.betterhitreg.hitreg.Hitreg;
import you.jass.betterhitreg.settings.Toggle;
import you.jass.betterhitreg.utility.PingSound;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(at = @At("HEAD"), method = "jump")
    private void onJump(CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if ((Object) this == mc.player) {
            int age = mc.player.age;
            Hitreg.lastJumpAge = age;
            // ping fires ON JUMP, only if:
            // 1. player was recently hit (jumped AFTER the hit)
            // 2. player was on the ground when they were hit (not mid-air, which causes false positives)
            int ticksSinceHit = age - Hitreg.hurtAge;
            if (Toggle.JUMP_RESET_PING.toggled()
                    && Hitreg.wasMovingForward
                    && Hitreg.wasOnGroundWhenHit
                    && ticksSinceHit >= 0 && ticksSinceHit <= 8) {
                PingSound.play();
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "onDamaged")
    private void onDamaged(CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if ((Object) this == mc.player) {
            Hitreg.hurtAge = mc.player.age;
            Hitreg.wasOnGroundWhenHit = mc.player.isOnGround();
        }
    }
}
