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
import org.pampasim.dsl.metadata.*;
import org.pampasim.dsl.errors.*;
}
@members {
@Getter
private Map<String, Entity> entities = new HashMap<>();
@Getter
private Set<Event> events = new HashSet<>();
}
descriptionFile : eventsSection entitySection EOF;
eventsSection: 'events' eventDeclsBlock {
    System.out.println("Events list:\n" + events);
};
eventDeclsBlock: '{' eventGroupDecl+ '}';
eventGroupDecl: 'transmitting' associatedType=eventDataType eventDeclList;
eventDataType: javaType | 'nothing';
javaType: ID ('.' ID)*?;
eventDeclList: '{' (eventsList+=eventId ';')+ '}' {
    // FIXME: the associated type is ignored for now
    for (var evtok : $eventsList) {
        var evName = evtok.getText();
        var unique = events.add(new Event(evName));
        if (!unique) {
            System.err.println("duplicate event \"" + evName + "\"");
        }
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
        $event = new Event($eventName.text);
        if (!events.contains($event)) {
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
        Event event = new Event($eventId.text);
        if (!events.contains(event)) {
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
ID: [A-Za-z_][A-Za-z0-9_]*;
fragment COMMENT_LEADER: '//';
