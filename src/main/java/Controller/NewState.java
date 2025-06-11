package Controller;

public class NewState implements PrintJobState {
    @Override
    public void handle(PrintJob job) {
        System.out.println("Job " + job.getJobId() + " is now active.");
        job.setState(new ActiveState());
    }

    @Override
    public String getStateName() {
        return "New";
    }
}
