package Controller;

public class ErrorState implements PrintJobState {
    @Override
    public void handle(PrintJob job) {
        System.err.println("Cannot proceed. Job " + job.getJobId() + " is in error.");
    }

    @Override
    public String getStateName() {
        return "Error";
    }
}
