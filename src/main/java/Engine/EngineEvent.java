package Engine;

import java.time.Instant;
import java.util.Map;

public class EngineEvent {

    public enum Type {
        JOB_STARTED,
        JOB_COMPLETED,
        JOB_ERROR,
        INK_LOW
    }

    private final Type type;
    private final String jobId;
    private final String message;       // utilisé pour les erreurs
    private final String color;         // utilisé pour ink_low
    private final double level;         // utilisé pour ink_low
    private final Map<String, Double> inkUsed; // utilisé pour job_completed
    private final Instant timestamp;

    // Constructeurs surchargés pour différents types d’événements

    public EngineEvent(Type type, String jobId) {
        this(type, jobId, null, null, 0.0, null);
    }

    public EngineEvent(Type type, String jobId, String message, String color, double level) {
        this(type, jobId, message, color, level, null);
    }

    public EngineEvent(Type type, String jobId, Map<String, Double> inkUsed) {
        this(type, jobId, null, null, 0.0, inkUsed);
    }

    private EngineEvent(Type type, String jobId, String message, String color, double level, Map<String, Double> inkUsed) {
        this.type = type;
        this.jobId = jobId;
        this.message = message;
        this.color = color;
        this.level = level;
        this.inkUsed = inkUsed;
        this.timestamp = Instant.now();
    }

    // Getters

    public Type getType() {
        return type;
    }

    public String getJobId() {
        return jobId;
    }

    public String getMessage() {
        return message;
    }

    public String getColor() {
        return color;
    }

    public double getLevel() {
        return level;
    }

    public Map<String, Double> getInkUsed() {
        return inkUsed;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
