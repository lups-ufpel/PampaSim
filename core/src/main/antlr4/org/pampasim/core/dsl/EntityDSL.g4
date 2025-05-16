grammar EntityDSL; // Must match file name
// Lexer rules start with an uppercase letter
// Grammar rules start with a lowercase letter

@header {
import lombok.Getter;
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.HashSet;
import org.pampasim.core.dsl.metadata.*;
import org.pampasim.core.dsl.errors.*;
}
@members {
@Getter
private Map<String, Entity> entities = new HashMap<>();
@Getter
private Map<Class<?>, Set<Event>> eventGroups = new HashMap<>();
@Getter
private HashMap<String, Event> events = new HashMap<>();
}
descriptionFile : eventsSection entitySection EOF;
eventsSection: 'events' eventDeclsBlock {
    System.out.println("Events list:\n" + events);
};
eventDeclsBlock: '{' eventGroupDecl+ '}';
eventGroupDecl locals [Class<?> dataClass]:
    'transmitting' associatedType=eventDataType
    {
        System.out.println($associatedType.start.getType());
        System.out.println(NOTHING_KW);
        if ($associatedType.start.getType() != NOTHING_KW) {
            try {
            $dataClass = Class.forName($associatedType.text);
            } catch (ClassNotFoundException cnfe) {
                throw new InvalidEventData($associatedType.text);
            }
        } else {
            $dataClass = null;
        }
        eventGroups.computeIfAbsent($dataClass, _k -> new HashSet<>());
    }
    eventDeclList;
eventDataType: javaType | NOTHING_KW;
javaType: ID ('.' ID)*?;
eventDeclList: '{' (eventsList+=eventData ';')+ '}' {
    // FIXME: the associated type is ignored for now
    for (var evdata : $eventsList) {
        var evName = evdata.start.getText();
        var realtime = evdata.stop != evdata.start;
        Event ev = new Event(evName, $eventGroupDecl::dataClass, realtime);
        var evicted = events.put(evName, ev);
        if (evicted != null) {
            throw new DuplicateEvent(ev);
        }
        eventGroups.get($eventGroupDecl::dataClass).add(ev);
    }
};
eventData: eventName=ID realtimeOpt=REALTIME_KW?;
entitySection: entity+;
entity locals [Entity ent]: name=ID
    {
        $ent = new Entity();
        $ent.setName($name.text);
        $ent.setHandlers(new HashMap<>());
    }
    entityBlock
    {
        entities.put($ent.getName(), $ent);
    }
    ;
entityBlock: '{' eventHandler+ '}';
eventHandler
    locals [ Event event ]
    : 'on' eventName=ID {
        $event = events.get($eventName.text);
        if ($event == null) {
            throw new UndeclaredEvent($eventName.text);
        }
    } mappings;
mappings
    : 'do' handlerMap
    ;
handlerMap: eventAction;
eventAction locals [ ArrayList<Event> chainedEvents = new ArrayList<>() ]
    : desc=eventHandlerDesc actionResult ';'
    {
        Entity ent = $entity::ent;
        Event event = $eventHandler::event;

        var handler = new Handler(ent, event, $chainedEvents, $desc.text);
        ent.getHandlers().put(event, handler);
    }
    ;
actionResult
    : 'chains' eventId=ID {
        Event event = events.get($eventId.text);
        if (event == null) {
            throw new UndeclaredEvent($eventId.text);
        }
        $eventAction::chainedEvents.add(event);
    }
    | 'nochain'
    | // optional, equivalent to nochain
    ;
eventHandlerDesc: QUOTED;

COMMENT: COMMENT_LEADER ~[\n]* '\n' -> skip;
WS: [\r\n\t ]+ -> skip;
ANY: '_';
QUOTED: '"' .*? '"';
NOTHING_KW: 'nothing';
CHAINS_KW: 'chains';
REALTIME_KW: 'realtime';
ID: [A-Za-z_][A-Za-z0-9_]*;
fragment COMMENT_LEADER: '//';
