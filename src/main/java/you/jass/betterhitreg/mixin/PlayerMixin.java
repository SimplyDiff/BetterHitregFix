package you.jass.betterhitreg.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import you.jass.betterhitreg.hitreg.Hitreg;


@Mixin(ClientPlayerEntity.class)
public abstract class PlayerMixin {
    @Inject(method = "addCritParticles", at = @At("HEAD"), cancellable = true)
    private void addCritParticles(Entity target, CallbackInfo ci) {
        if (Hitreg.isToggled()) ci.cancel();
    }

    @Inject(method = "addEnchantedHitParticles", at = @At("HEAD"), cancellable = true)
    private void addEnchantedHitParticles(Entity target, CallbackInfo ci) {
        if (Hitreg.isToggled()) ci.cancel();
    }
}