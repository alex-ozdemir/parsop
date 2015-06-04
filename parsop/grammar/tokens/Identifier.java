package parsop.grammar.tokens;

import java.util.ArrayList;
import java.util.Stack;

import parsop.parser.AST;

public class Identifier implements Token {

	String symbol;
	private int index;
	
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
	public String symbol() {
		return symbol;
	}

	@Override
	public TokenType type() {
		return TokenType.Identifier;
	}
	
	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public Identifier cloneWithIndex(int i) {
		Identifier o = new Identifier(symbol);
		o.index = i;
		return o;
	}
	
	@Override
	public int hashCode() {
		return symbol.hashCode(); 
	}	
	
	public boolean equals(Object other) {
		return (other instanceof Identifier && other.toString().equals(toString()));
	}
}
