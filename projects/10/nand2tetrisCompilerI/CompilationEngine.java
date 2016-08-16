package nand2tetrisCompilerI;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class CompilationEngine {

	private BufferedWriter bufferedWriter;
	private JackTokenizer jackTokenizer;
	private static String indent = "";
	private static String[] statementTypes = { "let", "if", "else", "while", "do", "return" };
	private static String[] operatorTypes = { "+", "-", "*", "/", "&", "|", "&lt;", "&gt;", "=" };
	private static String[] unaryOperatorTypes = { "-", "~" };

	public CompilationEngine(File inputFile, File outputFile) {
		try {
			bufferedWriter = new BufferedWriter(new FileWriter(outputFile));
			jackTokenizer = new JackTokenizer(inputFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void CompileClass() {
		enterNonTerminal("class");
		writeTerminal();
		writeTerminal();
		writeTerminal();
		while (true) {
			if (jackTokenizer.getCurrentToken().equals("static") || jackTokenizer.getCurrentToken().equals("field")) {
				compileClassVarDec();
			} else if (jackTokenizer.getCurrentToken().equals("constructor")
					|| jackTokenizer.getCurrentToken().equals("function")
					|| jackTokenizer.getCurrentToken().equals("method")) {
				compileSubroutineDec();
			} else {
				break;
			}
		}
		writeTerminal();
		exitNonTerminal("class");
	}

	private void compileClassVarDec() {
		enterNonTerminal("classVarDec");
		writeTerminal();
		writeTerminal();
		writeTerminal();
		while (!jackTokenizer.getCurrentToken().equals(";")) {
			writeTerminal();
		}
		writeTerminal();
		exitNonTerminal("classVarDec");
	}

	private void compileSubroutineDec() {
		enterNonTerminal("subroutineDec");
		writeTerminal();
		writeTerminal();
		writeTerminal();
		writeTerminal();
		compileParamList();
		writeTerminal();
		compileSubroutineBody();
		exitNonTerminal("subroutineDec");
	}

	private void compileSubroutineBody() {
		enterNonTerminal("subroutineBody");
		writeTerminal();
		while (jackTokenizer.getCurrentToken().equals("var")) {
			compileVarDec();
		}
		compileStatements();
		writeTerminal();
		exitNonTerminal("subroutineBody");
	}

	private void compileStatements() {
		enterNonTerminal("statements");
		while (Arrays.asList(statementTypes).contains(jackTokenizer.getCurrentToken())) {
			if (jackTokenizer.getCurrentToken().equals("let")) {
				compileLetStatement();
			} else if (jackTokenizer.getCurrentToken().equals("if")) {
				compileIfStatement();
			} else if (jackTokenizer.getCurrentToken().equals("while")) {
				compileWhileStatement();
			} else if (jackTokenizer.getCurrentToken().equals("do")) {
				compileDoStatement();
			} else {
				compileReturnStatement();
			}
		}
		exitNonTerminal("statements");
	}

	private void compileLetStatement() {
		enterNonTerminal("letStatement");
		writeTerminal();
		writeTerminal();
		if (jackTokenizer.getCurrentToken().equals("[")) {
			writeTerminal();
			compileExpression();
			writeTerminal();
		}
		writeTerminal();
		compileExpression();
		writeTerminal();
		exitNonTerminal("letStatement");
	}

	private void compileIfStatement() {
		enterNonTerminal("ifStatement");
		writeTerminal();
		writeTerminal();
		compileExpression();
		writeTerminal();
		writeTerminal();
		compileStatements();
		writeTerminal();
		if (jackTokenizer.getCurrentToken().equals("else")) {
			writeTerminal();
			writeTerminal();
			compileStatements();
			writeTerminal();
		}
		exitNonTerminal("if");
	}

	private void compileWhileStatement() {
		enterNonTerminal("whileStatement");
		writeTerminal();
		writeTerminal();
		compileExpression();
		writeTerminal();
		writeTerminal();
		compileStatements();
		writeTerminal();
		exitNonTerminal("whileStatement");
	}

	private void compileDoStatement() {
		enterNonTerminal("doStatement");
		writeTerminal();
		compileSubroutineCall();
		writeTerminal();
		exitNonTerminal("doStatement");
	}

	private void compileReturnStatement() {
		enterNonTerminal("returnStatement");
		writeTerminal();
		if (!jackTokenizer.getCurrentToken().equals(";")) {
			compileExpression();
		}
		writeTerminal();
		exitNonTerminal("returnStatement");

	}

	private void compileExpression() {
		enterNonTerminal("expression");
		compileTerm();
		while (Arrays.asList(operatorTypes).contains(jackTokenizer.getCurrentToken())) {
			writeTerminal();
			compileTerm();
		}
		exitNonTerminal("expression");
	}

	private void compileTerm() {
		enterNonTerminal("term");
		if (jackTokenizer.tokenType().equals("integerConstant") || jackTokenizer.tokenType().equals("stringConstant")
				|| jackTokenizer.tokenType().equals("keyword")) {
			writeTerminal();
		} else if (jackTokenizer.tokenType().equals("symbol")) {
			if (Arrays.asList(unaryOperatorTypes).contains(jackTokenizer.getCurrentToken())) {
				writeTerminal();
				compileTerm();
			} else {
				writeTerminal();
				compileExpression();
				writeTerminal();
			}
		} else {
			if (jackTokenizer.lookAhead().equals("[")) {
				writeTerminal();
				writeTerminal();
				compileExpression();
				writeTerminal();
			} else if (jackTokenizer.lookAhead().equals(".")) {
				compileSubroutineCall();
			} else {
				writeTerminal();
			}
		}
		exitNonTerminal("term");
	}

	private void compileSubroutineCall() {
		writeTerminal();
		if (jackTokenizer.getCurrentToken().equals(".")) {
			writeTerminal();
			writeTerminal();
		}
		writeTerminal();
		compileExpressionList();
		writeTerminal();
	}

	private void compileExpressionList() {
		enterNonTerminal("expressionList");
		if (!jackTokenizer.getCurrentToken().equals(")")){
			compileExpression();
			while (jackTokenizer.getCurrentToken().equals(",")){
				writeTerminal();
				compileExpression();
			}
		}
		exitNonTerminal("expressionList");
	}

	private void compileVarDec() {
		enterNonTerminal("varDec");
		while (!jackTokenizer.getCurrentToken().equals(";")) {
			writeTerminal();
		}
		writeTerminal();
		exitNonTerminal("varDec");
	}

	private void compileParamList() {
		enterNonTerminal("parameterList");
		while (!jackTokenizer.getCurrentToken().equals(")")) {
			writeTerminal();
		}
		exitNonTerminal("parameterList");
	}

	private void decreaseIndent() {
		indent = indent.substring(0, indent.length() - 2);
	}

	private void increaseIndent() {
		indent += "  ";
	}

	private void writeTerminal() {
		try {
			bufferedWriter.write(indent);
			bufferedWriter.write("<" + jackTokenizer.tokenType() + "> " + jackTokenizer.getCurrentToken() + " </"
					+ jackTokenizer.tokenType() + ">\n");
			if (!jackTokenizer.currentTokenIsFinal()) {
				jackTokenizer.advance();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void enterNonTerminal(String type) {
		try {
			bufferedWriter.write(indent);
			bufferedWriter.write("<" + type + ">\n");
			increaseIndent();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void exitNonTerminal(String type) {
		try {
			decreaseIndent();
			bufferedWriter.write(indent);
			bufferedWriter.write("</" + type + ">\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void finish() {
		try {
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
