package parsop.grammar.tokens;

import java.util.ArrayList;
import java.util.Stack;

import parsop.grammar.GrammarException;
import parsop.parser.AST;

public class Operation implements Token {
	public static Operation START = new Operation("START", -1, TokenType.Start);
	public static Operation END = new Operation("END", -2, TokenType.End);

	String symbol;
	int arity;
	TokenType type;
	private int index;
	
	/**
	 * Constructs an operation from its symbol and arity.
	 * @param symbol
	 * @param arity
	 */
	public Operation(String symbol, int arity) {
		this.symbol = symbol;
		this.arity = arity;
		this.type = TokenType.Invalid;
	}
	
	public Operation(String symbol, int arity, TokenType type) {
		this(symbol, arity);
		this.type = type; 
	}

	public static Operation fromString(String encoding) throws GrammarException {
		int arity = Integer.parseInt(encoding.substring(0, 1));
		String symbol = encoding.substring(1, encoding.length());
		switch (arity) {
		case 1:
			return new UnaryOperation(symbol, arity);
		case 2:
			return new BinaryOperation(symbol, arity);
		default:
			throw new GrammarException(String.format("The encoding <%s> has arity %d. Only Unary and Binary Operations supported", encoding, arity));
		}
	}
	
	public String toString() {
		return String.format("%d%s", this.arity, this.symbol);
	}
	
	@Override
	public AST build(Stack<Token> reversePolishStack) {
		ArrayList<AST> operands = new ArrayList<AST>(arity());
		for (int i = 0; i < arity(); i++)
			operands.add(0, reversePolishStack.pop().build(reversePolishStack));
		return new AST(this, operands);
	}	
	
	public int arity() {
		return this.arity;
	}
	
	public String symbol() {
		return symbol;
	}

	@Override
	public TokenType type() {
		return type;
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public Operation cloneWithIndex(int i) {
		Operation o = new Operation(symbol, arity, type);
		o.index = i;
		return o;
	}
	
	@Override
	public int hashCode() {
		return arity ^ symbol.hashCode() ^ type.hashCode(); 
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Operation) {
			Operation op = (Operation) o;
			return op.arity == arity && op.symbol.equals(symbol) && op.type == type;
		}
		else return false;
			
	}

}
