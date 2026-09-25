package games.brennan.streamdetect.api;

import games.brennan.streamdetect.ProcessNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;

/**
 * Public API: is <b>streaming software</b> — OBS Studio, or its Streamlabs derivative — running on
 * this machine?
 *
 * <p>Built for mods that want to nudge a streamer at the right moment (Dungeon Train shows a
 * "share your stream" tab on its Videos button). This mod does nothing on its own; it only answers
 * when asked.</p>
 *
 * <h3>What it looks at, and what it does not</h3>
 * <p>Running process <b>names</b>, from the {@link ProcessNames} snapshot, matched against
 * {@link #STREAMING_PROCESSES}. Nothing else. Whether the software is actually recording or live is
 * not asked — that would need OBS's WebSocket API, a password, and the player's say-so — so
 * "running" is the whole of what is known. The result never leaves this machine; the only trace is
 * one log line.</p>
 *
 * <p>Every platform, since OBS runs on all of them. One-shot per session ({@link #detectNow()}
 * caches), belongs off the render thread, and treats any failure as "not detected".</p>
 */
public final class StreamDetect {

    private static final Logger LOGGER = LoggerFactory.getLogger("StreamDetect");

    /**
     * Process names that mean streaming software is open, lowercase.
     *
     * <p>OBS Studio's executable is {@code obs64.exe} on Windows ({@code obs32.exe} on the old
     * 32-bit build), {@code OBS} inside the macOS bundle, and {@code obs} on Linux. The Streamlabs
     * pair is the same program under a different name. Deliberately short — callers use this to
     * <i>nudge</i> someone, so a name that could plausibly belong to unrelated software (an
     * {@code obs-*} helper, a note-taking app that starts with the same letters) does not belong
     * on it, and matches are whole-name only.</p>
     */
    public static final List<String> STREAMING_PROCESSES = List.of(
            "obs64.exe",               // OBS Studio, Windows
            "obs32.exe",               // OBS Studio, Windows 32-bit
            "obs",                     // OBS Studio, macOS bundle executable / Linux binary
            "streamlabs obs.exe",      // Streamlabs, older name
            "streamlabs desktop.exe"); // Streamlabs, current name

    /**
     * Dev/QA override: forces {@link #detectNow()} to report this process name without looking at
     * anything, so a caller's UI can be seen on a machine that isn't running OBS —
     * {@code -Dstreamdetect.test=obs64.exe}.
     */
    public static final String TEST_OVERRIDE_PROPERTY = "streamdetect.test";

    /** Session cache: the matched name, {@code ""} for "looked, found nothing", null for "not yet". */
    private static String cached = null;

    private StreamDetect() {}

    /**
     * The streaming software's process name if it is running, else {@code null}. Runs the probe on
     * first call and caches it for the session.
     *
     * <p>Blocking on first call (tens of milliseconds); call it off the render thread until
     * {@link #hasResult()} is true, after which it is a cached read.</p>
     */
    public static synchronized String detectNow() {
        if (cached == null) {
            cached = probe();
            LOGGER.info("[StreamDetect] Streaming-software check: {}",
                    cached.isEmpty() ? "none running" : "found " + cached);
        }
        return cached.isEmpty() ? null : cached;
    }

    /** Whether the probe has already run this session, so callers can avoid blocking on it. */
    public static synchronized boolean hasResult() {
        return cached != null;
    }

    /** Non-blocking read: true only once the probe has run <i>and</i> found something. */
    public static boolean isRunningNow() {
        return hasResult() && detectNow() != null;
    }

    /** The probe proper. Never throws; anything unexpected reads as "nothing found". */
    private static String probe() {
        String override = System.getProperty(TEST_OVERRIDE_PROPERTY, "").trim();
        if (!override.isEmpty()) {
            LOGGER.info("[StreamDetect] Streaming-software check: forced by -D{}", TEST_OVERRIDE_PROPERTY);
            return override;
        }
        String match = matchIn(ProcessNames.snapshot());
        return match == null ? "" : match;
    }

    /**
     * Pure form: the first {@link #STREAMING_PROCESSES} entry present in {@code processNames}, or
     * {@code null}. Case-insensitive, and tolerant of a full path in place of a bare name. Null and
     * blank entries are skipped rather than thrown on — this reads live system data, which is never
     * owed to be well-formed.
     */
    public static String matchIn(Collection<String> processNames) {
        if (processNames == null) return null;
        for (String raw : processNames) {
            if (raw == null || raw.isBlank()) continue;
            String name = ProcessNames.baseName(raw);
            for (String candidate : STREAMING_PROCESSES) {
                if (candidate.equals(name)) return candidate;
            }
        }
        return null;
    }
}
