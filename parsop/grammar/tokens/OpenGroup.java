package parsop.grammar.tokens;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import parsop.parser.AST;

public class OpenGroup implements Token {
	String symbol;
	private int index;

	public OpenGroup(String symbol) {
		this.symbol = symbol;
	}

	@Override
	public String symbol() {
		return symbol;
	}

	@Override
	public AST build(Stack<Token> reversePolishStack) {
		List<AST> operands = new ArrayList<AST>();
		operands.add(reversePolishStack.pop().build(reversePolishStack));
		return new AST(this, operands);
	}

	@Override
	public String toString() {
		return symbol;
	}
	@Override
	public TokenType type() {
		return TokenType.OpenGroup;
	}


	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public OpenGroup cloneWithIndex(int i) {
		OpenGroup o = new OpenGroup(symbol);
		o.index = i;
		return o;
	}
	
	@Override
	public int hashCode() {
		return symbol.hashCode(); 
	}	
	
	public boolean equals(Object other) {
		return (other instanceof OpenGroup && other.toString().equals(toString()));
	}

}
