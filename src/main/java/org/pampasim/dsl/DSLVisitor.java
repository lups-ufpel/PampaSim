package org.pampasim.dsl;

import java.util.HashMap;
import java.util.HashSet;
import org.pampasim.dsl.metadata.*;

public class DSLVisitor extends EntityDSLBaseVisitor<String> {
    HashSet<String> eventNames;
    HashMap<String, Entity> entityMeta;
}
