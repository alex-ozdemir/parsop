package parsop.parser;

public class ParseException extends Exception {

	int[] indices;
	
	public ParseException(String format, int... indices) {
		super(format);
		this.indices = indices;
	}

	public ParseException(String format, Throwable e, int... indices) {
		super(format, e);
		this.indices = indices;
	}
	
	

	private static final long serialVersionUID = -8003930746967164211L;

}
