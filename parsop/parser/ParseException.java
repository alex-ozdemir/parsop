package parsop.parser;

public class ParseException extends Exception {

	public ParseException(String format) {
		super(format);
	}

	public ParseException(String format, Throwable e) {
		super(format, e);
	}

	private static final long serialVersionUID = -8003930746967164211L;

}
