package you.jass.betterhitreg.mixin;

//version 1.21.9
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import net.minecraft.client.util.ObjectAllocator;

import net.minecraft.client.render.*;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import you.jass.betterhitreg.utility.Render;

@Mixin(WorldRenderer.class)
public abstract class WorldMixin {
    //version 1.21.9
//    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/FrameGraphBuilder;run(Lnet/minecraft/client/util/ObjectAllocator;Lnet/minecraft/client/render/FrameGraphBuilder$Profiler;)V", shift = At.Shift.AFTER))
//    private void render(ObjectAllocator allocator, RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, Matrix4f positionMatrix, Matrix4f matrix4f, Matrix4f projectionMatrix, GpuBufferSlice fogBuffer, Vector4f fogColor, boolean renderSky, CallbackInfo ci) {
//        Render.render(camera);
//    }
}