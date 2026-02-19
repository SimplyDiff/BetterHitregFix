package you.jass.betterhitreg.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.EntityDamageS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import you.jass.betterhitreg.utility.PacketProcessor;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayNetworkHandler.class)
public class ServerMixin {
    @Inject(method = "onEntityDamage(Lnet/minecraft/network/packet/s2c/play/EntityDamageS2CPacket;)V", at = @At("HEAD"), cancellable = true)
    private void onEntityDamage(EntityDamageS2CPacket packet, CallbackInfo ci) {
        //this will run once on the network thread & once on the main thread, unless a server like minemenclub bundles it
        if (!PacketProcessor.processDamage(packet)) ci.cancel();
    }

    @Inject(method = "onEntityAnimation(Lnet/minecraft/network/packet/s2c/play/EntityAnimationS2CPacket;)V", at = @At("HEAD"), cancellable = true)
    private void onEntityAnimation(EntityAnimationS2CPacket packet, CallbackInfo ci) {
        if (!MinecraftClient.getInstance().isOnThread()) return;
        if (!PacketProcessor.processAnimation(packet)) ci.cancel();
    }

    @Inject(method = "onPlaySound(Lnet/minecraft/network/packet/s2c/play/PlaySoundS2CPacket;)V", at = @At("HEAD"), cancellable = true)
    private void onPlaySound(PlaySoundS2CPacket packet, CallbackInfo ci) {
        if (!MinecraftClient.getInstance().isOnThread()) return;
        if (!PacketProcessor.processSound(packet)) ci.cancel();
    }
}