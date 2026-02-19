package you.jass.betterhitreg.mixin;

import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import you.jass.betterhitreg.settings.Toggle;
import you.jass.betterhitreg.utility.MultiVersion;

import static you.jass.betterhitreg.hitreg.Hitreg.*;

@Mixin(EntityRenderer.class)
public abstract class RenderMixin {
    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private void entity(Entity entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof PlayerEntity || entity instanceof DisplayEntity.TextDisplayEntity) {
            if (!shouldRender(entity)) cir.setReturnValue(false);
        }
    }

    @Unique
    private boolean shouldRender(Entity entity) {
        if (!Toggle.HIDE_OTHER_FIGHTS.toggled() || client.player == null || entity.getId() == lastTarget) return true;
        if (distanceFromPlayer(MultiVersion.getBasePosition(entity)) <= 5 || distanceToTarget() > 10 || System.currentTimeMillis() - lastAttack > 5000 || !bothAlive) return true;
        return false;
    }
}