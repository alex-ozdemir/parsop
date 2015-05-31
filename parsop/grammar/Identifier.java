package parsop.grammar;

public class Identifier implements Token {

	String symbol;
	
	public Identifier(String symbol) {
		this.symbol = symbol;
	}
	
	public String toString() {
		return this.symbol;
	}

	@Override
	public int arity() {
		return 0;
	}
}
