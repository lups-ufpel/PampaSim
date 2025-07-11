package org.pampasim.resources.dialog;

import java.util.List;

public record MemoryAccessRecord(int index, List<Integer> addresses, boolean modifies) { }
