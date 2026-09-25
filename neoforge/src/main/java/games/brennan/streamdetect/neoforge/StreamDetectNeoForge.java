package games.brennan.streamdetect.neoforge;

import games.brennan.streamdetect.StreamDetectMod;
import net.neoforged.fml.common.Mod;

/** NeoForge entrypoint. Runs common init; the API is plain static calls. */
@Mod(StreamDetectMod.MOD_ID)
public final class StreamDetectNeoForge {

    public StreamDetectNeoForge() {
        StreamDetectMod.init();
    }
}
