package parsop.grammar.tokens;

public class BinaryOperation extends Operation {

	public BinaryOperation(String symbol, int arity) {
		super(symbol, arity, TokenType.BinaryOperation);
	}


}
