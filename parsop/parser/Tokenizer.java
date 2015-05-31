package parsop.parser;

import java.util.ArrayList;
import java.util.List;

import parsop.grammar.Grammar;
import parsop.grammar.Identifier;
import parsop.grammar.Token;

public class Tokenizer {
	
	Grammar grammar;
	
	public Tokenizer(Grammar grammar) {
		this.grammar = grammar;
	}
	
	public List<Token> tokenize(String input) {
		String[] splitInput = split(input);
		List<Token> tokens = new ArrayList<Token>();
		for (String symbol : splitInput)
			if (!symbol.equals(""))
				tokens.add(this.toToken(symbol));
		return tokens;
	}
	
	private Token toToken(String symbol) {
		if (this.grammar.isOperation(symbol))
			return this.grammar.getOperation(symbol);
		else
			return new Identifier(symbol);
	}

	private String[] split(String input) {
		input = insertWhitespace(input);
		String[] splitInput = input.split("\\s+");
		return splitInput;
	}

	private String insertWhitespace(String input) {
		for (String s : this.grammar.operationSymbols())
			input = input.replace(s, String.format(" %s ", s));
		return input;
	}

}