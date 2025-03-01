grammar EntityDSL; // Must match file name
// Lexer rules start with an uppercase letter
// Grammar rules start with a lowercase letter
@members {
int test_member;
}

descriptionFile
    : entitySection EOF
    ;
entitySection
    : entity+
    ;
entity
    : ID entityBlock
    ;
entityBlock
    : '{' eventBlock+ '}'
    ;
eventBlock:
    'on' ID handlerList
    ;
handlerList
    : '{' eventHandler+ '}'
    ;
eventHandler
    : stateId '/' eventHandlerDesc '/' handlerResult ';'
    ;
handlerResult
    : eventId
    | NO_EVENT // may not be part of an event chain
    ;
stateId
    : ID
    ;
eventPattern
    : eventId
    | ANY // Used to broadly match 
    ;
eventId
    : ID
    ;
eventHandlerDesc: QUOTED;
WS: [\r\n\t ]+ -> skip;
NO_EVENT: '!';
ANY: '_';
QUOTED: '"' .*? '"';
ID: [A-Za-z_][A-Za-z0-9_]*;
