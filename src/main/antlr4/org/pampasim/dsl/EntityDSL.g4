grammar EntityDSL; // Must match file name
// Lexer rules start with an uppercase letter
// Grammar rules start with a lowercase letter
@members {
int test_member;
}

descriptionFile : eventsSection entitySection EOF;
eventsSection: 'events' eventDeclsBlock;
eventDeclsBlock: '{' eventGroupDecl+ '}';
eventGroupDecl: 'transmitting' eventDataType eventDeclList;
eventDataType: javaType | 'nothing';
javaType: ID ('.' ID)*?;
eventDeclList: '{' (eventId ';')+ '}';
entitySection : entity+;
entity : ID entityBlock;
entityBlock : '{' eventBlock+ '}';
eventBlock: 'on' ID mappings;
mappings
    : 'do' handlerMap
    | 'transition' transitionMap
    | transitionMap // specifier optional
    ;
handlerMap : '{' eventHandler+ '}';
eventHandler
    : stateId 'then' eventHandlerDesc handlerTransition handlerResult ';'
    ;
handlerTransition
    : TRANSITION_OPERATOR handlerTransitionADT
    | // optional, equivalent to no transition
    ;
handlerTransitionADT
    : stateId         # Immediate
    | '...' handlerTransitionADT # Eventual
    | stateId '|' handlerTransitionADT # Or
    | '(' handlerTransitionADT ')' # Paren
    ;
handlerResult
    : 'chains' eventId
    | 'nochain'
    | // optional, equivalent to nochain
    ;
transitionMap: '{' transition+ '}';
transition
    : statePattern TRANSITION_OPERATOR stateId ';'
    ;
stateId
    : ID
    ;
statePattern
    : stateId
    | ANY // Used to match all not previously matched
    ;
eventId
    : ID
    ;
eventHandlerDesc: QUOTED;
WS: [\r\n\t ]+ -> skip;
ANY: '_';
QUOTED: '"' .*? '"';
TRANSITION_OPERATOR: '->';
ID: [A-Za-z_][A-Za-z0-9_]*;
