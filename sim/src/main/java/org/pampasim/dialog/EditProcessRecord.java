package org.pampasim.dialog;

import org.pampasim.resources.dialog.CreateProcessRecord;

public record EditProcessRecord(CreateProcessRecord processRecord,
                                boolean removable) {
}
