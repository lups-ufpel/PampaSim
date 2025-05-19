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

private Event getEvent(String eventName) throws UndeclaredEvent {
    Event event = events.get(eventName);
    if (event == null) {
        throw new UndeclaredEvent(eventName);
    }
    return event;
}
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
eventGroupDecl locals [EventGroup eventGroup]:
    prefixTok=ID TRANSMITTING_KW associatedType=eventDataType
    {
        var prefix = $prefixTok.text;
        Class<?> dataClass;
        if ($associatedType.start.getType() != NOTHING_KW) {
            try {
            dataClass = Class.forName($associatedType.text);
            } catch (ClassNotFoundException cnfe) {
                throw new InvalidEventData($associatedType.text);
            }
        } else {
            dataClass = null;
        }
        $eventGroup = new EventGroup(prefix, new HashSet<>(), dataClass);
        eventGroups.computeIfAbsent(prefix, _k -> $eventGroup);
    }
    eventDeclList;
eventDataType: javaType | NOTHING_KW;
javaType: ID (('.'|'$') ID)*?;
eventDeclList: '{' (eventsList+=eventData ';')+ '}' {
    EventGroup eventGroup = $eventGroupDecl::eventGroup;
    for (var evdata : $eventsList) {
        var evName = eventGroup.name() + "." + evdata.start.getText();
        var realtime = evdata.stop != evdata.start;
        Event ev = new Event(evName, eventGroup, realtime);
        var evicted = events.put(evName, ev);
        if (evicted != null) {
            throw new DuplicateEvent(ev);
        }
        eventGroup.events().add(ev);
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
entityBlock: '{' eventHandler* transmitsBlock? '}';
eventHandler
    locals [ Event event ]
    : 'on' eventName=ID {
        $event = getEvent($eventName.text);
    } 'do' action=eventAction
    {
       Entity ent = $entity::ent;
       var handler = new Handler(ent, $event, $action.chainOp, $action.descText);
       ent.getHandlers().put($event, handler);
    };
eventAction returns [ Handler.ChainOp chainOp, String descText ]
    : desc=eventHandlerDesc res=actionResult ';'
    {
        $chainOp = $res.chainOp;
        $descText = $desc.text;
    }
    ;
actionResult returns [ Handler.ChainOp chainOp ]
    : 'chains' expr=chainExpr
    {
        $chainOp = $expr.chainOp;
    }
    | // Optional
    ;
transmitsBlock
    : TRANSMITS_KW eventIds+=ID (eventIds+=ID)* ';'
    {
        Entity ent = $entity::ent;
        ent.getTransmitList().addAll($eventIds
            .stream().map(tok -> getEvent(tok.getText())).toList()
        );
    }
    ;
parenChainExpr: '(' chainExpr ')';
chainExpr returns [ Handler.ChainOp chainOp ]: NOTHING_KW
    | singleEvent=ID
        { $chainOp = new Handler.SingleChain(events.get($singleEvent.text)); }
    | productList+=ID ('*' productList+=ID)*
        { $chainOp = new Handler.ProductChain($productList
                .stream()
                .map(tok -> events.get(tok))
                .toList());
        }
    | sumList+=parenChainExpr ('+' sumList+=parenChainExpr)*
        { $chainOp = new Handler.SumChain($sumList
            .stream()
            .map(rule -> rule.chainExpr().chainOp)
            .toList());
        }
    ;
eventHandlerDesc: QUOTED;

COMMENT: COMMENT_LEADER ~[\n]* '\n' -> skip;
WS: [\r\n\t ]+ -> skip;
ANY: '_';
QUOTED: '"' .*? '"';
NOTHING_KW: 'nothing';
CHAINS_KW: 'chains';
REALTIME_KW: 'realtime';
TRANSMITS_KW: 'transmits';
TRANSMITTING_KW: 'transmitting';
ID: [A-Za-z_][A-Za-z0-9_.]*;
fragment COMMENT_LEADER: '//';
