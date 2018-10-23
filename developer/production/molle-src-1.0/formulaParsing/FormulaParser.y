%{

import java.lang.Math;
import java.io.*;
import java.util.StringTokenizer;
import formulaTree.*;
%}

/* YACC Declarations */
%token COMMA CONSTANT PREDICATE NEWLINE RP LP 
%left OR
%right IMPLIES
%left IFF 
%left AND
%left NOT POS NEC NNOT NPOS NNEC
%left FORALL EXISTS
/* Grammar follows */
%%

input: fmla { rootFormula = (Formula)$1.obj; }
 ;
/*
line: '\n'
 | fmla '\n' { System.out.println(" " + $1.dval + " "); }
 ;
*/

fmla: PREDICATE arglist { $$ = new ParserVal(new FormulaTermNode(new String($1.sval))); }
 | fmla AND fmla { $$ = new ParserVal(new FormulaAndNode((Formula)$1.obj, (Formula)$3.obj )); }
 | fmla OR fmla { $$ = new ParserVal(new FormulaOrNode((Formula)$1.obj, (Formula)$3.obj )); }
 | fmla IFF fmla { $$ = new ParserVal(new FormulaIffNode((Formula)$1.obj, (Formula)$3.obj )); }
 | NOT fmla %prec NNOT { $$ = new ParserVal(((Formula)$2.obj).negatedClone()); }
 | POS fmla %prec NPOS { $$ = new ParserVal(new FormulaPosNode((Formula)$2.obj)); }
 | NEC fmla %prec NNEC { $$ = new ParserVal(new FormulaNecNode((Formula)$2.obj)); }
 | fmla IMPLIES fmla { $$ = new ParserVal(new FormulaImplicationNode((Formula)$1.obj,(Formula)$3.obj )); }
 | LP fmla RP { $$.obj = $2.obj; }
 | FORALL args fmla { yyerror("Modal FOL not implemented");}
 | EXISTS args fmla { yyerror("Modal FOL not implemented");}
 ;
 
arglist: 
	| LP args RP {yyerror("Modal FOL not implemented"); }
;

args: CONSTANT 
| CONSTANT COMMA args 
;
%%

  /* a reference to the lexer object */
  private FormulaLexerForParser lexer;
  private Formula rootFormula;
  
  /* interface to the lexer */
  private int yylex ()  throws MolleLexicalErrorException {
    int yyl_return = -1;
    try {
      yyl_return = lexer.yylex();
    }
    catch (IOException e) {
      throw new MolleLexicalErrorException("IO error :" + e);
    }
    return yyl_return;
  }


/**
 * Manages a syntactical error.
 * @param s A message describing the parse error. 
 * @throw MolleSyntaxErrorException Always throws a syntax error exception.
 */
void yyerror(String s) throws MolleSyntaxErrorException
{
	throw new MolleSyntaxErrorException(s);
}
	    
/** Constructs a new formula parser. 
 * @param r A reader for the input to parse. 
 * @param debugMe <b>true</b> if the parser must emit debug messages, <b>false</b> otherwise.
 */
public FormulaParser(Reader r, boolean debugMe) {
	rootFormula = null;
	lexer = new FormulaLexerForParser(r, this);
	yydebug=debugMe;
  }
	    
/**
 * Parses the input.
 * @throws MolleSyntaxErrorException Thrown if a parse error is found.
 * @throws MolleLexicalErrorException Thrown if a lexical error is found.
 * @return the parsed formula if the input is parsable.
 */
public Formula parse() throws MolleSyntaxErrorException, MolleLexicalErrorException
{
	this.yyparse();
	return rootFormula;
	
}
