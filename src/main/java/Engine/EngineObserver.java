package Engine;

import model.EngineEvent;

public interface EngineObserver {
    void onEvent(EngineEvent event);

    void onEvent(Engine.EngineEvent event);
}