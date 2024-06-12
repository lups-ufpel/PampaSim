package org.pampasim.Utils;

public class SimulatorClock {

    Integer currentTick;
    Integer finalTick;
    final int START_TIME = 0;

    public SimulatorClock(){
        currentTick = START_TIME;
    }

    public void resetClock(){
        currentTick = START_TIME;
    }
    public void setFinalTick(){
         finalTick = currentTick;
    }
    public Integer TotalTicks(){
        return finalTick;
    }
    public Integer getTick(){
        return currentTick;
    }
    public void incrTick(){
        currentTick++;
    }

}
