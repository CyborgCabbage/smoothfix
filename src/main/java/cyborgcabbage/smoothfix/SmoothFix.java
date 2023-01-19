package cyborgcabbage.smoothfix;

import net.fabricmc.api.ModInitializer;


public class SmoothFix implements ModInitializer {
    public static final String MOD_ID = "smoothfix";

    public static String name(String name) {
        return SmoothFix.MOD_ID + "." + name;
    }

    @Override
    public void onInitialize() {
    }
}
