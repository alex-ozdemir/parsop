package parsop.grammar;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import parsop.parser.AST;

public class OpenGroup implements Token {
	String symbol;

	public OpenGroup(String symbol) {
		this.symbol = symbol;
	}

	@Override
	public String symbol() {
		return symbol;
	}
	
	@Override
	public boolean isOpenGroup() {
		return true;
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
	
	public boolean equals(Object other) {
		return (other instanceof OpenGroup && other.toString().equals(toString()));
	}

}
