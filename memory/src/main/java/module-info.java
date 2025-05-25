module org.pampasim.memory {
    requires slf4j.api;
    requires java.desktop;
    requires org.pampasim.core;
    requires org.pampasim.resources;
    requires org.pampasim.events;
    requires guru.nidi.graphviz;
    requires static lombok;
    requires org.antlr.antlr4.runtime;
    requires org.apache.logging.log4j;

    exports org.pampasim.memory;
    exports org.pampasim.memory.entity;
    exports org.pampasim.memory.entity.algorithms;
}