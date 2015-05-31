package parsop.grammar;

public class Operation implements Token {
	public static Operation START = new Operation("START", -1);
	public static Operation END = new Operation("END", -2);

	String symbol;
	int arity;
	
	/**
	 * Constructs an operation from its symbol and arity.
	 * @param symbol
	 * @param arity
	 */
	public Operation(String symbol, int arity) {
		this.symbol = symbol;
		this.arity = arity;
	}

	public static Operation fromString(String encoding) {
		int arity = Integer.parseInt(encoding.substring(0, 1));
		String symbol = encoding.substring(1, encoding.length());
		return new Operation(symbol, arity);
	}
	
	public String toString() {
		return String.format("%d%s", this.arity, this.symbol);
	}

	@Override
	public int arity() {
		return this.arity;
	}

}
