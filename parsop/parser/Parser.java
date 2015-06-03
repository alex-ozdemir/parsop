package parsop.parser;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import parsop.grammar.CloseGroup;
import parsop.grammar.Grammar;
import parsop.grammar.GrammarException;
import parsop.grammar.OpenGroup;
import parsop.grammar.Operation;
import parsop.grammar.Token;
import parsop.util.ListStream;

/**
 * This is the core of the Operator Parser system.
 * 
 * The central idea behind the algorithm is to translate the incoming stream to
 * reverse Polish notation, and then build an AST from that (credits to Nika).
 * This involves two stacks.
 * 
 * The first of these is the reversePolishStack, on which the RPS equivalent of
 * the input is built. Identifiers from the input go directly onto this stack.
 * 
 * The second stack is the tokenStack. Operations or Groupers are put on the
 * stack in the order that they occur in the string, according to the following
 * rules:
 *    1. The stack begins with a "START" token.
 *    2. When a new Operation is read from the string, if it has higher
 *       precedence than the top token on the stack, the new token is placed 
 *       on the stack. If not, then the top of the tokenStack is transfered to
 *       the RPS stack.
 *    3. Groupers are given the 2nd lowest precedence (after
 *       the start and end symbols). However, when a grouper is read from the 
 *       input, the rules are different: An open grouper is always put on the
 *       tokenStack and when a close grouper is read, tokens are transfered
 * 		 from the token stack to the RPS stack until the matching open grouper
 *       is found. The open grouper is also put on the RPS stack, and is then
 *       treated as a Unary operator (so the AST reflects which grouper was
 *       used to manipulate precedence). Note that groupers only have precedence
 *       because when an open grouper is on the top of the stack in need be
 *       compared to other operations.
 *    4. START and END have the lowest precedence.
 * 
 * Example: Normal Arithmetic:
 * 
 * RPS       : 
 * TokenStack: START 
 * Input     : 1 + 2 * ( 3 + 4 ) END
 * 
 * RPS       : 1 
 * TokenStack: START 
 * Input     : + 2 * ( 3 + 4 ) END
 * 
 * RPS       : 1 
 * TokenStack: START + 
 * Input     : 2 * ( 3 + 4 ) END
 * 
 * RPS       : 1 2 
 * TokenStack: START + 
 * Input     : * ( 3 + 4 ) END
 * 
 * RPS       : 1 2 
 * TokenStack: START + * 
 * Input     : ( 3 + 4 ) END
 * 
 * RPS       : 1 2 
 * TokenStack: START + * ( 
 * Input     : 3 + 4 ) END
 * 
 * RPS       : 1 2 3 
 * TokenStack: START + * ( 
 * Input     : + 4 ) END
 * 
 * RPS       : 1 2 3 
 * TokenStack: START + * ( + 
 * Input     : 4 ) END
 * 
 * RPS       : 1 2 3 4 
 * TokenStack: START + * ( + 
 * Input     : ) END
 * 
 * RPS       : 1 2 3 4 + ( 
 * TokenStack: START + * 
 * Input     : END
 * 
 * RPS       : 1 2 3 4 + ( * 
 * TokenStack: START + 
 * Input     : END
 * 
 * RPS       : 1 2 3 4 + ( * + 
 * TokenStack: START 
 * Input     : END
 * 
 * RPS       : 1 2 3 4 + ( * + 
 * TokenStack: START END 
 * Input     :
 * 
 * Done!
 * 
 * @author aozdemir
 *
 */
public class Parser {

	// Permanent Members
	Grammar grammar;
	Tokenizer tokenizer;
	boolean verbose;

	// Members refreshed for each parse
	SyntaxChecker syntaxChecker;
	Stack<Token> reversePolishStack;
	Stack<Token> tokenStack;
	ListStream<Token> tokenStream;

