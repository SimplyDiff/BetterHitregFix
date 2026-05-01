package you.jass.betterhitreg.utility;

//version 1.21.11
import net.minecraft.world.debug.gizmo.GizmoDrawing;

import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.*;

import org.joml.Matrix4f;
import you.jass.betterhitreg.hitreg.Hitreg;
import you.jass.betterhitreg.settings.Settings;
import you.jass.betterhitreg.settings.Toggle;

import java.awt.*;

import static you.jass.betterhitreg.hitreg.Hitreg.*;

public class Render {
    private static int FAR_HITBOX = 0xFFFFFFFF;
    private static int NEAR_HITBOX = 0xFFFFFFFF;
    private static int FAR_CROSS = 0xFFFFFFFF;
    private static int NEAR_CROSS = 0xFFFFFFFF;
    private static int FAR_RING = 0xFFFFFFFF;
    private static int NEAR_RING = 0xFFFFFFFF;
    private static int FAR_CROSS_WITH_HITBOX = 0xFFFFFFFF;
    private static int NEAR_CROSS_WITH_HITBOX = 0xFFFFFFFF;

    public static void updateColors() {
        FAR_HITBOX = getColor("hitbox_far_color", "hitbox_opacity");
        NEAR_HITBOX = getColor("hitbox_near_color", "hitbox_opacity");
        FAR_CROSS = getColor("cross_far_color", "cross_opacity");
        NEAR_CROSS = getColor("cross_near_color", "cross_opacity");
        FAR_RING = getColor("ring_far_color", "ring_opacity");
        NEAR_RING = getColor("ring_near_color", "ring_opacity");
        FAR_CROSS_WITH_HITBOX = getColor("cross_far_color_with_hitbox", "cross_opacity");
        NEAR_CROSS_WITH_HITBOX = getColor("cross_near_color_with_hitbox", "cross_opacity");
    }

    public static int getColor(String colorKey, String opacityKey) {
        String hex = Settings.get(colorKey);
        int opacity = Settings.getInt(opacityKey);
        if (hex == null) hex = "FFFFFF";
        int alpha = Math.max(0, Math.min(255, opacity));
        Color rgb = Color.decode("#" + hex.replace("#", ""));
        Color argb = new Color(rgb.getRed(), rgb.getGreen(), rgb.getBlue(), alpha);
        return argb.getRGB();
    }

    public static void render(Camera camera) {
        boolean isHitbox = Toggle.RENDER_HITBOX.toggled();
        boolean isCross = Toggle.RENDER_CROSS.toggled();
        boolean isRing = Toggle.RENDER_RING.toggled();

        if (client.player == null) return;
        if (!isHitbox && !isCross && !isRing) return;

        if (!Hitreg.bothAlive || Hitreg.targetInvisible) {
            if (isRing) ring(camera, 3, 64, 3, FAR_RING);
            return;
        }

        // =========================
        // DISTANCE (RANGE CHECK)
        // =========================
        Vec3d closest = getClosestPoint(client.player, target);
        double distance = client.player.getEyePos().squaredDistanceTo(closest);
        boolean inRange = distance <= 9; // 3 blocks (squared)

        // =========================
        // LOOKING AT THE ENTITY (KEY)
        // =========================
        var mc = Hitreg.client;
        var hit = mc.crosshairTarget; // <- equivalent to combat-hitboxes

        boolean isLookingAt = false;

        if (hit instanceof net.minecraft.util.hit.EntityHitResult ehr) {
            isLookingAt = ehr.getEntity() == target;
        }

        // =========================
        // FINAL CONDITION FOR COLOR DECISION
        // =========================
        boolean shouldBeRed = inRange && isLookingAt;

        // =========================
        // HITBOX
        // =========================
        if (isHitbox) {
            int color = shouldBeRed ? NEAR_HITBOX : FAR_HITBOX;
            box(camera, getBoundingBox(target), 3, color);
        }

        // =========================
        // CROSS
        // =========================
        if (isCross && distance <= 100) {
            int color = isHitbox
                    ? (shouldBeRed ? NEAR_CROSS_WITH_HITBOX : FAR_CROSS_WITH_HITBOX)
                    : (shouldBeRed ? NEAR_CROSS : FAR_CROSS);

            cross(camera, closest, 3, 30, 0.005, color);
        }

        // =========================
        // RING
        // =========================
        if (isRing) {
            int color = inRange ? NEAR_RING : FAR_RING;
            ring(camera, 3, 64, 3, color);
        }
    }

    public static Box getBoundingBox(Entity entity) {
        Vec3d lerpedPos = MultiVersion.getLerpedPosition(entity);
        Vec3d actualPos = MultiVersion.getBasePosition(entity);
        Vec3d delta = lerpedPos.subtract(actualPos);
        Box box = entity.getBoundingBox();
        return box.offset(delta);
    }

