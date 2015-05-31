package parsop.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Stack;

import parsop.grammar.Grammar;
import parsop.grammar.GrammarException;
import parsop.grammar.Operation;
import parsop.grammar.Token;
import parsop.util.ListStream;

/**
 * This is the core of the Operator Parser system.
 * 
 * The central idea behind the algorithm used here (credits:
 * https://www.youtube.com/watch?v=n5UWAaw_byw) is to maintain 2 stacks, one
 * of expressions and one of operations. 
 * 
 * The first of these is the expressionStack, which at any given point holds
 * valid sub-expressions that can be found in the string we are parsing.
 * Expressions that occur earlier in the string are deeper in the stack. This
 * stack is constantly changing as new basic expressions are added and existing
 * expressions are combined. Hopefully the expressionStack holds only a single
 * expression at the end of the parse - the one corresponding to the entire
 * string.
 * 
 * The second stack is the tokenStack. Tokens (Operations or Identifiers) are
 * put on the stack in the order that they occure in the string, according to
 * the following rules:
 *    1. The stack begins with a "START" token.
 *    2. When a new token is read from the string, if it has higher precedence
 *       than the top token on the stack, the new token is placed on the stack.
 *       If not, then the token on the top of the stack is built - it has the
 *       correct number of operands pulled from the expressionStack, it combines
 *       those operands, and the result goes on the expressionStack. The new
 *       token is not removed from the input string in this case
 *    3. Identifiers are treated as having the highest precedence and require no
 *       operands to build. The "START" token has the lowest precedence.
 *    4. In this way, low precedence operators remain on the stack until the 
 *       string to the right of the has been built into a complete expression
 *       (or until a lower precedence operator is reached)
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
	Stack<AST> expressions;
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
		if (expressions.size() > 1)
			throw new ParseException(
					"Not actally sure what is wrong here, but there seem to be too few tokens:\n"
							+ input);
		else
			return expressions.pop();
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
		if (isLeftHigherPrecedence(tokenStack.peek(), next)) {
			popToken();
		} else {
			pushToken();
		}
	}

	private void pushToken() throws ParseException {
		syntaxChecker.checkNextToken(tokenStream.peek());
		tokenStack.push(tokenStream.next());
	}

	private void popToken() {
		Token buildToken = tokenStack.pop();
		ArrayList<AST> operands = new ArrayList<AST>(buildToken.arity());
		for (int i = 0; i < buildToken.arity(); i++)
			operands.add(0, expressions.pop());
		expressions.push(new AST(buildToken, operands));
	}

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
		System.out.print("\nExpressions: ->");
		ListIterator<AST> stackIter = expressions.listIterator(expressions.size());
		while (stackIter.hasPrevious())
			System.out.print("  " + stackIter.previous().toString());
		System.out.print("  \nTokens: ->");
		ListIterator<Token> stackIter2 = tokenStack.listIterator(tokenStack.size());
		while (stackIter2.hasPrevious())
			System.out.print("  " + stackIter2.previous().toString());
		System.out.println("  \nTokensStream: " + tokenStream.toString());
	}

	private void setupParse(String input) {
		expressions = new Stack<AST>();

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
