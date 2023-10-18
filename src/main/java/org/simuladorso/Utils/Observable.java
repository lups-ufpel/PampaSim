package org.simuladorso.Utils;


public interface Observable {
    void subscribe(Observer observer);
    void notifyObservers();
}
