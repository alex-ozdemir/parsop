package parsop.grammar;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;

public class Grammar {

	Map<Operation, Integer> precedenceTable;
	Map<Operation, Associativity> associativityTable;
	Map<String, Operation> symbolTable;
	List<Set<Operation>> precedences;
	List<Associativity> associativities;

	/**
	 * Given a list of operator precedence classes and the associativity of each
	 * class, constructs an Operator Grammar
	 * 
	 * @param precedences
	 *            - Early precedence classes bind more tightly
	 * @param associativities
	 *            - Indices must correspond to that of the precedence list.
	 * @throws GrammarException
	 *             - If the precedences and associativities do not line up.
	 */
	private Grammar(List<Set<Operation>> precedences,
			List<Associativity> associativities) throws GrammarException {
		if (precedences.size() != associativities.size())
			throw new GrammarException(
					"Must be an equal number of precedences and associativities!");
		this.precedences = precedences;
		this.associativities = associativities;
		this.precedenceTable = new HashMap<Operation, Integer>();
		this.associativityTable = new HashMap<Operation, Associativity>();
		this.symbolTable = new HashMap<String, Operation>();
		for (int i = 0; i < precedences.size(); i++) {
			Associativity assoc = associativities.get(i);
			for (Operation o : precedences.get(i)) {
				this.associativityTable.put(o, assoc);
				this.precedenceTable.put(o, i);
				this.symbolTable.put(o.symbol, o);
			}
		}

		this.precedenceTable.put(Operation.START, Integer.MAX_VALUE);
		this.precedenceTable.put(Operation.END, Integer.MAX_VALUE);
	}

	/**
	 * Parses a grammar from a file. Each line of the file defines a precedence
	 * class, with higher precedence classes coming first.
	 * 
	 * Each line should have format [left|right] ([1|2][\w*])* where left|right
	 * indicates associativity of the class, [1|2] indicates the arity of the
	 * operator, followed by the whitespace-free symbol for the operator.
	 * 
	 * Unary operators are assumed to be prefix, and binary operators are
	 * assumed to be infix.
	 * 
	 * @param filepath
	 *            - the location of the file
	 * @return The Grammar made from the file
	 * @throws GrammarException
	 *             if the file is improperly formatted.
	 * @throws IOException
	 *             if the file cannot be read for some reason.
	 */
	public static Grammar fromFile(String filepath) throws GrammarException,
			IOException {
		List<Set<Operation>> precedences = new ArrayList<Set<Operation>>();
		List<Associativity> associativities = new ArrayList<Associativity>();
		BufferedReader fin = new BufferedReader(new FileReader(filepath));
		String line;
		while ((line = fin.readLine()) != null) {
			if (line.charAt(0) == '#')
				continue;
			Set<Operation> precedenceClass = new HashSet<Operation>();
			Scanner in = new Scanner(line);
			try {
				associativities.add(Associativity.fromString(in.next()));
				String operation;
				try {
					while ((operation = in.next()) != null) {
						precedenceClass.add(Operation.fromString(operation));
					}
				} catch (NoSuchElementException e) {
				}
			} catch (GrammarException e) {
				throw new GrammarException("Problem with line" + line + '\n', e);
			} finally {
				in.close();
			}
			precedences.add(precedenceClass);
		}
		fin.close();
		return new Grammar(precedences, associativities);
	}

	public String toString() {
		StringBuffer result = new StringBuffer("Grammar:\n");
		for (int i = 0; i < this.precedences.size(); i++) {
			result.append(this.associativities.get(i));
			for (Operation o : this.precedences.get(i))
				result.append(' ').append(o.toString());
			result.append('\n');
		}
		return result.toString();
	}

	/**
	 * @return Returns the set of operation symbols in this grammar
	 */
	public Set<String> operationSymbols() {
		return this.symbolTable.keySet();
	}

	/**
	 * Determines if the string is an operation symbol in this grammar
	 */
	public boolean isOperation(String s) {
		return this.symbolTable.containsKey(s);
	}

	/**
	 * Gets an Operation object given a symbol
	 */
	public Operation getOperation(String s) {
		return this.symbolTable.get(s);
	}

	/**
	 * Determines which Token has higher precedence.
	 * 
	 * Given that a Token is either an Identifier or Operation, Identifiers
	 * always have higher precedence.
	 * 
	 * Operations have precedences defined in the precedenceTable. If they tie,
	 * the tie is broken by the associativity of their precedence class.
	 * 
	 * The START and END Operations have the lowest precedence.
	 * 
	 * @return Whether the left Token has higher precedence
	 * @throws GrammarException
	 *             if two Identifiers are being compared
	 */
	public boolean leftIsTighter(Token left, Token right)
			throws GrammarException {

		// Identifiers have highest precedence
		if (left.isOperation() != right.isOperation())
			return right.isOperation();
		if (!left.isOperation())
			throw new GrammarException(
					String.format(
							"Adjacent Identifiers: <%s> <%s>. Precedence is not defined between identifiers.",
							left, right));
		
		int leftPrecedence = this.precedenceTable.get(left);
		int rightPrecedence = this.precedenceTable.get(right);
		
		// Handle equal precedence
		if (leftPrecedence == rightPrecedence)
			switch (this.associativityTable.get(left)) {
			case Left:
				return true;
			case Right:
				return false;
			default:
				throw new Error("Associativity Enumeration is broken");
			}
		// Lower precedence is tighter precedence
		else
			return leftPrecedence < rightPrecedence;

	}
}
