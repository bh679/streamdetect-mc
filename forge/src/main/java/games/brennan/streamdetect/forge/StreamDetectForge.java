package games.brennan.streamdetect.forge;

import games.brennan.streamdetect.StreamDetectMod;
import net.minecraftforge.fml.common.Mod;

/** Forge entrypoint. Runs common init; the API is plain static calls. */
@Mod(StreamDetectMod.MOD_ID)
public final class StreamDetectForge {

    public StreamDetectForge() {
        StreamDetectMod.init();
    }
}
