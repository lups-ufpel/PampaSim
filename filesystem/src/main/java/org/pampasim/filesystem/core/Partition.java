package org.pampasim.filesystem.core;

public class Partition {
    private int firstBlockIndex;
    private int lastBlockIndex;
    private boolean active;
    //private FileSystem fileSystem;

    public Partition(int firstBlockIndex, int lastBlockIndex, boolean active){
        this.firstBlockIndex = firstBlockIndex;
        this.lastBlockIndex = lastBlockIndex;
        this.active = active;
    }

    public boolean isActive(){
        return active;
    }

    public int getFirstBlockIndex(){
        return firstBlockIndex;
    }


    public int getLastBlockIndex(){
        return lastBlockIndex;
    }

    public int size(){
      return (lastBlockIndex - firstBlockIndex) + 1;
    }

    public void setActive(boolean active){
        this.active = active;
    }
}
