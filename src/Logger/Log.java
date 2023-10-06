package Logger;

import java.util.Date;

public class Log {
    private LogType logType;
    private Date time;
    private String cause;
    private String message;
    public Log(LogType logType, Date time, String cause, String message) {
        this.logType = logType;
        this.time = time;
        this.cause = cause;
        this.message = message;
    }

    public LogType getLogType() {
        return logType;
    }

    public Date getTime() {
        return time;
    }

    public String getCause() {
        return cause;
    }

    public String getMessage() {
        return message;
    }
}
