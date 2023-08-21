package simuladorso.Logger;

import simuladorso.Utils.Observable;
import simuladorso.Utils.Observer;

import java.util.Date;
import java.util.LinkedList;

public class Logger extends Observable {
    private final LinkedList<Log> logs = new LinkedList<>();
    private LogType logLevel;
    public Logger() {
        super();
        this.logLevel = LogType.DEBUG;
    }

    private void addLog(Log log) {
        this.logs.add(log);

        if (log.getLogType().ordinal() >= this.logLevel.ordinal())
            this.notifyObservers();
    }

    public void error(String message) {
        Log log = new Log(LogType.ERROR, new Date(), "Not informed", message);
        this.addLog(log);
    }

    public void error(String message, String cause) {
        Log log = new Log(LogType.ERROR, new Date(), cause, message);
        this.addLog(log);
    }

    public void warning(String message) {
        Log log = new Log(LogType.WARNING, new Date(), "Not informed", message);
        this.addLog(log);
    }

    public void info(String message) {
        Log log = new Log(LogType.INFO, new Date(), "Not informed", message);
        this.addLog(log);
    }

    public void debug(String message) {
        Log log = new Log(LogType.DEBUG, new Date(), "Not informed", message);
        this.addLog(log);
    }

    public void setLogLevel(LogType logLevel) {
        this.logLevel = logLevel;
    }

    public void reset() {
        logs.clear();
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : this.observers) {
            observer.update(String.format("%s : %s => %s", this.logs.getLast().getTime().toString(), this.logs.getLast().getLogType().toString(), this.logs.getLast().getMessage()));
        }
    }

    public LinkedList<Log> getLogs() {
        return logs;
    }
}
