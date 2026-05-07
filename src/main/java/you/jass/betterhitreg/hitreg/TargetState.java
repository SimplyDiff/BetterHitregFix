package you.jass.betterhitreg.hitreg;

import net.minecraft.entity.LivingEntity;

public class TargetState {
    public LivingEntity target;
    public int lastTarget;
    public LivingEntity lockedTarget;
    public int lockedTargetId = -1;
}
