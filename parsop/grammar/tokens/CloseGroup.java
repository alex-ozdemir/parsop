package parsop.grammar.tokens;

import java.util.Stack;

import parsop.parser.AST;

public class CloseGroup implements Token {

	String symbol;
	private int index;

	public CloseGroup(String symbol) {
		this.symbol = symbol;
	}

	@Override
	public String symbol() {
		return symbol;
	}

	@Override
	public AST build(Stack<Token> reversePolishStack) {
		throw new Error("Close group should never be built");
	}
	
	@Override
	public String toString() {
		return symbol;
	}

	@Override
	public TokenType type() {
		return TokenType.CloseGroup;
	}
	
	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public CloseGroup cloneWithIndex(int i) {
		CloseGroup o = new CloseGroup(symbol);
		o.index = i;
		return o;
	}
	
	@Override
	public int hashCode() {
		return symbol.hashCode(); 
	}	
	
	public boolean equals(Object other) {
		return (other instanceof CloseGroup && other.toString().equals(toString()));
	}

}
