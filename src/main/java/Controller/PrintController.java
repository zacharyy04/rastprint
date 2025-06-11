package Controller;

import Engine.EngineEvent;
import Engine.JobMonitor;
import Engine.PrintEngine;
import model.PrintParameters;

public class PrintController {
    private final JobQueueManager queueManager;
    private final PrintEngine engine;

    public PrintController() {
        this.queueManager = new JobQueueManager();
        this.engine = null; // inutile ici
    }

    public PrintController(PrintEngine engine, JobMonitor monitor) {
        this.queueManager = new JobQueueManager();
        this.engine = engine;

        // Enregistrer ce contrôleur comme observateur du moteur
        monitor.registerObserver(new NotificationListener(this));
    }

    public void submitJob(PrintParameters params) {
        PrintJob job = new PrintJob(params);
        queueManager.addJob(job);
        System.out.println("Job submitted: " + job.getJobId());
        engine.startJob(job);
    }

    public void submitJob(PrintJob job) {
        queueManager.addJob(job);
        System.out.println("Job submitted: " + job.getJobId());
        engine.startJob(job);
    }

    public void handleJobStarted(EngineEvent event) {
        System.out.println("🟢 Job started: " + event.getJobId());
    }

    public void handleJobCompleted(EngineEvent event) {
        System.out.println("✅ Job completed: " + event.getJobId());
        System.out.println("🖋️  Encre utilisée : " + event.getInkUsed());
    }

    public void handleJobError(EngineEvent event) {
        System.err.println("❌ Error on job " + event.getJobId() + ": " + event.getMessage());
    }

    public void handleInkLow(EngineEvent event) {
        System.out.println("⚠️  Ink low: " + event.getColor() +
                " (" + event.getLevel() + " ml restants)");
    }
}
