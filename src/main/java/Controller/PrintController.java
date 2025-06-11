package Controller;

import Engine.observer.EngineEvent;

public class PrintController {
    private final JobQueueManager queueManager;

    public PrintController() {
        this.queueManager = new JobQueueManager();
    }

    public void submitJob(PrintParameters params) {
        PrintJob job = new PrintJob(params);
        queueManager.addJob(job);
        System.out.println("Job submitted: " + job.getJobId());
    }

    public void start() {
        while (queueManager.hasJobs()) {
            PrintJob job = queueManager.getNextJob();
            System.out.println("Processing job: " + job.getJobId());
            job.transition();
        }
    }

    public void handleJobStarted(EngineEvent event) {
        System.out.println("Job started: " + event.getJobId());
    }

    public void handleJobCompleted(EngineEvent event) {
        System.out.println("Job completed: " + event.getJobId());
    }

    public void handleJobError(EngineEvent event) {
        System.err.println("Error on job " + event.getJobId() + ": " + event.getMessage());
    }

    public void handleInkLow(EngineEvent event) {
        System.out.println("Attention : niveau faible sur la cartouche " + event.getColor() +
                " (" + event.getLevel() + " ml)");
    }
}