    public static void box(Camera camera, Box box, float thickness, int rgba) {
        //corners
        Vec3d c0 = new Vec3d(box.minX, box.minY, box.minZ);
        Vec3d c1 = new Vec3d(box.maxX, box.minY, box.minZ);
        Vec3d c2 = new Vec3d(box.maxX, box.minY, box.maxZ);
        Vec3d c3 = new Vec3d(box.minX, box.minY, box.maxZ);
        Vec3d c4 = new Vec3d(box.minX, box.maxY, box.minZ);
        Vec3d c5 = new Vec3d(box.maxX, box.maxY, box.minZ);
        Vec3d c6 = new Vec3d(box.maxX, box.maxY, box.maxZ);
        Vec3d c7 = new Vec3d(box.minX, box.maxY, box.maxZ);

        //bottom face
        line(camera, c0, c1, thickness, rgba);
        line(camera, c1, c2, thickness, rgba);
        line(camera, c2, c3, thickness, rgba);
        line(camera, c3, c0, thickness, rgba);

        //top face
        line(camera, c4, c5, thickness, rgba);
        line(camera, c5, c6, thickness, rgba);
        line(camera, c6, c7, thickness, rgba);
        line(camera, c7, c4, thickness, rgba);

        //vertical walls
        line(camera, c0, c4, thickness, rgba);
        line(camera, c1, c5, thickness, rgba);
        line(camera, c2, c6, thickness, rgba);
        line(camera, c3, c7, thickness, rgba);
    }

    public static void line(Camera camera, Vec3d start, Vec3d end, float thickness, int rgba) {
        //version 1.21.10-
        //MatrixStack ms = new MatrixStack();

        //version 1.21.8-
        //Vec3d cameraPosition = camera.getPos();

        //version 1.21.9+
        Vec3d cameraPosition = camera.getCameraPos();

        //version 1.21.8-
        //ms.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
        //ms.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180f));

        //version 1.21.10-
        //Matrix4f mat = ms.peek().getPositionMatrix();
        //Vec3d a = start.subtract(cameraPosition);
        //Vec3d b = end.subtract(cameraPosition);
        //Vec3d mid = a.add(b).multiply(0.5);
        //float distance = (float) mid.length();
        //float worldHalfWidth = thickness * distance * 0.001f;
        //Vec3d dir  = b.subtract(a).normalize();
        //Vec3d view = mid.normalize().multiply(-1);
        //Vec3d perpendicular = dir.crossProduct(view).normalize().multiply(worldHalfWidth);
        //Vec3d v0 = a.add(perpendicular);
        //Vec3d v1 = a.subtract(perpendicular);
        //Vec3d v2 = b.subtract(perpendicular);
        //Vec3d v3 = b.add(perpendicular);
        //MultiVersion.render(mat, v0, v1, v2, v3, rgba);

        //version 1.21.11+
        GizmoDrawing.line(start, end, rgba, thickness);
    }

    public static void ring(Camera camera, double radius, int segments, float thickness, int rgba) {
        if (segments < 3) segments = 3;

        Vec3d player = MultiVersion.getLerpedPosition(client.player);
        Vec3d center = new Vec3d(player.x, Hitreg.ground, player.z);

        double angleDelta = 2.0 * Math.PI / segments;
        double cosDelta = Math.cos(angleDelta);
        double sinDelta = Math.sin(angleDelta);

        double x = radius;
        double z = 0;

        Vec3d prev = new Vec3d(center.x + x, center.y, center.z + z);

        for (int i = 1; i <= segments; i++) {
            double nx = x * cosDelta - z * sinDelta;
            double nz = x * sinDelta + z * cosDelta;

            Vec3d next = new Vec3d(center.x + nx, center.y, center.z + nz);
            line(camera, prev, next, thickness, rgba);

            x = nx;
            z = nz;
            prev = next;
        }
    }

    public static void cross(Camera camera, Vec3d center, float pixelThickness, double pixelHalfLength, double nudgeTowardVec3d, int rgba) {
        //version 1.21.8-
        //Vec3d cameraPosition = camera.getPos();

        //version 1.21.9+
        Vec3d cameraPosition = camera.getCameraPos();

        Vec3d position = cameraPosition.subtract(center);

        if (nudgeTowardVec3d > 0.0) {
            Vec3d nudgeDir = position.normalize();
            center = center.add(nudgeDir.multiply(nudgeTowardVec3d));
            position = cameraPosition.subtract(center);
        }

        double distance = cameraPosition.distanceTo(center);

        double SCALE = 0.001;
        double worldHalfLength = pixelHalfLength * distance * SCALE;

        Vec3d forward = position.normalize();
        Vec3d worldUp = new Vec3d(0.0, 1.0, 0.0);
        Vec3d right = worldUp.crossProduct(forward);

        if (right.lengthSquared() < 1.0E-6) {
            right = new Vec3d(1.0, 0.0, 0.0);
        } else {
            right = right.normalize();
        }

        Vec3d up = forward.crossProduct(right).normalize();

        //horizontal
        Vec3d x0 = center.add(right.multiply(-worldHalfLength));
        Vec3d x1 = center.add(right.multiply( worldHalfLength));

        //vertical
        Vec3d y0 = center.add(up.multiply(-worldHalfLength));
        Vec3d y1 = center.add(up.multiply( worldHalfLength));

        line(camera, x0, x1, pixelThickness, rgba);
        line(camera, y0, y1, pixelThickness, rgba);
    }

    public static Vec3d getClosestPoint(Entity entity1, Entity entity2) {
        Vec3d eye = MultiVersion.getLerpedPosition(entity1).add(0, entity1.getEyeHeight(entity1.getPose()), 0);
        Box box = getBoundingBox(entity2);

        double closestX = clamp(eye.x, box.getMin(Direction.Axis.X), box.getMax(Direction.Axis.X));
        double closestY = clamp(eye.y, box.getMin(Direction.Axis.Y), box.getMax(Direction.Axis.Y));
        double closestZ = clamp(eye.z, box.getMin(Direction.Axis.Z), box.getMax(Direction.Axis.Z));

        return new Vec3d(closestX, closestY, closestZ);
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(value, max));
    }
}
