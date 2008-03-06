package com.go.teacup.intellij.lang.tea;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;

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
"<%"                  { yybegin(SCRIPTLET); return TeaTokenTypes.LSCRIPT; }
{PLAIN_BODY}            { return TeaTokenTypes.PLAIN_TEXT; }
}

<SCRIPTLET> {
{WHITE_SPACE_CHAR}+   { return TeaTokenTypes.SCRIPT_WHITE_SPACE; }
"%>"                  { yybegin(YYINITIAL); return TeaTokenTypes.RSCRIPT; }
{C_STYLE_COMMENT}     { return TeaTokenTypes.C_STYLE_COMMENT; }
{END_OF_LINE_COMMENT} { return TeaTokenTypes.END_OF_LINE_COMMENT; }

{INTEGER_LITERAL}     { return TeaTokenTypes.NUMERIC_LITERAL; }
{FLOAT_LITERAL}       { return TeaTokenTypes.NUMERIC_LITERAL; }

{QUOTED_LITERAL}      {
                        return isHighlightModeOn ?
                          TeaTokenTypes.SINGLE_QUOTE_STRING_LITERAL:
                          TeaTokenTypes.STRING_LITERAL;
                      }

{DOUBLE_QUOTED_LITERAL}      { return TeaTokenTypes.STRING_LITERAL; }

"true"                { return TeaTokenTypes.TRUE_KEYWORD; }
"false"               { return TeaTokenTypes.FALSE_KEYWORD; }
"null"                { return TeaTokenTypes.NULL_KEYWORD; }

"and"                 { return TeaTokenTypes.AND_KEYWORD; }
"or"                  { return TeaTokenTypes.OR_KEYWORD; }
"as"                  { return TeaTokenTypes.AS_KEYWORD; }
"break"               { return TeaTokenTypes.BREAK_KEYWORD; }
"call"                { return TeaTokenTypes.CALL_KEYWORD; }
"define"              { return TeaTokenTypes.DEFINE_KEYWORD; }
"else"                { return TeaTokenTypes.ELSE_KEYWORD; }
"foreach"             { return TeaTokenTypes.FOREACH_KEYWORD; }
"if"                  { return TeaTokenTypes.IF_KEYWORD; }
"import"              { return TeaTokenTypes.IMPORT_KEYWORD; }
"in"                  { return TeaTokenTypes.IN_KEYWORD; }
"not"                 { return TeaTokenTypes.NOT_KEYWORD; }
"reverse"             { return TeaTokenTypes.REVERSE_KEYWORD; }
"template"            { return TeaTokenTypes.TEMPLATE_KEYWORD; }
"isa"                 { return TeaTokenTypes.ISA; }

{IDENTIFIER}          { return TeaTokenTypes.IDENTIFIER; }

"=="                  { return TeaTokenTypes.EQEQ; }
"!="                  { return TeaTokenTypes.NE; }
"<"                   { return TeaTokenTypes.LT; }
">"                   { return TeaTokenTypes.GT; }
"<="                  { return TeaTokenTypes.LE; }
">="                  { return TeaTokenTypes.GE; }

"&"                   { return TeaTokenTypes.AND; }

"("                   { return TeaTokenTypes.LPAR; }
")"                   { return TeaTokenTypes.RPAR; }
"{"                   { return TeaTokenTypes.LBRACE; }
"}"                   { return TeaTokenTypes.RBRACE; }
"["                   { return TeaTokenTypes.LBRACKET; }
"]"                   { return TeaTokenTypes.RBRACKET; }
";"                   { return TeaTokenTypes.SEMICOLON; }
","                   { return TeaTokenTypes.COMMA; }
"."                   { return TeaTokenTypes.DOT; }

"="                   { return TeaTokenTypes.EQ; }
"+"                   { return TeaTokenTypes.PLUS; }
"-"                   { return TeaTokenTypes.MINUS; }
"*"                   { return TeaTokenTypes.MULT; }
"/"                   { return TeaTokenTypes.DIV; }
"%"                   { return TeaTokenTypes.PERC; }

"##"                  { return TeaTokenTypes.HASHHASH; }
"#"                   { return TeaTokenTypes.HASH; }
".."                  { return TeaTokenTypes.DOTDOT; }
"..."                 { return TeaTokenTypes.ELLIPSIS; }

.                     { return TeaTokenTypes.BAD_CHARACTER; }
}

.                     { return TeaTokenTypes.BAD_CHARACTER; }