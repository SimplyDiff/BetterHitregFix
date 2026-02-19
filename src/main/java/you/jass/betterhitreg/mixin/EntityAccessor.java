package you.jass.betterhitreg.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface EntityAccessor {
    @Invoker("getHurtSound")
    SoundEvent getHurtSound(DamageSource source);
}