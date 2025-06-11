package Controller;

public class ActiveState implements PrintJobState {
    @Override
    public void handle(PrintJob job) {
        System.out.println("Job " + job.getJobId() + " is printing...");
    }

    @Override
    public String getStateName() {
        return "Active";
    }
}
