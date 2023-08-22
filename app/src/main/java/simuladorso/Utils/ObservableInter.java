package simuladorso.Utils;

public interface ObservableInter {
    public void subscribe(Observer observer);
    public void notifyObservers();
}
