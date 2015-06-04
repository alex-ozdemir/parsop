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

import parsop.grammar.tokens.CloseGroup;
import parsop.grammar.tokens.OpenGroup;
import parsop.grammar.tokens.Operation;
import parsop.grammar.tokens.Token;
import parsop.util.Pair;

public class Grammar {

	private static int GROUP_PRECEDENCE = Integer.MAX_VALUE - 1;

	List<Set<Operation>> precedences;
	List<Associativity> associativities;
	List<Pair<OpenGroup, CloseGroup>> groupers;

	Map<Token, Integer> precedenceTable;
	Map<Operation, Associativity> associativityTable;

	Map<String, Operation> symbolTable;
	Set<String> specialSymbols;

	Map<String, OpenGroup> openGroupTable;
	Map<String, CloseGroup> closeGroupTable;
	HashMap<CloseGroup, OpenGroup> closeToOpenTable;

	/**
	 * Given a list of operator precedence classes and the associativity of each
	 * class, constructs an Operator Grammar
	 * 
	 * @param precedences
	 *            - Early precedence classes bind more tightly
	 * @param associativities
	 *            - Indices must correspond to that of the precedence list.
	 * @param groupers
	 *            - Pairs of grouping symbols
	 * @throws GrammarException
	 *             - If the precedences and associativities do not line up.
	 */
	private Grammar(List<Set<Operation>> precedences, List<Associativity> associativities,
			List<Pair<OpenGroup, CloseGroup>> groupers) throws GrammarException {
		if (precedences.size() != associativities.size())
			throw new GrammarException(
					"Must be an equal number of precedences and associativities!");
		this.precedences = precedences;
		this.associativities = associativities;
		this.groupers = groupers;
		this.precedenceTable = new HashMap<Token, Integer>();
		this.associativityTable = new HashMap<Operation, Associativity>();
		this.symbolTable = new HashMap<String, Operation>();
		this.openGroupTable = new HashMap<String, OpenGroup>();
		this.closeGroupTable = new HashMap<String, CloseGroup>();
		this.closeToOpenTable = new HashMap<CloseGroup, OpenGroup>();
		this.specialSymbols = new HashSet<String>();
		for (int i = 0; i < precedences.size(); i++) {
			Associativity assoc = associativities.get(i);
			for (Operation o : precedences.get(i)) {
				this.associativityTable.put(o, assoc);
				this.precedenceTable.put(o, i);
				this.symbolTable.put(o.symbol(), o);
			}
		}

		for (Pair<OpenGroup, CloseGroup> p : groupers) {
			this.openGroupTable.put(p.first.symbol(), p.first);
			this.precedenceTable.put(p.first, GROUP_PRECEDENCE);
			this.closeGroupTable.put(p.second.symbol(), p.second);
			this.precedenceTable.put(p.second, GROUP_PRECEDENCE);
			this.closeToOpenTable.put(p.second, p.first);
		}

		this.specialSymbols.addAll(this.symbolTable.keySet());
		this.specialSymbols.addAll(this.openGroupTable.keySet());
		this.specialSymbols.addAll(this.closeGroupTable.keySet());

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
	public static Grammar fromFile(String filepath) throws GrammarException, IOException {
		List<Set<Operation>> precedences = new ArrayList<Set<Operation>>();
		List<Associativity> associativities = new ArrayList<Associativity>();
		List<Pair<OpenGroup, CloseGroup>> groupers = new ArrayList<Pair<OpenGroup, CloseGroup>>();
		BufferedReader fin = new BufferedReader(new FileReader(filepath));
		String line;
		while ((line = fin.readLine()) != null) {
			if (line.charAt(0) == '#')
				continue;
			Set<Operation> precedenceClass = new HashSet<Operation>();
			Scanner in = new Scanner(line);
			String first = in.next();
			if (first.toLowerCase().equals("group")) {
				OpenGroup open = new OpenGroup(in.next());
				CloseGroup close = new CloseGroup(in.next());
				groupers.add(new Pair<OpenGroup, CloseGroup>(open, close));
				try {
					String extraToken = in.next();
					if (extraToken != null)
						;
					throw new GrammarException(String.format(
							"Unexpected extra tokens on line: %s, such as: <%s>", line, extraToken));
				} catch (NoSuchElementException e) {
				} finally {
					in.close();
				}
			} else {
				try {
					associativities.add(Associativity.fromString(first));
					String operation;
					try {
						while ((operation = in.next()) != null) {
							Operation op = Operation.fromString(operation);
							checkOverlappingOperations(precedences, precedenceClass, op);
							precedenceClass.add(op);
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
		}
		fin.close();
		return new Grammar(precedences, associativities, groupers);
	}

	private static void checkOverlappingOperations(List<Set<Operation>> precedences,
			Set<Operation> precedenceClass, Operation op) throws GrammarException {
		for (Set<Operation> precClass : precedences)
			for (Operation otherOp : precClass)
				reportOverlappingOperations(op, otherOp);
		for (Operation otherOp : precedenceClass)
			reportOverlappingOperations(op, otherOp);
	}

	private static void reportOverlappingOperations(Operation op, Operation otherOp)
			throws GrammarException {
		if (op.symbol().indexOf(otherOp.symbol()) != -1
				|| otherOp.symbol().indexOf(op.symbol()) != -1)
			throw new GrammarException(String.format("Operations %s and %s overlap", otherOp, op));
	}

	public String toString() {
		StringBuffer result = new StringBuffer("Grammar:\n");
		for (int i = 0; i < this.precedences.size(); i++) {
			result.append(this.associativities.get(i));
			for (Operation o : this.precedences.get(i))
				result.append(' ').append(o.toString());
			result.append('\n');
		}
		for (Pair<OpenGroup, CloseGroup> p : groupers) {
			result.append("group ");
			result.append(p.first);
			result.append(' ');
			result.append(p.second);
			result.append('\n');
		}
		return result.toString();
	}

	public OpenGroup openGroup(CloseGroup close) {
		return this.closeToOpenTable.get(close);
	}

	/**
	 * @return Returns the set of operation and group symbols in this grammar
	 */
	public Set<String> specialSymbols() {
		return specialSymbols;
	}

	/**
	 * Determines if the string is an operation symbol in this grammar
	 */
	public boolean isSpecialSymbol(String s) {
		return this.specialSymbols.contains(s);
	}

	/**
	 * Gets an Operation object given a symbol
	 */
	public Token getToken(String s) {
		if (this.symbolTable.containsKey(s))
			return this.symbolTable.get(s);
		else if (this.openGroupTable.containsKey(s))
			return this.openGroupTable.get(s);
		else if (this.closeGroupTable.containsKey(s))
			return this.closeGroupTable.get(s);
		else {
			System.err.println("OH GOD NO");
			return null;
		}

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
	 *             if two Identifiers are being compared do not have precedence
	 *             rules
	 */
	public boolean leftIsTighter(Token left, Token right) throws GrammarException {

		Integer leftPrecedence = this.precedenceTable.get(left);
		Integer rightPrecedence = this.precedenceTable.get(right);

		String error = String.format(
				"The following tokens do not have precedence rules: <%s> <%s>.", left, right);

		if (leftPrecedence == null || rightPrecedence == null)
			throw new GrammarException(error);

		// Handle equal precedence
		if (leftPrecedence == rightPrecedence) {
			Associativity assoc = this.associativityTable.get(left);
			if (assoc == null)
				throw new GrammarException(error);
			switch (assoc) {
			case Left:
				return true;
			case Right:
				return false;
			default:
				throw new Error("Associativity Enumeration is broken");
			}
		}
		// Lower precedence is tighter precedence
		else
			return leftPrecedence < rightPrecedence;

	}
}
