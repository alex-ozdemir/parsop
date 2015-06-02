package parsop.grammar;

import java.util.ArrayList;
import java.util.Stack;

import parsop.parser.AST;

public class Identifier implements Token {

	String symbol;
	
	public Identifier(String symbol) {
		this.symbol = symbol;
	}
	
	public String toString() {
		return this.symbol;
	}

	public int arity() {
		return 0;
	}

	@Override
	public AST build(Stack<Token> reversePolishStack) {
		return new AST(this, new ArrayList<AST>());
	}
	
	@Override
	public boolean isIdentifier() {
		return true;
	}

	@Override
	public String symbol() {
		return symbol;
	}
}
