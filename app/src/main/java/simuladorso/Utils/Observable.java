package simuladorso.Utils;

import java.util.ArrayList;

public interface Observable {
    void subscribe(Observer observer);
    void notifyObservers();
}
