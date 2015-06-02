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
		if (this.grammar.isSpecialSymbol(symbol)) {
//			System.err.println(String.format("Token Match: %s -> %s", symbol, this.grammar.getToken(symbol)));
			return this.grammar.getToken(symbol);
		}
		else
			return new Identifier(symbol);
	}

	private String[] split(String input) {
		input = insertWhitespace(input);
		String[] splitInput = input.split("\\s+");
		return splitInput;
	}

	private String insertWhitespace(String input) {
		for (String s : this.grammar.specialSymbols())
			input = input.replace(s, String.format(" %s ", s));
		return input;
	}

}
