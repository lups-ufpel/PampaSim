module org.pampasim.events {
    requires org.pampasim.core;
    requires org.pampasim.resources;
    requires lombok;
    exports org.pampasim.events;
    exports org.pampasim.events.Process;
    exports org.pampasim.events.External;
    exports org.pampasim.events.Memory;
}