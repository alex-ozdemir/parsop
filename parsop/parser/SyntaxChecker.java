package parsop.parser;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import parsop.grammar.Grammar;
import parsop.grammar.tokens.CloseGroup;
import parsop.grammar.tokens.OpenGroup;
import parsop.grammar.tokens.Operation;
import parsop.grammar.tokens.Token;
import parsop.grammar.tokens.TokenType;
import parsop.util.Pair;

/**
 * This syntax checker is intended to process each and every token in the input
 * stream, in order. It uses a simple, deterministic PDA to do so.
 * 
 * Let the token classes be: S - Start I - Identifier U - Unary Operation B -
 * Binary Operation O - Open Group C - Close Group E - End
 * 
 * We will denote 'B may follow A' as A -> B, and will assume that Y may not
 * follow X by default.
 * 
 * There are two syntax requirements: 1. The Groupers must match properly. (())
 * Ok (()()) Ok (()() Meh (] Meh 2. Adjacent tokens must obey the following: S
 * -> I | U | O U -> I | U | O O -> I | U | O B -> I | U | O I -> B | C | E C ->
 * B | C | E Interestingly enough these rules are necessary and sufficient.
 * 
 * By tracking the last token and maintaining an OpenGroup stack, these rules
 * can be easily checked.
 * 
 * @author aozdemir
 *
 */
public class SyntaxChecker {
	Grammar grammar;
	Token lastToken;
	Set<Pair<TokenType, TokenType>> acceptableTokenPairs;
	Stack<OpenGroup> openGroupers;

	public SyntaxChecker(Grammar grammar) {
		this.grammar = grammar;
		this.refresh();
		this.buildAcceptableTokenPairs();
	}

	public void refresh() {
		this.lastToken = Operation.START;
		this.openGroupers = new Stack<OpenGroup>();
	}

	public void checkNextToken(Token t) throws ParseException {
		checkGroupers(t);
		checkTokenPairs(t);
		checkEndCondition(t);
		lastToken = t;
	}

	/**
	 * Verifies that the grouper stack is empty if the end is reached
	 * 
	 * @throws ParseException
	 *             - if there are unmatched groupers
	 */
	private void checkEndCondition(Token t) throws ParseException {
		if (t.type() == TokenType.End)
			if (!openGroupers.isEmpty())
				throw new ParseException("Unmatched closing groupers!", openGroupers.peek().getIndex());
	}

	/**
	 * Verify adjacent tokens make sense together
	 * 
	 * @throws ParseException
	 *             - If an illegal pair occurs, I.E. + )
	 */
	private void checkTokenPairs(Token t) throws ParseException {
		if (!acceptableTokenPairs.contains(new Pair<TokenType, TokenType>(lastToken.type(), t
				.type())))
			throw new ParseException(String.format("Syntax Error: token <%s> followed by <%s>",
					lastToken.symbol(), t.symbol()), lastToken.getIndex(), t.getIndex());
	}

	/**
	 * Verify that the grouper subsequence makes sense. Also updated grouper
	 * stack to use for future checks
	 * 
	 * @throws ParseException
	 *             - If there is a mismatch, such as ( ( ) ]
	 */
	private void checkGroupers(Token t) throws ParseException {
		if (t.isOpenGroup())
			openGroupers.push((OpenGroup) t);
		if (t.isCloseGroup()) {
			if (openGroupers.isEmpty())
				throw new ParseException(
						String.format("Unmatched closing grouper <%s>", t.symbol()), t.getIndex());
			OpenGroup match = openGroupers.pop();
			if (!match.equals(grammar.openGroup((CloseGroup) t)))
				throw new ParseException(String.format(
						"Syntax Error: Mismatched groupers: <%s> <%s>", match.symbol(),
						t.symbol()), match.getIndex(), t.getIndex());
		}
	}

	/**
	 * Set up pairs of acceptable consecutive tokens
	 */
	private void buildAcceptableTokenPairs() {
		this.acceptableTokenPairs = new HashSet<Pair<TokenType, TokenType>>();
		canGoToBeginExpression(TokenType.Start);
		canGoToBeginExpression(TokenType.BinaryOperation);
		canGoToBeginExpression(TokenType.UnaryOperation);
		canGoToBeginExpression(TokenType.OpenGroup);
		canGoToEndExpression(TokenType.CloseGroup);
		canGoToEndExpression(TokenType.Identifier);
	}

	/**
	 * Call this if t can be followed by a Beginning Expressions (Identifier,
	 * UnaryOperation, or OpenGroup)
	 */
	private void canGoToBeginExpression(TokenType t) {
		acceptableTokenPairs.add(new Pair<TokenType, TokenType>(t, TokenType.Identifier));
		acceptableTokenPairs.add(new Pair<TokenType, TokenType>(t, TokenType.OpenGroup));
		acceptableTokenPairs.add(new Pair<TokenType, TokenType>(t, TokenType.UnaryOperation));
	}

	/**
	 * Call this if t can be followed by an endeing expressions (Binary
	 * Operation,
	 * 
	 */
	private void canGoToEndExpression(TokenType t) {
		acceptableTokenPairs.add(new Pair<TokenType, TokenType>(t, TokenType.End));
		acceptableTokenPairs.add(new Pair<TokenType, TokenType>(t, TokenType.CloseGroup));
		acceptableTokenPairs.add(new Pair<TokenType, TokenType>(t, TokenType.BinaryOperation));
	}

}
