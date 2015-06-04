package parsop.grammar.tokens;

public class UnaryOperation extends Operation {

	public UnaryOperation(String symbol, int arity) {
		super(symbol, arity, TokenType.UnaryOperation);
	}

}
