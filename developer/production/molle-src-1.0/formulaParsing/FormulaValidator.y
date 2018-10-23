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

input: fmla
 ;
fmla: PREDICATE arglist
 | fmla AND fmla 
 | fmla OR fmla 
 | fmla IFF fmla 
 | NOT fmla %prec NNOT 
 | POS fmla %prec NPOS 
 | NEC fmla %prec NNEC 
 | fmla IMPLIES fmla 
 | LP fmla RP 
 | FORALL args fmla { yyerror("Modal FOL not implemented"); }
 | EXISTS args fmla { yyerror("Modal FOL not implemented"); }
 ;
 
arglist: 
	| LP args RP { yyerror("Modal FOL not implemented"); } 
;

args: CONSTANT 
| CONSTANT COMMA args
;
%%

  /* a reference to the lexer object */
  private FormulaLexerForValidator lexer;
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
 * @throws MolleSyntaxErrorException Always throws a syntax error exception.
 */
void yyerror(String s) throws MolleSyntaxErrorException
{
	throw new MolleSyntaxErrorException(s);
}
	    
/** Constructs a new formula validator. 
 * A validator is a simple checker for lexical syntactical correctness. 
 */
public FormulaValidator() {
	rootFormula = null;
	lexer = null;
  }

/**
 * Checks if a given input is lexically and syntactically correct.
 * @param r A Reader for the input.
 * @return <b>true</b> is the input is correct, <b>false</b> otherwise.
 */
public boolean check(Reader r) 
{
	if (lexer == null)
		lexer = new FormulaLexerForValidator(r, this);
	else	
		lexer.yyreset(r);
		
	try {
		
		this.yyparse();
	}
	catch (Exception e)
	{
		return false;
	}
	
	return true;
}
