package you.jass.betterhitreg.mixin;

import net.minecraft.client.sound.Source;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.EXTEfx;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import you.jass.betterhitreg.hitreg.Hitreg;

@Mixin(Source.class)
public abstract class SourceMixin {
    @Final @Shadow private int pointer;

    @Inject(method = "play", at = @At("TAIL"))
    private void shouldMuffle(CallbackInfo ci) {
        if (!AL.getCapabilities().ALC_EXT_EFX) return;
        if (Hitreg.shouldMuffle == 0) return;
        Hitreg.shouldMuffle--;
        int filter = EXTEfx.alGenFilters();
        EXTEfx.alFilteri(filter, EXTEfx.AL_FILTER_TYPE, EXTEfx.AL_FILTER_LOWPASS);
        EXTEfx.alFilterf(filter, EXTEfx.AL_LOWPASS_GAINHF, 1 - Hitreg.muffleAmount);
        AL10.alSourcei(pointer, EXTEfx.AL_DIRECT_FILTER, filter);
    }
}