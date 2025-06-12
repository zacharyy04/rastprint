package Controller;

import Engine.EngineObserver;
import Engine.EngineEvent;
import org.json.JSONObject;
import shared.NotificationJsonWriter;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class NotificationListener implements EngineObserver {

    private final PrintController controller;

    public NotificationListener(PrintController controller) {
        this.controller = controller;
    }

    @Override
    public void onEvent(EngineEvent event) {
        String jobId = event.getJobId();
        String timestamp = ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        switch (event.getType()) {
            case JOB_STARTED -> {
                controller.handleJobStarted(event);

                JSONObject json = new JSONObject();
                json.put("event", "job_started");
                json.put("job_id", jobId);
                json.put("timestamp", timestamp);

                NotificationJsonWriter.writeNotification(json, "notif_" + jobId + "_started.json");
            }

            case JOB_COMPLETED -> {
                controller.handleJobCompleted(event);

                JSONObject json = new JSONObject();
                json.put("event", "job_completed");
                json.put("job_id", jobId);
                json.put("timestamp", timestamp);

                // Si getInkUsed() est une Map<String, Double>
                JSONObject inkUsed = new JSONObject();
                Map<String, Double> used = event.getInkUsed();
                if (used != null) {
                    for (String color : used.keySet()) {
                        inkUsed.put(color.toLowerCase(), used.getOrDefault(color, 0.0));
                    }
                }
                json.put("ink_used", inkUsed);

                NotificationJsonWriter.writeNotification(json, "notif_" + jobId + "_completed.json");
            }

            case JOB_ERROR -> {
                controller.handleJobError(event);

                JSONObject json = new JSONObject();
                json.put("event", "error");
                json.put("job_id", jobId);
                json.put("timestamp", timestamp);
                json.put("error_type", "job_error");
                json.put("message", event.getMessage());

                NotificationJsonWriter.writeNotification(json, "notif_" + jobId + "_error.json");
            }

            case INK_LOW -> {
                controller.handleInkLow(event);

                JSONObject json = new JSONObject();
                json.put("event", "ink_low");
                json.put("color", event.getColor() != null ? event.getColor().toLowerCase() : "unknown");
                json.put("level", event.getLevel());
                json.put("timestamp", timestamp);

                NotificationJsonWriter.writeNotification(
                        json,
                        "notif_" + jobId + "_inklow_" + event.getColor().toLowerCase() + ".json"
                );
            }

            default -> System.out.println("Unrecognized event type: " + event.getType());
        }
    }
}