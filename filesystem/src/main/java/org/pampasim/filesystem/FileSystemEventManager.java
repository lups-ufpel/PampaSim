package org.pampasim.filesystem;

import org.pampasim.core.EventManager;
import org.pampasim.core.Simulation;
import org.pampasim.core.events.Event;
import org.pampasim.events.*;

public class FileSystemEventManager extends EventManager {
    public FileSystemEventManager(Simulation s) {
        super(s);
    }

    @Override
    public void setupHandlers() {}

    @Override
    public void setupTranslations() {}

    @Override
    public void setupFlags() {}

    @Override
    public void handleEvent(Event event) {
        super.handleEvent(event);
    }
}
