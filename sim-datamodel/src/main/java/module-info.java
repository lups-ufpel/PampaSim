module org.pampasim.resources {
    requires java.desktop;
    requires org.pampasim.core;
    requires static lombok;
    requires org.antlr.antlr4.runtime;
    requires org.apache.logging.log4j;

    exports org.pampasim.resources;
}