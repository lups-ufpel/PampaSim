package org.pampasim.dsl.metadata;

public record Event (
        String name
){
    @Override
    public String toString() {
        return "Event " + name();
    }
}
