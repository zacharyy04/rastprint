package Engine;

import java.util.ArrayList;
import java.util.List;

public class JobMonitor implements EngineSubject {

    private final List<EngineObserver> observers;

    public JobMonitor() {
        this.observers = new ArrayList<>();
    }

    @Override
    public void registerObserver(EngineObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(EngineObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(EngineEvent event) {
        for (EngineObserver observer : observers) {
            observer.onEvent(event);
        }
    }
}