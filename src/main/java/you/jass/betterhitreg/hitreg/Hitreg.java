package you.jass.betterhitreg.hitreg;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import you.jass.betterhitreg.settings.Commands;
import you.jass.betterhitreg.settings.Settings;
import you.jass.betterhitreg.settings.Toggle;
import you.jass.betterhitreg.ui.UIUtils;
import you.jass.betterhitreg.utility.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static you.jass.betterhitreg.utility.MultiVersion.message;

public class Hitreg {
    public static MinecraftClient client;
    public static int lastTarget;
    public static LivingEntity target;
    public static int tick;
    public static long lastAttack;
    public static long lastAttacked;
    public static long lastAnimation;
    public static boolean alreadyAnimated;
    public static boolean alreadyKnockedBack;
    public static boolean wasMovingForward;
    public static boolean sprintIsReset = true;
    public static boolean fighting = false;
    public static boolean wasGhosted;
    public static boolean newTarget = true;
    public static boolean hitByAnother;
    public static boolean targetHasShield;
    public static boolean targetIsBlocking;
    public static RegQueue last100Regs = new RegQueue(100);
    public static Vec3d lastAttackLocation = Vec3d.ZERO;
    public static Vec3d targetLocation = Vec3d.ZERO;
    public static Vec3d previousTargetLocation = Vec3d.ZERO;
    public static long fightStartedAt;
    public static double ground;
    public static boolean bothAlive;
    public static boolean withinFight;
    public static boolean targetInvisible;
    public static double distance;
    public static int playerId;
    public static long lastJumpTimestamp;
    public static boolean wasOnGround;
    public static int lastJumpAge;
    public static int hurtAge;
    public static boolean wasOnGroundWhenHit;
    public static int shouldMuffle;
    public static int fightsThisSession;
    public static float muffleAmount;
    public static long lastNonGhost;
    public static boolean tutorialAlreadySeen;


    public static void tick() {
        if (client.player == null || client.world == null) return;
        tick++;
        playerId = client.player.getId();

        int metronome = Settings.getInt("metronome");

        if (metronome >= 10) {
            if (tick % metronome == 0) {
                //version 1.21.10-
                //Hitreg.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1));

                //version 1.21.11+
                Hitreg.client.getSoundManager().play(PositionedSoundInstance.ui(SoundEvents.UI_BUTTON_CLICK, 1));
            }
        }

        muffleAmount = (Math.max(0, Math.min(1, Settings.getFloat("muffle_amount"))));

        UIUtils.update();
        updateFightState();
        updateGround();

        boolean movingForward = client.options.forwardKey.isPressed();
        if (movingForward && !wasMovingForward) sprintIsReset = true;
        wasMovingForward = movingForward;

        boolean onGround = client.player.isOnGround();
        wasOnGround = onGround;

        //if the fight ended, clear all expected hits to prevent any false ghosts, else remove all unneeded hits naturally
        if (!withinFight) {
            if (fighting) {
                long duration = (System.currentTimeMillis() - fightStartedAt) / 1_000;

                //if the fight lasted at least 10 seconds, at most 10 minutes, and you hit at least once, track it
                if (duration >= 10 && duration <= 600 && lastNonGhost >= fightStartedAt) {
                    fightsThisSession++;
                    Settings.addFight(duration);
                    if (Toggle.ALERT_FIGHTS.toggled()) message("fight §7took §f" + formatTime(duration) + " §7(#" + fightsThisSession + "/#" + Settings.getInt("total_fights") + ")", "/hitreg alertDelays");
                }
            }

            fighting = false;
            targetLocation = Vec3d.ZERO;
            previousTargetLocation = Vec3d.ZERO;
            HitTracker.clear();
        } else {
            HitTracker.process();
        }

        //if the target moves backwards, they may be taking knockback
        if (targetTakingKnockback() && !alreadyKnockedBack) {
            long knockbackDelay = System.currentTimeMillis() - lastAttack;
            if (Toggle.ALERT_DELAYS.toggled() && knockbackDelay <= 500)
                message("knockback §7took at minimum §f" + knockbackDelay + "§7ms", "/hitreg alertDelays");
            alreadyKnockedBack = true;
        }

        if (target != null) {
            targetInvisible = target.isInvisible() || target.isSpectator();
            previousTargetLocation = targetLocation;
            targetLocation = MultiVersion.getBasePosition(target);
        }

        if (Settings.isTutorial() && !tutorialAlreadySeen) {
            message("Thanks for using BetterHitreg!", "/hitreg");
            message("use /hitreg or press " + Commands.getUIKey() + " to configure", "/hitreg");
            message("(you can click on these messages)", "/hitreg");
            tutorialAlreadySeen = true;
        }
    }