	public Parser(String filename, boolean verbose) {
		try {
			grammar = Grammar.fromFile(filename);
			tokenizer = new Tokenizer(grammar);
			syntaxChecker = new SyntaxChecker(grammar);
			this.verbose = verbose;
		} catch (GrammarException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public AST parse(String input) throws ParseException {
		setupParse(input);
		processTokens();
		if (verbose)
			dumpState();
		return processReversePolish();
	}

	private AST processReversePolish() {
		Token top = reversePolishStack.pop();
		return top.build(reversePolishStack);
		// TODO: extra tokens on stack? Error?
	}

	private void processTokens() throws ParseException {
		while (tokenStream.hasNext()) {
			if (verbose)
				dumpState();
			processToken();
		}
	}

	private void processToken() throws ParseException {
		Token next = tokenStream.peek();
		if (next.isIdentifier())
			reversePolishStack.push(takeToken());
		else if (next.isOpenGroup())
			tokenStack.push(takeToken());
		else if (next.isCloseGroup())
			transferTokensUntilOpenGroup();
		else if (isLeftHigherPrecedence(tokenStack.peek(), next))
			transferToken();
		else
			tokenStack.push(takeToken());
	}

	/**
	 * Only call when a CloseGroup is next in the input. Transfers all tokens
	 * from the tokenStack until the matching OpenGroup is found.
	 */
	private void transferTokensUntilOpenGroup() throws ParseException {
		CloseGroup close = (CloseGroup) takeToken();
		OpenGroup expectedOpen = grammar.openGroup(close);
		while (!tokenStack.peek().isOpenGroup())
			transferToken();
		if (!expectedOpen.equals(tokenStack.peek()))
			throw new ParseException(String.format(
					"Found mismatched groupers: %s %s", tokenStack.peek(),
					close));
		transferToken();
	}

	/**
	 * Takes the next token from the input stream
	 * @throws ParseException - If there is a syntax error in the input
	 */
	private Token takeToken() throws ParseException {
		Token next = tokenStream.next();
		syntaxChecker.checkNextToken(next);
		return next;
	}

	/**
	 * Pulls top token off the tokenStack and puts it on the Reverse Polish
	 * Notation stack
	 */
	private void transferToken() {
		reversePolishStack.push(tokenStack.pop());
	}

	/**
	 * True if the left has higher precedence than the right
	 * 
	 * @throws ParseException
	 *             - If they are both identifiers
	 */
	private boolean isLeftHigherPrecedence(Token left, Token right)
			throws ParseException {
		if (left == Operation.START && right == Operation.END)
			return false;
		try {
			return grammar.leftIsTighter(left, right);
		} catch (GrammarException e) {
			throw new ParseException(
					String.format(
							"Adjacent identifiers: <%s> and <%s>. Expected an operation between them",
							left, right));
		}
	}

	private void dumpState() {
		System.out.print("\nPolish  Stack: ");
		for (Token t : reversePolishStack)
			System.out.print("  " + t.toString());
		System.out.print("\n       Tokens: ");
		for (Token t : tokenStack)
			System.out.print("  " + t.toString());
		System.out.println("\nTokens Stream: " + tokenStream.toString());
	}

	private void setupParse(String input) {
		reversePolishStack = new Stack<Token>();

		tokenStack = new Stack<Token>();
		tokenStack.push(Operation.START);

		List<Token> tokens = tokenizer.tokenize(input);
		tokens.add(Operation.END);
		tokenStream = new ListStream<Token>(tokens);

		syntaxChecker.refresh();
	}

	public static Parser fromCommandLineArguments(String[] args) {
		String file = null;
		boolean debug = false;
		for (String arg : Arrays.asList(args)) {
			if (arg.equals("-d"))
				debug = true;
			else {
				file = arg;
				break;
			}
		}

		if (file == null) {
			System.err.println("Usage: [-d] path_to_grammar_spec");
			System.exit(2);
		}
		Parser parser = new Parser(file, debug);
		return parser;
	}

}
