package simuladorso.Utils;

import java.util.ArrayList;

public abstract class Observable {
    protected final ArrayList<Observer> observers = new ArrayList<>();

    public void subscribe(Observer observer) {
        if (observer != null)
            observers.add(observer);
    }

    public abstract void notifyObservers();
}
