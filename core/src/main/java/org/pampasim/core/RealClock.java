package org.pampasim.core;

import lombok.Getter;

public class RealClock {
    @Getter
    private int tick = 0;

    public int next() {
        return tick++;
    }
    @Override
    public String toString() {
        return "real clock is " + getTick();
    }
};
