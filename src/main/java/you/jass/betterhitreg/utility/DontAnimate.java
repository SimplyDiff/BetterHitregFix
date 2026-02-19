package you.jass.betterhitreg.utility;

import net.minecraft.entity.damage.DamageSource;

public class DontAnimate extends DamageSource {
    public final DamageSource wrapped;

    public DontAnimate(DamageSource wrapped) {
        super(wrapped.getTypeRegistryEntry());
        this.wrapped = wrapped;
    }
}