package parsop.grammar;

import java.util.Stack;

import parsop.parser.AST;

public class CloseGroup implements Token {

	String symbol;

	public CloseGroup(String symbol) {
		this.symbol = symbol;
	}

	@Override
	public String symbol() {
		return symbol;
	}
	
	@Override
	public boolean isCloseGroup() {
		return true;
	}

	@Override
	public AST build(Stack<Token> reversePolishStack) {
		throw new Error("Close group should never be built");
	}
	
	@Override
	public String toString() {
		return symbol;
	}
}
