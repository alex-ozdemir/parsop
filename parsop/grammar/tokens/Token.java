package parsop.grammar.tokens;

import java.util.Stack;

import parsop.parser.AST;

public interface Token {
	
	public default boolean isIdentifier() {
		return type() == TokenType.Identifier;
	}
	
	public default boolean isOpenGroup() {
		return type() == TokenType.OpenGroup;
	}
	
	public default boolean isCloseGroup() {
		return type() == TokenType.CloseGroup;
	}
	
	/**
	 * The symbol of a token is the input that maps to it in the parsed string.
	 */
	public String symbol();
	
	public TokenType type();
	
	public AST build(Stack<Token> expressions);
	
	/**
	 * A token must be completely encoded in it's toString() representation. That is, the
	 * Token -> String map must be injective.
	 */
	public String toString();
	
	public int getIndex();
	
	public Token cloneWithIndex(int i);
	
	public int hashCode();
	
	public boolean equals(Object other);
}
