package parsop.parser;

import java.util.ArrayList;
import java.util.List;

import parsop.grammar.Grammar;
import parsop.grammar.tokens.Identifier;
import parsop.grammar.tokens.Token;

public class Tokenizer {
	
	private Grammar grammar;
	private List<List<Integer>> tokenIndexToInputIndex;
	
	public Tokenizer(Grammar grammar) {
		this.grammar = grammar;
	}
	
	public void refresh(String input) {
		tokenIndexToInputIndex = new ArrayList<List<Integer>>(input.length());
	}
	
	public List<Integer> inputIndices(int tokenIndex) {
		List<Integer> list = tokenIndexToInputIndex.get(tokenIndex);
		return list;
	}
	
	public List<Token> tokenize(String input) {
		refresh(input);
		List<Token> tokens = toTokens(input);
		mapTokensToInput(input, tokens);
		return tokens;
	}

	private List<Token> toTokens(String input) {
		String[] splitInput = split(input);
		List<Token> tokens = new ArrayList<Token>();
		int i = 0;
		for (String symbol : splitInput) {
			if (!symbol.equals("")) {
				tokens.add(this.toToken(symbol).cloneWithIndex(i));
				i++;
			}
		}
		return tokens;
	}
	
	private Token toToken(String symbol) {
		if (this.grammar.isSpecialSymbol(symbol)) {
			return this.grammar.getToken(symbol);
		}
		else
			return new Identifier(symbol);
	}

	private void mapTokensToInput(String input, List<Token> tokens) throws Error {
		int lastTokenStart = -1;
		for (int i = 0; i < tokens.size(); i++) {
			Token t = tokens.get(i);
			lastTokenStart = input.indexOf(t.symbol(), lastTokenStart + 1);
			if (lastTokenStart < 0)
				throw new Error("Problem with tokenizer token -> ouput mapping");
			List<Integer> inputIndices = new ArrayList<Integer>(t.symbol().length());
			for (int j = lastTokenStart; j < lastTokenStart + t.symbol().length(); j++)
				inputIndices.add(j);
			lastTokenStart += t.symbol().length() - 1;
			tokenIndexToInputIndex.add(inputIndices);
		}
	}

	private String[] split(String input) {
		String[] splitInput = insertWhitespace(input).split("\\s+");
		return splitInput;
	}

	private String insertWhitespace(String input) {
		String output = input;
		for (String s : this.grammar.specialSymbols())
			output = output.replace(s, String.format(" %s ", s));
		return output;
	}
	
	

}
