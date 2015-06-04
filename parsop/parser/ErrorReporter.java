package parsop.parser;

import java.util.List;

public class ErrorReporter {
	Tokenizer tokenizer;
	String input;
	IndexIndicator indicator;
	
	class IndexIndicator {
		boolean[] markedIndices;
		
		IndexIndicator(int length) {
			this.markedIndices = new boolean[length];
			// Defaults to false
		}
		
		void markIndices(List<Integer> indices) {
			for (int i : indices)
				markedIndices[i] = true;
		}
		
		public String toString() {
			StringBuilder builder = new StringBuilder(markedIndices.length);
			for (boolean mark : markedIndices)
				builder.append(mark ? '^' : ' ');
			return builder.toString();
		}
	}
	
	ErrorReporter(Tokenizer tokenizer) {
		this.tokenizer = tokenizer;
	}
	
	void setInput(String input) {
		this.input = input;
		this.indicator = new IndexIndicator(input.length());
	}
	
	void reportError(ParseException e, int[] tokenIndices) {
		for (int i : tokenIndices)
			indicator.markIndices(tokenizer.inputIndices(i));
		System.err.println(e.getMessage());
		System.err.println(input);
		System.err.println(indicator.toString());
	}
}

