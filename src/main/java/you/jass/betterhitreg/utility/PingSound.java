package you.jass.betterhitreg.utility;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import you.jass.betterhitreg.settings.Settings;

public class PingSound {

    private static final String[] NAMES = {"Orb", "Pling", "Bell", "Harp", "Chime"};

    public static String getName(int index) {
        int i = Math.max(1, Math.min(5, index)) - 1;
        return NAMES[i];
    }

    public static void play() {
        playWith("ping_sound", "ping_volume");
    }

    public static void playJumpReset() {
        playWith("jr_ping_sound", "jr_ping_volume");
    }

    public static void playWith(String soundKey, String volumeKey) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.getSoundManager() == null) return;

        int soundIndex = Settings.getInt(soundKey);
        float volume = Settings.getInt(volumeKey) / 100f;

        if (soundIndex == 2)      client.getSoundManager().play(PositionedSoundInstance.ui(SoundEvents.BLOCK_NOTE_BLOCK_PLING, volume));
        else if (soundIndex == 3) client.getSoundManager().play(PositionedSoundInstance.ui(SoundEvents.BLOCK_NOTE_BLOCK_BELL, volume));
        else if (soundIndex == 4) client.getSoundManager().play(PositionedSoundInstance.ui(SoundEvents.BLOCK_NOTE_BLOCK_HARP, volume));
        else if (soundIndex == 5) client.getSoundManager().play(PositionedSoundInstance.ui(SoundEvents.BLOCK_NOTE_BLOCK_CHIME, volume));
        else                      client.getSoundManager().play(PositionedSoundInstance.ui(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, volume));
    }
}

