package you.jass.betterhitreg.settings;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static you.jass.betterhitreg.utility.MultiVersion.message;

public class Commands {
    public static void initialize() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registry) -> {
            var root = ClientCommandManager.literal("hitreg");

            for (Toggle toggle : Toggle.values()) {
                root = root.then(ClientCommandManager.literal(toggle.key())
                .executes(context -> {
                   toggle.toggle();
                   return 1;
                }));
            }

            root = root.then(ClientCommandManager.literal("set")
                   .then(argument("value", IntegerArgumentType.integer())
                   .executes(context -> set(IntegerArgumentType.getInteger(context, "value"))))
                   .executes(context -> set(0)));

            dispatcher.register(root.executes(context -> guide()));
        });
    }

    public static int guide() {
        message("/hitreg <command> (press " + getUIKey() + " for UI)", "/hitreg " + Toggle.TOGGLE.key());
        message("custom hitreg: " + "§f" + Settings.getHitreg() + "§7ms", "/hitreg set 0");

        for (Toggle toggle : Toggle.values()) {
            if (toggle == Toggle.TOGGLE) {
                message("hitreg toggled: " + onOrOff(toggle.toggled()), "/hitreg " + toggle.key());
                continue;
            }

            message(toggle.label() + ": " + onOrOff(toggle.toggled()), "/hitreg " + toggle.key());
        }

        if (Settings.getBoolean("tutorial")) Settings.set("tutorial", "false");
        return 1;
    }

    public static String getUIKey() {
        return you.jass.betterhitreg.BetterHitreg.uiKey.getBoundKeyTranslationKey()
                .replace("key.keyboard.", "")
                .replace("key.mouse.", "")
                .replace(".", " ")
                .toUpperCase();
    }

    public static int set(int hitreg) {
        if (hitreg < 0) {
            Settings.set("toggled", "false");
            message("custom hitreg §7is now §coff", "/hitreg " + Toggle.TOGGLE.key());
            return 1;
        }

        Settings.set("hitreg", String.valueOf(hitreg));
        message("hitreg §7set to §f" + hitreg + "§7ms", "/hitreg set 0");

        if (!Toggle.TOGGLE.toggled()) Toggle.TOGGLE.toggle();
        return 1;
    }

    public static String onOrOff(boolean setting) {
        return setting ? "§aon§7" : "§coff§7";
    }
}
