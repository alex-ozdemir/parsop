package parsop.grammar;

enum Associativity {
	Left,
	Right;
	
	public static Associativity fromString(String encoding) throws GrammarException {
		encoding = encoding.toLowerCase();
		if (encoding.equals("left")) return Left;
		else if (encoding.equals("right")) return Right;
		else throw new GrammarException("Encoding of associativity not recognized");
	}
	
	public String toString() {
		switch (this) {
		case Left:
			return "Left";
		case Right:
			return "Right";
		default:
			throw new Error("Associativity Enumeration is broken");
		}
	}
}
