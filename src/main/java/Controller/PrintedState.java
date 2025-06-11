package Controller;

public class PrintedState implements PrintJobState {
    @Override
    public void handle(PrintJob job) {
        System.out.println("Job " + job.getJobId() + " already printed.");
    }

    @Override
    public String getStateName() {
        return "Printed";
    }
}
