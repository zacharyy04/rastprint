package Controller;

import Engine.EngineObserver;
import Engine.EngineEvent;

public abstract class NotificationListener implements EngineObserver {

    private final PrintController controller;

    public NotificationListener(PrintController controller) {
        this.controller = controller;
    }

    @Override
    public void onEvent(EngineEvent event) {
        switch (event.getType()) {
            case JOB_STARTED -> controller.handleJobStarted(event);
            case JOB_COMPLETED -> controller.handleJobCompleted(event);
            case JOB_ERROR -> controller.handleJobError(event);
            case INK_LOW -> controller.handleInkLow(event);
            default -> System.out.println("Unrecognized event type: " + event.getType());
        }
    }
}
