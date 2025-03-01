grammar EntityDSL; // Must match file name
// Lexer rules start with an uppercase letter
// Grammar rules start with a lowercase letter
descriptionFile : eventsSection entitySection EOF;
eventsSection: 'events' eventDeclsBlock;
eventDeclsBlock: '{' eventGroupDecl+ '}';
eventGroupDecl: 'transmitting' associatedType=eventDataType eventDeclList[associatedType.text];
eventDataType: javaType | 'nothing';
javaType: ID ('.' ID)*?;
eventDeclList[String associatedTypeName]: '{' (eventId ';')+ '}';
entitySection : entity+;
entity : name=ID entityBlock[name];
entityBlock[String entityName] : '{' eventBlock[entityName]+ '}';
eventBlock[String entityName]: 'on' ID mappings[entityName];
mappings[String entityName]
    : 'do' handlerMap[entityName]
    | 'transition'? transitionMap[entityName]
    ;
handlerMap[String entityName] : '{' eventHandler[entityName]+ '}';
eventHandler[String entityName]
    : from=stateId 'then' desc=eventHandlerDesc to=handlerTransition handlerResult ';'
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
transitionMap[String entityName]: '{' transition[entityName]+ '}';
transition[String entityName]
    : from=statePattern TRANSITION_OPERATOR to=stateId ';'
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
