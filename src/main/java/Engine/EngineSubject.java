package Engine;

public interface EngineSubject {
    void registerObserver(EngineObserver observer);
    void removeObserver(EngineObserver observer);
    void notifyObservers(EngineEvent event);
}
