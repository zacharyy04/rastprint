package Engine;

import Engine.EngineEvent;

public interface EngineObserver {
    void onEvent(EngineEvent event);
}