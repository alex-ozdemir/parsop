package parsop.grammar;

import java.util.Stack;

import parsop.parser.AST;

public interface Token {
	
	public default boolean isIdentifier() {
		return false;
	}
	
	public default boolean isOpenGroup() {
		return false;
	}
	
	public default boolean isCloseGroup() {
		return false;
	}
	
	public String symbol();
	
	public AST build(Stack<Token> expressions);
	
	/**
	 * A token must be completely encoded in it's toString() representation. That is, the
	 * Token -> String map must be injective.
	 */
	public String toString();
}
