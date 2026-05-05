package you.jass.betterhitreg.hitreg;

<<<<<<< HEAD
import static you.jass.betterhitreg.hitreg.Hitreg.*;
import static you.jass.betterhitreg.hitreg.Hitreg.alreadyAnimated;
import static you.jass.betterhitreg.hitreg.Hitreg.alreadyKnockedBack;
import static you.jass.betterhitreg.hitreg.Hitreg.lastTarget;
import static you.jass.betterhitreg.hitreg.Hitreg.newTarget;
import static you.jass.betterhitreg.hitreg.Hitreg.sprintIsReset;
import static you.jass.betterhitreg.utility.MultiVersion.*;

import java.util.ArrayList;
=======
>>>>>>> e33f8c601fc6870e7201befb111ca8d225c89255
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;
import you.jass.betterhitreg.settings.Settings;
import you.jass.betterhitreg.settings.Toggle;
import you.jass.betterhitreg.utility.HitTracker;
import you.jass.betterhitreg.utility.MultiVersion;
import you.jass.betterhitreg.utility.OnlyAnimate;
import you.jass.betterhitreg.utility.Scheduler;

<<<<<<< HEAD
public class Hit {

=======
import java.util.ArrayList;

import static you.jass.betterhitreg.hitreg.Hitreg.*;
import static you.jass.betterhitreg.hitreg.Hitreg.alreadyAnimated;
import static you.jass.betterhitreg.hitreg.Hitreg.alreadyKnockedBack;
import static you.jass.betterhitreg.hitreg.Hitreg.lastTarget;
import static you.jass.betterhitreg.hitreg.Hitreg.newTarget;
import static you.jass.betterhitreg.hitreg.Hitreg.sprintIsReset;
import static you.jass.betterhitreg.utility.MultiVersion.*;

public class Hit {
>>>>>>> e33f8c601fc6870e7201befb111ca8d225c89255
    public LivingEntity target;
    public float cooldown;
    public boolean tooEarlyForDamage;
    public boolean tooEarlyForSpecial;
    public boolean hadShield;
    public boolean wasBlocked;
    public boolean wasSprinting;
    public boolean wasFalling;
    public boolean wasOnGround;
    public boolean wasClimbing;
    public boolean wasTouchingWater;
    public boolean wasInVehicle;
    public boolean wasBlind;
    public boolean wasInvisible;
    public boolean wasHoldingSword;
    public boolean swordHadSharpness;
    public boolean sprintWasReset;

    public boolean shouldAnimate;
    public boolean shouldMakeSound;
    public boolean shouldSoundBeLegacy;
    public boolean shouldSpawnParticles;
    public boolean shouldKnockback;
    public boolean shouldCrit;
    public boolean shouldSweep;
    public boolean shouldPick;
    public boolean shouldFullPick;
    public boolean shouldHalfPick;
    public boolean shouldSpawnSharpnessParticles;

    public SoundEvent expectedSound;
    public HitType type;
    public ArrayList<HitType> potentialServerTypes = new ArrayList<>();
    public boolean wasServerRight;
    public boolean wasAnimated;
    public boolean wasNewTarget;
    public boolean wasHitByAnother;
    public long timestamp;
<<<<<<< HEAD
    public double distance;
=======
>>>>>>> e33f8c601fc6870e7201befb111ca8d225c89255

    public Hit() {
        timestamp = System.currentTimeMillis();
    }

    public void updateSettings() {
        shouldAnimate = !Toggle.HIDE_ANIMATIONS.toggled() && !tooEarlyForDamage;
        shouldMakeSound = !Toggle.SILENCE_SELF.toggled();
        shouldSoundBeLegacy = Toggle.LEGACY_SOUNDS.toggled();
        shouldSpawnParticles = !Toggle.HIDE_ALL_PARTICLES.toggled();
<<<<<<< HEAD
        shouldSpawnSharpnessParticles =
            !Toggle.HIDE_OTHER_PARTICLES.toggled() &&
            (swordHadSharpness || Toggle.PARTICLES_EVERY_HIT.toggled());
=======
        shouldSpawnSharpnessParticles = !Toggle.HIDE_OTHER_PARTICLES.toggled() && (swordHadSharpness || Toggle.PARTICLES_EVERY_HIT.toggled());
>>>>>>> e33f8c601fc6870e7201befb111ca8d225c89255
    }

    public void load() {
        shouldKnockback = !tooEarlyForSpecial && wasSprinting && sprintWasReset;
<<<<<<< HEAD
        shouldCrit =
            !tooEarlyForSpecial &&
            !shouldKnockback &&
            wasFalling &&
            !wasOnGround &&
            !wasClimbing &&
            !wasTouchingWater &&
            !wasInVehicle &&
            !wasBlind;
        shouldSweep =
            !tooEarlyForSpecial &&
            wasHoldingSword &&
            wasOnGround &&
            (!wasSprinting);
=======
        shouldCrit = !tooEarlyForSpecial && !shouldKnockback && wasFalling && !wasOnGround && !wasClimbing && !wasTouchingWater && !wasInVehicle && !wasBlind;
        shouldSweep = !tooEarlyForSpecial && wasHoldingSword && wasOnGround && (!wasSprinting);
>>>>>>> e33f8c601fc6870e7201befb111ca8d225c89255
        shouldPick = !shouldKnockback && !shouldCrit && !shouldSweep;
        shouldFullPick = !tooEarlyForSpecial && shouldPick;
        shouldHalfPick = !shouldFullPick && shouldPick;

        type = HitType.of(this);
        if (type == null) return;
        expectedSound = type.getMainSound();

        if (!tooEarlyForDamage) HitTracker.add(this);
<<<<<<< HEAD
        if (Hitreg.isToggled()) Scheduler.schedule(
            Settings.getHitreg(),
            this::run
        );
=======
        if (Hitreg.isToggled()) Scheduler.schedule(Settings.getHitreg(), this::run);
>>>>>>> e33f8c601fc6870e7201befb111ca8d225c89255
    }

    public void run() {
        if (target == null) return;
        updateSettings();

<<<<<<< HEAD
        if (shouldAnimate) target.onDamaged(
            new OnlyAnimate(target.getDamageSources().generic())
        );
=======
        if (shouldAnimate) target.onDamaged(new OnlyAnimate(target.getDamageSources().generic()));
>>>>>>> e33f8c601fc6870e7201befb111ca8d225c89255

        if (shouldMakeSound) {
            Vec3d location = getLerpedPosition(target);

            if (shouldSoundBeLegacy) {
                if (!tooEarlyForDamage) HitType.HALF_PICK.playSounds(location);
            } else {
                type.playSounds(location);
            }
        }

        if (shouldSpawnParticles) {
            if (shouldCrit) playParticles("CRIT", target);
<<<<<<< HEAD
            if (shouldSpawnSharpnessParticles) playParticles(
                "ENCHANTED_HIT",
                target
            );
        }
    }
}
=======
            if (shouldSpawnSharpnessParticles) playParticles("ENCHANTED_HIT", target);
        }
    }
}
>>>>>>> e33f8c601fc6870e7201befb111ca8d225c89255
