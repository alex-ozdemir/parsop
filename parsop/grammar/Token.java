package parsop.grammar;

public interface Token {
	public int arity();
	public default boolean isOperation() {
		return this.arity() != 0;
	}
	
}
