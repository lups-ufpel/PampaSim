grammar SpecFile;
options { caseInsensitive=true; }
specFile: confCmds+=configCommand+ cmds+=command* EOF;

configCommand:
    (SCHEDULER_CMD schedulerInfo EOCOMMAND)
    | (PROCESSOR_CMD processorInfo EOCOMMAND)
    | (PROCESSMANAGER_CMD processManagerInfo EOCOMMAND);

// other fields may be supplied here when time comes
schedulerInfo:
    'FCFS'
    | 'SJF'
    | 'Round-Robin'
    | 'Priority-Queue'; // really annoying keyword conflict issue

processorInfo: CORE_KW MIPS_KW mips=NUMBER COUNT_KW count=NUMBER nextInfo=processorInfo?;
processManagerInfo: ;

command: PROC_CMD procInfo EOCOMMAND;
procInfo: // these marks are just for readability (position is enough)
    startMark NUMBER durationMark NUMBER priorityMark NUMBER (clrMark CLR_VAL)?;

startMark: START_KW | 's';
durationMark: DURATION_KW | 'd';
clrMark: CLR_KW | 'c';
priorityMark: PRIORITY_KW | 'p';

COMMENT: COMMENT_LEADER ~[\n]* '\n' -> skip;
WS: [\r\n\t ]+ -> skip;
SCHEDULER_CMD: 'scheduler';
PROCESSOR_CMD: 'processor';
PROCESSMANAGER_CMD: 'procmanager';
CORE_KW: 'core';
MIPS_KW: 'mips';
COUNT_KW: 'count';
PROC_CMD: 'proc';
START_KW: 'start';
DURATION_KW: 'duration';
PRIORITY_KW: 'priority';
CLR_KW: 'clr';
NUMBER: [0-9]+;
CLR_VAL: '#' HEXDIG HEXDIG HEXDIG HEXDIG HEXDIG HEXDIG;
EOCOMMAND: ';';
ID: [a-z_][a-z0-9_]*;
HEXDIG: [0-9a-f];
fragment COMMENT_LEADER: '//';
