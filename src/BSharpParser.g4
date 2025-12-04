/*
* The parser grammer for the BSharp language.
*/
parser grammar BSharpParser;

options
{
    tokenVocab = BSharpLexer;
}

/*
* The start of the tree.
*/
program
    : methodDeclaration* EOF
    ;

/*
* Methods
*/
methodDeclaration
    : (primitiveTypes | VOID) IDENTIFIER parametersDeclaration codeBlock
    ;

parametersDeclaration
    : PAREN_OPEN parameterDeclaration? (COMMA parameterDeclaration)* PAREN_CLOSE
    ;

parameterDeclaration
    : primitiveTypes IDENTIFIER
    ;

/*
* Build in methods
*/
buildInMethodCalls
    : writeLineMethod # writeLine
    | readConsoleInt # readInt
    | readConsoleLine # readConsole
    | readConsoleFloat # readFloat
    | readConsoleBool # readBool
    | randomInt # randomIntGenerator
    ;

writeLineMethod
    : WRITE_LINE PAREN_OPEN expression PAREN_CLOSE
    ;

readConsoleInt
    : READ_CONSOLE_INT PAREN_OPEN PAREN_CLOSE
    ;

readConsoleLine
    : READ_CONSOLE_LINE PAREN_OPEN PAREN_CLOSE
    ;

readConsoleFloat
    : READ_CONSOLE_FLOAT PAREN_OPEN PAREN_CLOSE
    ;

readConsoleBool
    : READ_CONSOLE_BOOL PAREN_OPEN PAREN_CLOSE
    ;

randomInt
    : RANDOM_INT PAREN_OPEN expression? PAREN_CLOSE
    ;

/*
* Statements
*/
statement
    : codeBlock # blockStatement
    | expression SEMICOLON # expressionStatement
    | IF parameterExpression statement (ELSE statement)? # ifStatement
    | WHILE parameterExpression statement # whileStatement
    | RETURN expressions? SEMICOLON # returnStatement
    ;

codeBlock
    : BRACE_OPEN codeBlockStatement* BRACE_CLOSE
    ;

codeBlockStatement
    : varDeclaration SEMICOLON
    | statement
    ;

varDeclaration
    : (primitiveTypes | VAR) varDeclarator (COMMA varDeclarator)*
    ;

varDeclarator
    : IDENTIFIER (ASSIGNMENT expression)?
    ;

/*
* Expression
*/
expression
    : literal # primaryExpression
    | IDENTIFIER # primaryExpression
    | methodCall # primaryExpression
    | parameterExpression # primaryExpression
    | left=expression op=(MOD | MUL | DIV) right=expression # calculationExpression
    | left=expression op=(ADD | SUB) right=expression # calculationExpression
    | left=expression op=(LE | LT | GE |GT) right=expression # compareExpression
    | left=expression op=(EQUALS | NOT_EQUALS) right=expression # compareExpression
    | left=expression op=AND right=expression # andOrExpression
    | left=expression op=OR right=expression # andOrExpression
    | <assoc=right> left=expression op=ASSIGNMENT right=expression # assignExpression
    | BANG expression # bangExpression
    ;

parameterExpression
    : PAREN_OPEN expression PAREN_CLOSE
    ;

expressions
    : expression (COMMA expression)*
    ;

methodCall
    : IDENTIFIER PAREN_OPEN expressions? PAREN_CLOSE
    | buildInMethodCalls
    ;

/*
* Primitives
*/
primitiveTypes
    : INT # intPrimitive
    | FLOAT # floatPrimitive
    | STRING # stringPrimitive
    | BOOL # boolPrimitive
    | CHAR # charPrimitive
    ;

/*
* Literals
*/
literal
    : content=STRING_LITERAL
    | content=DIGIT_LITERAL
    | content=DECIMAL_LITERAL
    | content=BOOL_LITERAL
    | content=CHAR_LITERAL
    ;
