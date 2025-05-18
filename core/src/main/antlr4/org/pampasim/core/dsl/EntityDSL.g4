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
import java.nio.file.Path;
import java.nio.file.Paths;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import java.io.IOException;
}
@members {
@Getter
private Map<String, Entity> entities = new HashMap<>();
@Getter
private Map<String, EventGroup> eventGroups = new HashMap<>();
@Getter
private HashMap<String, Event> events = new HashMap<>();
}
descriptionFile : eventsSection entitySection EOF;
eventsSection: 'events' ('import' importList+=pathRule+)? eventDeclsBlock {
    for (var importPath : $importList) {
        CharStream stream = null;
        System.out.println("trying to open " + importPath.getText());
        try {
            stream = CharStreams.fromFileName(importPath.getText());
        } catch (IOException e) {
            System.err.println(importPath + " not found!");
        }
        var lexer = new EntityDSLLexer(stream);
        var tokenStream = new CommonTokenStream(lexer);
        var parser = new EntityDSLParser(tokenStream);
        parser.setBuildParseTree(true);
        EntityDSLParser.DescriptionFileContext tree
                = parser.descriptionFile();
        events.putAll(parser.getEvents());
    }
    System.out.println("Events list:\n" + events);
};
pathRule locals [Path path]: (acc+='..' '/' | acc+='.' '/')? acc += ID ('/' ID)* {
    $path = $acc.stream().map(a -> Paths.get(a.getText())).reduce((l,r) -> l.resolve(r)).orElseThrow();
};
eventDeclsBlock: '{' eventGroupDecl+ '}';
eventGroupDecl locals [Class<?> dataClass, String prefix]:
    prefixTok=ID 'transmitting' associatedType=eventDataType
    {
        $prefix = $prefixTok.text;
        if ($associatedType.start.getType() != NOTHING_KW) {
            try {
            $dataClass = Class.forName($associatedType.text);
            } catch (ClassNotFoundException cnfe) {
                throw new InvalidEventData($associatedType.text);
            }
        } else {
            $dataClass = null;
        }
        eventGroups.computeIfAbsent($prefix, _k -> new EventGroup($prefix, new HashSet<>(), $dataClass));
    }
    eventDeclList;
eventDataType: javaType | NOTHING_KW;
javaType: ID (('.'|'$') ID)*?;
eventDeclList: '{' (eventsList+=eventData ';')+ '}' {
    // FIXME: the associated type is ignored for now
    for (var evdata : $eventsList) {
        var evName = $eventGroupDecl::prefix + "." + evdata.start.getText();
        var realtime = evdata.stop != evdata.start;
        Event ev = new Event(evName, $eventGroupDecl::dataClass, realtime);
        var evicted = events.put(evName, ev);
        if (evicted != null) {
            throw new DuplicateEvent(ev);
        }
        eventGroups.get($eventGroupDecl::prefix).events().add(ev);
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
ID: [A-Za-z_][A-Za-z0-9_.]*;
fragment COMMENT_LEADER: '//';
