package parsop.grammar;

public class GrammarException extends Exception {

	private static final long serialVersionUID = -4757276675002512205L;

	public GrammarException(String arg0) {
		super(arg0);
	}

	public GrammarException(String string, GrammarException e) {
		super(string, e);
	}
}
