package games.brennan.streamdetect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common init. The mod does nothing on its own — it only answers {@link StreamDetect} when another mod
 * asks — so this owns the id and announces the mod at startup.
 */
public final class StreamDetectMod {

    public static final String MOD_ID = "streamdetect";
    public static final Logger LOGGER = LoggerFactory.getLogger("StreamDetect");

    private StreamDetectMod() {}

    public static void init() {
        LOGGER.info("[StreamDetect] initialised — idle until another mod asks");
    }
}
