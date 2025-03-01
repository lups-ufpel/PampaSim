package org.pampasim.dsl;

import java.util.HashMap;

public record CodeGenResult(
    String eventEnumSrc,
    String eventManagerSrc,
    HashMap<String, String> entitiesSrc,
    String graphSrc) {}
