package games.brennan.streamdetect.fabric;

import games.brennan.streamdetect.StreamDetectMod;
import net.fabricmc.api.ModInitializer;

/** Fabric entrypoint. Runs common init; the API is plain static calls. */
public final class StreamDetectFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        StreamDetectMod.init();
    }
}
