package org.pampasim.core;

import javafx.beans.property.SimpleIntegerProperty;

public class RealClock extends SimpleIntegerProperty {
    public int next() {
        var t = this.get()+1;
        this.set(t);
        return t;
    }
    @Override
    public String toString() {
        return "real clock is " + get();
    }
};
