package parsop.parser;

import java.util.HashSet;
import java.util.Set;

import parsop.grammar.Grammar;
import parsop.grammar.Operation;
import parsop.grammar.Token;
import parsop.util.Pair;

public class SyntaxChecker {
	Grammar grammar;
	Token lastToken;
	Set<Pair<Integer,Integer>> acceptableTokenPairs;
	
	public SyntaxChecker(Grammar grammar) {
		this.grammar = grammar;
		this.refresh();
		this.buildAcceptableTokenPairs();
	}

	public void refresh() {
		this.lastToken = Operation.START;
	}
	
	public void checkNextToken(Token t) throws ParseException {
		if (!acceptableTokenPairs.contains(new Pair<Integer, Integer>(lastToken.arity(), t.arity())))
			throw new ParseException(String.format("Syntax Error: token <%s> followed by <%s>", lastToken, t));
		lastToken = t;
	}
	
	private void buildAcceptableTokenPairs() {
		acceptableTokenPairs = new HashSet<Pair<Integer, Integer>>();
		// Acceptable transitions
		// Start -> Identifier
		acceptableTokenPairs.add(new Pair<Integer, Integer>(Operation.START.arity(), 0));
		// Start -> Unary
		acceptableTokenPairs.add(new Pair<Integer, Integer>(Operation.START.arity(), 1));
		// Identifier -> Binary
		acceptableTokenPairs.add(new Pair<Integer, Integer>(0, 2));
		// Identifier -> End
		acceptableTokenPairs.add(new Pair<Integer, Integer>(0, Operation.END.arity()));
		// Unary -> Unary
		acceptableTokenPairs.add(new Pair<Integer, Integer>(1, 1));
		// Unary -> Identifier
		acceptableTokenPairs.add(new Pair<Integer, Integer>(1, 0));
		// Binary -> Unary
		acceptableTokenPairs.add(new Pair<Integer, Integer>(2, 1));
		// Binary -> Identifier
		acceptableTokenPairs.add(new Pair<Integer, Integer>(2, 0));
	}
	
}
