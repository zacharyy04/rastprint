package Controller;

import java.util.LinkedList;
import java.util.Queue;

public class JobQueueManager {
    private final Queue<PrintJob> queue = new LinkedList<>();

    public void addJob(PrintJob job) {
        queue.add(job);
    }

    public PrintJob getNextJob() {
        return queue.poll();
    }

    public boolean hasJobs() {
        return !queue.isEmpty();
    }


}