    public static String formatTime(long duration) {
        long minutes = TimeUnit.SECONDS.toMinutes(duration);
        long seconds = duration % 60;
        if (minutes == 0 && seconds == 0) return "no time";

        String m = "";
        String s = "";

        if (minutes > 1) m = minutes + " minutes";
        if (minutes == 1) m = minutes + " minute";
        if (seconds > 1) s = seconds + " seconds";
        if (seconds == 1) s = seconds + " second";

        if (minutes == 0) return s;
        if (seconds == 0) return m;
        return m + " " + s;
    }

    public static void updateGround() {
        if (client.player.isOnGround()) ground = client.player.getY();
        else {
            int x = client.player.getBlockX();
            int y = client.player.getBlockY();
            int z = client.player.getBlockZ();

            for (int i = 1; i <= 3; i++) {
                int under = y - i;
                BlockPos checkPos = new BlockPos(x, under, z);

                if (!client.world.isAir(checkPos)) {
                    ground = under + 1;
                    break;
                }
            }
        }
    }

    public static int getPing(UUID uuid) {
        if (client.getNetworkHandler() == null) return 0;
        PlayerListEntry entry = client.getNetworkHandler().getPlayerListEntry(uuid);
        return entry == null ? -1 : entry.getLatency();
    }

    public static int getPlayersPing() {
        if (client.player == null) return 0;
        return getPing(client.player.getUuid());
    }

    public static int getTargetsPing() {
        if (target == null) return 0;
        return getPing(target.getUuid());
    }

    public static boolean isToggled() {
        if (!Toggle.TOGGLE.toggled()) return false;
        if (!withinFight || targetIsBlocking) return false;
        if (Toggle.SAFE_REGS_ONLY.toggled() && (newTarget || wasGhosted || hitByAnother)) return false;
        if (Toggle.IGNORE_SHIELD_HOLDERS.toggled() && targetHasShield) return false;
        return true;
    }

    public static void updateFightState() {
        bothAlive = client.player != null && target != null && client.player.isAlive() && target.isAlive() && !client.player.isSpectator() && !target.isSpectator();
        targetHasShield = target != null && Hitreg.target.isHolding(Items.SHIELD);
        targetIsBlocking = targetHasShield && Hitreg.target.isUsingItem();

        if (bothAlive) {
            distance = distanceToTarget();
            withinFight = distance <= 30;
        } else {
            withinFight = false;
        }
    }

    public static double distanceToTarget() {
        if (client.player == null || target == null) return Double.MAX_VALUE;
        return distanceFrom(MultiVersion.getBasePosition(client.player), MultiVersion.getBasePosition(target));
    }

    public static double distanceFromPlayer(Vec3d position) {
        if (client.player == null) return Double.MAX_VALUE;
        return distanceFrom(MultiVersion.getBasePosition(client.player), position);
    }

    public static double distanceFromTarget(Vec3d position) {
        if (target == null) return Double.MAX_VALUE;
        return distanceFrom(MultiVersion.getBasePosition(target), position);
    }

    public static double distanceFrom(Vec3d a, Vec3d b) {
        if (a == null || b == null) return Double.MAX_VALUE;
        double dx = a.x - b.x;
        double dz = a.z - b.z;
        return Math.sqrt(dx * dx + dz * dz);
    }

    public static boolean targetTakingKnockback(double angleThreshold, double minimumSpeed, boolean horizontalOnly) {
        if (client.player == null) return false;

        double mvx = targetLocation.x - previousTargetLocation.x;
        double mvy = targetLocation.y - previousTargetLocation.y;
        double mvz = targetLocation.z - previousTargetLocation.z;

        double pvx = lastAttackLocation.x - targetLocation.x;
        double pvy = lastAttackLocation.y - targetLocation.y;
        double pvz = lastAttackLocation.z - targetLocation.z;

        if (horizontalOnly) {
            mvy = 0;
            pvy = 0;
        }

        //are they moving fast enough
        double mLen = Math.sqrt(mvx*mvx + mvy*mvy + mvz*mvz);
        if (mLen < minimumSpeed) return false;

        //how far away are they
        double pLen = Math.sqrt(pvx*pvx + pvy*pvy + pvz*pvz);
        if (pLen == 0.0) return false;

        //normalized
        double dot = mvx*pvx + mvy*pvy + mvz*pvz;
        double cosAngle = dot / (mLen * pLen);

        //does the movement direction meet our threshold
        double thresholdCos = Math.cos(Math.toRadians(angleThreshold));

        //are they moving away
        return cosAngle < thresholdCos;
    }

    public static boolean targetTakingKnockback() {
        return targetTakingKnockback(120, 1e-3, true);
    }
}