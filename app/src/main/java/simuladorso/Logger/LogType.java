package simuladorso.Logger;

public enum LogType {
    ERROR("ERROR"),
    WARNING("WARNING"),
    INFO("INFO"),
    DEBUG("DEBUG");

    private final String label;

    LogType(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
