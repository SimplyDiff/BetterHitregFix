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
            Hitreg.lastJumpAge = mc.player.age;
        }
    }

    @Inject(at = @At("HEAD"), method = "onDamaged")
    private void onDamaged(CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if ((Object) this == mc.player) {
            Hitreg.hurtAge = mc.player.age;
            // a good jump reset: jumped within 6 ticks (300ms) of being hit
            if (Toggle.JUMP_RESET_PING.toggled()
                    && Hitreg.wasMovingForward
                    && Math.abs(Hitreg.lastJumpAge - Hitreg.hurtAge) <= 6) {
                PingSound.play();
            }
        }
    }
}
