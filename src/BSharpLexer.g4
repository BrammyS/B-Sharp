/*
* The lexer grammer for the BSharp language.
*/
lexer grammar BSharpLexer;

/*
* Keywords
*/
INT:                    'int';
FLOAT:                  'float';
BOOL:                   'bool';
STRING:                 'string';
CHAR:                   'char';
RETURN:                 'return';
VOID:                   'void';
WHILE:                  'while';
IF:                     'if';
ELSE:                   'else';
VAR:                    'var';

/*
* Literals
*/
DIGIT_LITERAL:          Numbers;
DECIMAL_LITERAL:        Numbers DOT Numbers;
STRING_LITERAL:         ["].*?["];
CHAR_LITERAL:           ['].['];
BOOL_LITERAL:           'true' | 'false';

/*
* Operators
*/
ASSIGNMENT:             '=';
EQUALS:                 '==';
NOT_EQUALS:             '!=';
LE:                     '>';
LT:                     '>=';
GE:                     '<';
GT:                     '<=';
BANG:                   '!';
AND:                    '&&';
OR:                     '||';
ADD:                    '+';
SUB:                    '-';
MUL:                    '*';
DIV:                    '/';
MOD:                    '%';

/*
* Seperators
*/
PAREN_OPEN:             '(';
PAREN_CLOSE:            ')';
BRACE_OPEN:             '{';
BRACE_CLOSE:            '}';
SEMICOLON:              ';';
COMMA:                  ',';
DOT:                    '.';

/*
* Provided methods
*/
WRITE_LINE:             'WriteLine';
READ_CONSOLE_INT:       'ReadConsoleInt';
READ_CONSOLE_LINE:      'ReadConsoleLine';
READ_CONSOLE_FLOAT:     'ReadConsoleFloat';
READ_CONSOLE_BOOL:      'ReadConsoleBool';
RANDOM_INT:             'RandomInt';

/*
* Identifiers
*/
IDENTIFIER:             Letter LettOrNumber*;

/*
* Fragements
*/
fragment Numbers:       '-'? Number Number*;
fragment Number:        [0-9];
fragment Letter:        [a-zA-Z_];
fragment LettOrNumber:  Number | Letter;

/*
* Ignore
*/
COMMENT:                '/*' .*? '*/' -> skip;
SINGLE_COMMENT:         '//' ~[\r\n\t]* -> skip;
WS:                     [ \r\n\t]     -> skip;
