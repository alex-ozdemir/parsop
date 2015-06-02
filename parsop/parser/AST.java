package parsop.parser;

import java.util.List;

import parsop.grammar.Token;

public class AST {
	
	Token node;
	List<AST> operands;

	public AST(Token node, List<AST> operands) {
		this.node = node;
		this.operands = operands;
	}

	public String toString() {
		StringBuffer result = new StringBuffer("{");
		result.append(this.node.toString());
		for (AST operand : operands)
			result.append(", ").append(operand.toString());
		result.append('}');
		return result.toString();
	}

	public Token getNode() {
		return node;
	}
}
