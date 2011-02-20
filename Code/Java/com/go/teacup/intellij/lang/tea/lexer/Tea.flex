package com.go.teacup.intellij.lang.tea.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import static com.go.teacup.intellij.lang.tea.lexer.TeaTokenTypes.*;

%%

%{
    public _TeaLexer() {
      this((java.io.Reader)null);
    }

    public _TeaLexer(boolean highlightMode) {
      this((java.io.Reader)null);
      isHighlightModeOn = highlightMode;
    }

    boolean isHighlightModeOn = false;
%}

%class _TeaLexer
%implements FlexLexer
%unicode

%function advance
%type IElementType

%eof{  return;
%eof}

DIGIT=[0-9]
HEX_DIGIT=[0-9A-Fa-f]
WHITE_SPACE_CHAR=[\ \n\r\t\f]

IDENTIFIER=[:jletter:] [:jletterdigit:]*
/*PACKAGE=({IDENTIFIER})+("."{IDENTIFIER})*
FQN=({PACKAGE})?{IDENTIFIER}*/

C_STYLE_COMMENT=("/*"{COMMENT_TAIL})|"/*"
COMMENT_TAIL=([^"*"]*("*"+[^"*""/"])?)*("*"+"/")?
END_OF_LINE_COMMENT="/""/"[^\r\n]*

INTEGER_LITERAL={DECIMAL_INTEGER_LITERAL}|{HEX_INTEGER_LITERAL}
DECIMAL_INTEGER_LITERAL=({DIGIT})+
HEX_INTEGER_LITERAL=0[Xx]({HEX_DIGIT})+

FLOAT_LITERAL=({FLOATING_POINT_LITERAL1})
FLOATING_POINT_LITERAL1=({DIGIT})+"."({DIGIT})+({EXPONENT_PART})?
EXPONENT_PART=[Ee]["+""-"]?({DIGIT})*

CRLF= [\ \t \f]* (\n | \r | \r\n)
QUOTED_LITERAL="'"([^\\\'\r\n]|{ESCAPE_SEQUENCE}|\\{CRLF})*("'"|\\)?
DOUBLE_QUOTED_LITERAL=\"([^\\\"\r\n]|{ESCAPE_SEQUENCE}|\\{CRLF})*(\"|\\)?
ESCAPE_SEQUENCE=\\[^\r\n]

PLAIN_BODY=([^<]*("<"+[^<%])?)*


%state SCRIPTLET

%%


<YYINITIAL> {
"<%"                  { yybegin(SCRIPTLET); return LSCRIPT; }
{PLAIN_BODY}            { return PLAIN_TEXT; }
}

<SCRIPTLET> {
{WHITE_SPACE_CHAR}+   { return SCRIPT_WHITE_SPACE; }
"%>"                  { yybegin(YYINITIAL); return RSCRIPT; }
{C_STYLE_COMMENT}     { return C_STYLE_COMMENT; }
{END_OF_LINE_COMMENT} { return END_OF_LINE_COMMENT; }

{INTEGER_LITERAL}     { return NUMERIC_LITERAL; }
{FLOAT_LITERAL}       { return NUMERIC_LITERAL; }

{QUOTED_LITERAL}      {
                        return isHighlightModeOn ?
                          SINGLE_QUOTE_STRING_LITERAL:
                          STRING_LITERAL;
                      }

{DOUBLE_QUOTED_LITERAL}      { return STRING_LITERAL; }

"true"                { return TRUE_KEYWORD; }
"false"               { return FALSE_KEYWORD; }
"null"                { return NULL_KEYWORD; }

"and"                 { return AND_KEYWORD; }
"or"                  { return OR_KEYWORD; }
"as"                  { return AS_KEYWORD; }
"break"               { return BREAK_KEYWORD; }
"call"                { return CALL_KEYWORD; }
"define"              { return DEFINE_KEYWORD; }
"else"                { return ELSE_KEYWORD; }
"foreach"             { return FOREACH_KEYWORD; }
"if"                  { return IF_KEYWORD; }
"import"              { return IMPORT_KEYWORD; }
"in"                  { return IN_KEYWORD; }
"not"                 { return NOT_KEYWORD; }
"reverse"             { return REVERSE_KEYWORD; }
"template"            { return TEMPLATE_KEYWORD; }
"isa"                 { return ISA; }

{IDENTIFIER}          { return IDENTIFIER; }

"=="                  { return EQEQ; }
"!="                  { return NE; }
"<"                   { return LT; }
">"                   { return GT; }
"<="                  { return LE; }
">="                  { return GE; }

"&"                   { return AND; }

"("                   { return LPAR; }
")"                   { return RPAR; }
"{"                   { return LBRACE; }
"}"                   { return RBRACE; }
"["                   { return LBRACKET; }
"]"                   { return RBRACKET; }
";"                   { return SEMICOLON; }
","                   { return COMMA; }
"."                   { return DOT; }

"="                   { return EQ; }
"+"                   { return PLUS; }
"-"                   { return MINUS; }
"*"                   { return MULT; }
"/"                   { return DIV; }
"%"                   { return PERC; }

"##"                  { return HASHHASH; }
"#"                   { return HASH; }
".."                  { return DOTDOT; }
"..."                 { return ELLIPSIS; }

.                     { return BAD_CHARACTER; }
}

.                     { return BAD_CHARACTER; }