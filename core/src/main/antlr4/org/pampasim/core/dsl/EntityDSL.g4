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
eventDeclList: '{' (eventsList+=eventId ';')+ '}' {
    // FIXME: the associated type is ignored for now
    for (var evtok : $eventsList) {
        var evName = evtok.getText();
        Event ev = new Event(evName, $eventGroupDecl::dataClass);
        var evicted = events.put(evName, ev);
        if (evicted != null) {
            throw new DuplicateEvent(ev);
        }
        eventGroups.get($eventGroupDecl::dataClass).add(ev);
    }
};
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
            throw new UndeclaredEvent($event);
        }
    } mappings;
mappings
    : 'do' handlerMap
    | 'transition'? transitionMap
    ;
handlerMap: '{' eventAction+ '}';
eventAction locals [ ArrayList<Event> chainedEvents = new ArrayList<>() ]
    : from=stateId 'then' desc=eventHandlerDesc to=actionTransition actionResult ';'
    {
        Entity ent = $entity::ent;
        Event event = $eventHandler::event;
        String fromStateName = $from.text;
        AssociatedState fromState = new AssociatedState(ent, fromStateName);
        EventStatePair pair = new EventStatePair(event, fromState);

        // FIXME: nextStates need to be passed along
        var handler = new Handler(ent, pair, null, $chainedEvents, $desc.text);
        ent.getHandlers().put(pair, handler);
    }
    ;
actionTransition
    : TRANSITION_OPERATOR actionTransitionExpr
    | // optional, equivalent to no transition
    ;
actionTransitionExpr
    : stateId         # Immediate
    | '...' actionTransitionExpr # Eventual
    | stateId '|' actionTransitionExpr # Or
    | '(' actionTransitionExpr ')' # Paren
    ;
actionResult
    : 'chains' eventId {
        Event event = events.get($eventId.text);
        if (event == null) {
            throw new UndeclaredEvent(event);
        }
        $eventAction::chainedEvents.add(event);
    }
    | 'nochain'
    | // optional, equivalent to nochain
    ;
transitionMap: '{' transition+ '}';
transition
    : from=statePattern TRANSITION_OPERATOR to=stateId ';'
    {
        Entity ent = $entity::ent;
        Event event = $eventHandler::event;
        String fromStateName = $from.text;
        String toStateName = $to.text;
        AssociatedState fromState = new AssociatedState(ent, fromStateName);
        AssociatedState toState = new AssociatedState(ent, toStateName);
        EventStatePair pair = new EventStatePair(event, fromState);
        var nextStates = new ArrayList<AssociatedState>();
        nextStates.add(toState);
        var handler = new Handler(ent, pair, nextStates, new ArrayList<>(), "simple transition");
        ent.getHandlers().put(pair, handler);
    }
    ;
stateId: ID;
statePattern
    : stateId
    | ANY // Used to match all not previously matched
    ;
eventId: ID;
eventHandlerDesc: QUOTED;

COMMENT: COMMENT_LEADER ~[\n]* '\n' -> skip;
WS: [\r\n\t ]+ -> skip;
ANY: '_';
QUOTED: '"' .*? '"';
TRANSITION_OPERATOR: '->';
NOTHING_KW: 'nothing';
ID: [A-Za-z_][A-Za-z0-9_]*;
fragment COMMENT_LEADER: '//';
