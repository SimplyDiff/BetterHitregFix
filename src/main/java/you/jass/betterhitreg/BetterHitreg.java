package you.jass.betterhitreg;

//version 1.21.8-
//import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

//version 1.21.10+
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import you.jass.betterhitreg.settings.Commands;
import you.jass.betterhitreg.ui.UIScreen;
import you.jass.betterhitreg.utility.Render;

import static you.jass.betterhitreg.hitreg.Hitreg.*;

public class BetterHitreg implements ModInitializer {
    public static KeyBinding uiKey;
    public static KeyBinding handKey;
    public static int handSwitchCooldown;

    @Override
    public void onInitialize() {
        client = MinecraftClient.getInstance();
        Commands.initialize();
        Render.updateColors();

        ClientTickEvents.START_CLIENT_TICK.register(client -> tick());

        //1.21.9 doesnt have worldrenderevents so we do it in WorldMixin

        //version 1.21.8-
//        WorldRenderEvents.END.register(context -> {
//            Render.render(context.camera());
//        });

        //version 1.21.10+
        WorldRenderEvents.END_MAIN.register(context -> {
            Render.render(context.gameRenderer().getCamera());
        });

        //version 1.21.8-
//        uiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
//                "Open Menu",
//                InputUtil.Type.KEYSYM,
//                GLFW.GLFW_KEY_H,
//                "Hitreg"
//        ));
//        handKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
//                "Switch Hand",
//                InputUtil.Type.KEYSYM,
//                GLFW.GLFW_KEY_I,
//                "Hitreg"
//        ));

        //version 1.21.9+
        KeyBinding.Category category = KeyBinding.Category.create(Identifier.of("betterhitreg", "hitreg"));
        uiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Open Menu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_H, category
        ));
        handKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Switch Hand",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_I, category
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (uiKey.wasPressed() && client.currentScreen == null) client.setScreen(new UIScreen());

            while (handKey.wasPressed() && handSwitchCooldown == 0 && client.currentScreen == null) {
                client.options.getMainArm().setValue(client.options.getMainArm().getValue().getOpposite());
                client.player.setMainArm(client.options.getMainArm().getValue());
                client.options.sendClientSettings();
                handSwitchCooldown = 5;
            }

            if (handSwitchCooldown > 0) handSwitchCooldown--;
        });
    }
}