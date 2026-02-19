package you.jass.betterhitreg.hitreg;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Vec3d;
import you.jass.betterhitreg.mixin.EntityAccessor;
import you.jass.betterhitreg.utility.MultiVersion;

import java.util.List;
import java.util.Arrays;

public enum HitType {
    TOO_EARLY(SoundEvents.ENTITY_PLAYER_ATTACK_WEAK),
    KNOCKBACK(SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK,
            SoundEvents.ENTITY_PLAYER_ATTACK_STRONG,
            SoundEvents.ENTITY_PLAYER_HURT),
    CRITICAL(SoundEvents.ENTITY_PLAYER_ATTACK_CRIT,
            SoundEvents.ENTITY_PLAYER_HURT),
    SWEEP(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP,
            SoundEvents.ENTITY_PLAYER_HURT),
    FULL_PICK(SoundEvents.ENTITY_PLAYER_ATTACK_STRONG,
            SoundEvents.ENTITY_PLAYER_HURT),
    HALF_PICK(SoundEvents.ENTITY_PLAYER_HURT);

    private final List<SoundEvent> sounds;

    HitType(SoundEvent... sounds) {
        this.sounds = Arrays.asList(sounds);
    }

    public void playSounds(Vec3d location) {
        MinecraftClient client = Hitreg.client;
        if (client.world == null || client.player == null) return;
        for (SoundEvent sound : sounds) {
            if (sound.equals(SoundEvents.ENTITY_PLAYER_HURT)) sound = getHurtSound();
            client.world.playSound(client.player, location.x, location.y, location.z, sound, SoundCategory.PLAYERS, 1, 1);
        }
    }

    public List<SoundEvent> getSounds() {
        return sounds;
    }

    public SoundEvent getMainSound() {
        //version 1.19.4 - 1.20.4
        //return sounds.isEmpty() ? null : sounds.get(0);

        //version 1.20.5+
        return sounds.getFirst();
    }

    public static HitType of(Hit hit) {
        if (hit.tooEarlyForDamage) return TOO_EARLY;
        if (hit.shouldKnockback) return KNOCKBACK;
        if (hit.shouldCrit) return CRITICAL;
        if (hit.shouldSweep) return SWEEP;
        if (hit.shouldFullPick) return FULL_PICK;
        if (hit.shouldHalfPick) return HALF_PICK;
        return null;
    }

    public static HitType of(SoundEvent sound) {
        if (sound.equals(SoundEvents.ENTITY_PLAYER_ATTACK_WEAK)) return TOO_EARLY;
        if (sound.equals(SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK)) return KNOCKBACK;
        if (sound.equals(SoundEvents.ENTITY_PLAYER_ATTACK_CRIT)) return CRITICAL;
        if (sound.equals(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP)) return SWEEP;
        if (sound.equals(SoundEvents.ENTITY_PLAYER_ATTACK_STRONG)) return FULL_PICK;
        if (sound.equals(SoundEvents.ENTITY_PLAYER_HURT)) return HALF_PICK;

        //version 1.21.1-
        //if (sound.getId().toTranslationKey().contains("hurt")) return HALF_PICK;

        //version 1.21.2+
        if (sound.id().toTranslationKey().contains("hurt")) return HALF_PICK;

        return null;
    }
    
    public static SoundEvent getHurtSound() {
        if (Hitreg.target == null) return SoundEvents.ENTITY_PLAYER_HURT;
        return ((EntityAccessor) Hitreg.target).getHurtSound(Hitreg.target.getDamageSources().generic());
    }
}
