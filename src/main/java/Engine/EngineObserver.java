package engine.observer;

import model.EngineEvent;

public interface EngineObserver {
    void onEvent(EngineEvent event);
}