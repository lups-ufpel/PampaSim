package org.pampasim.dsl.metadata;

import java.util.ArrayList;
import java.util.HashSet;

public class CodeGenData implements Mergeable<CodeGenData> {
    /* FIXME: all events are transmitting Processes at the moment,
        we're just ignoring the transmitting keyword */
    public HashSet<String> eventNames;
    public ArrayList<Entity> entities;

    public CodeGenData() {
        this.eventNames = new HashSet<>();
        this.entities = new ArrayList<>();
    }
    public CodeGenData(HashSet<String> eventNames, ArrayList<Entity> entities) {
        this.eventNames = eventNames;
        this.entities = entities;
    }

    public CodeGenData merge(CodeGenData other) {
        this.eventNames.addAll(other.eventNames);
        for (int i = 0; i < entities.size(); i++) {
            entities.get(i).merge(other.entities.get(i));
        }
        return this;
    }
}
