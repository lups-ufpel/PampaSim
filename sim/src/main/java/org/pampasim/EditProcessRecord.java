package org.pampasim;

public record EditProcessRecord(CreateProcessRecord processRecord,
                                boolean removable) {
}
