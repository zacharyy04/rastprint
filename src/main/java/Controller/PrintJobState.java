package Controller;

public interface PrintJobState {
    void handle(PrintJob job);
    String getStateName();
}
