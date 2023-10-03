package simuladorso.Log;

import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class SimulatorFormatter extends Formatter {
    @Override
    public String format(LogRecord record) {
        return String.format("%s - %s::%s::%s => %s", new Date(record.getMillis()), record.getLongThreadID(), record.getSourceClassName(), record.getSourceMethodName(), record.getMessage());
    }
}
