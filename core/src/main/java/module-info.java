module org.pampasim.core {
    requires static lombok;
    requires javafx.base; // we need javafx.beans for the observables
    requires guru.nidi.graphviz;
    requires org.antlr.antlr4.runtime;
    requires org.apache.logging.log4j;
    requires de.saxsys.mvvmfx;

    exports org.pampasim.core.dsl;
    exports org.pampasim.core.dsl.metadata;
    exports org.pampasim.core.dsl.errors;
    exports org.pampasim.core.entity;
    exports org.pampasim.core.events;
    exports org.pampasim.core.utils;
    exports org.pampasim.core;
    exports org.pampasim.core.dialog;
}