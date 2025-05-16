module org.pampasim.core {
    requires javafx.graphics;
    requires javafx.base;
    requires static lombok;
    requires guru.nidi.graphviz;
    requires org.antlr.antlr4.runtime;
    requires io.github.classgraph;

    exports org.pampasim.core.dsl;
    exports org.pampasim.core.dsl.metadata;
    exports org.pampasim.core.dsl.spec;
    exports org.pampasim.core.dsl.errors;
    exports org.pampasim.core.entity;
    exports org.pampasim.core.entity.Schedulers;
    exports org.pampasim.core.events;
    exports org.pampasim.core.resources;
    exports org.pampasim.core.utils;
    exports org.pampasim.core;
}