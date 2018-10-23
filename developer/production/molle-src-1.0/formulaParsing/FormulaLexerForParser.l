
package formulaParsing; 

%%

%byaccj
%class FormulaLexerForParser
%scanerror MolleLexicalErrorException

%{
  /* store a reference to the Parser object */
  private FormulaParser yyParser;

  /* constructor taking an additional Parser object */
  public FormulaLexerForParser(java.io.Reader r, FormulaParser yyParser)  {
    this(r);
    this.yyParser = yyParser;
  }
%}

CONSTANT = [a-z]+ ("_" [a-z]+)*
PREDICATE = [A-Z] [a-zA-Z]* ("_" [a-zA-Z]+)*
NEWLINE  = \n | \r | \r\n

%%

/* binary operators */
" "  { }
"&"  { return FormulaParserTokens.AND; }
"|"  { return FormulaParserTokens.OR; }
"=>"  { return FormulaParserTokens.IMPLIES; }
"<=>" { return FormulaParserTokens.IFF; }

/* unary operators */
"[]"  { return FormulaParserTokens.NEC; }
"<>"  { return FormulaParserTokens.POS; }
"~"   { return FormulaParserTokens.NOT; }
"\!E"   { return FormulaParserTokens.EXISTS; }
"\!A"   { return FormulaParserTokens.FORALL; }

/* lexical sugar */
"("   { return FormulaParserTokens.LP; }
")"   { return FormulaParserTokens.RP; }
","   { return FormulaParserTokens.COMMA; }
/* newline */
{NEWLINE}   { return FormulaParserTokens.NEWLINE; }

{CONSTANT}   {
		yyParser.yylval = new ParserVal(yytext());
		return FormulaParserTokens.CONSTANT; 
	}

/* float */
{PREDICATE}   {
		yyParser.yylval = new ParserVal(yytext());
		return FormulaParserTokens.PREDICATE; 
	}

.|\n   { throw new MolleLexicalErrorException("Illegal character <"+
         yytext()+">"); }
