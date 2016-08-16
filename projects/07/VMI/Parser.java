package nand2tetrisVMI;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Parser {

	Scanner scanner;
	String[] currentCommand;

	public Parser(File inputFile) {
		try {
			System.out.println("creating new scanner");
			scanner = new Scanner(inputFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// I rolled the hasMoreCommands and advance routines from the nand2tetris
	// Parser API into one function. The functionality is the same.
	protected boolean hasMoreCommands() {
		if (scanner.hasNextLine()) {
			String currentLine = scanner.nextLine().trim();
			System.out.println("currentLine = " + currentLine);
			if (currentLine.indexOf("/") == 0 || currentLine.length() == 0) {
				// Using recursion here is theoretically bad practice, but I'm
				// not
				// worried about having extremely long comment blocks that would
				// create
				// a risk of stack overflow in this context. Similarly, I'm not
				// worried
				// about performance issues that might require a rewrite.
				return (hasMoreCommands());
			} else if (currentLine.indexOf("/") == -1) {
				currentCommand = currentLine.split(" ");
				return true;
			} else {
				currentCommand = currentLine.split("//")[0].trim().split(" ");
				return true;
			}
		} else
			scanner.close();
		return false;

	}

	protected String commandType() {
		if (currentCommand[0].equals("add") || currentCommand[0].equals("sub") || currentCommand[0].equals("neg")
				|| currentCommand[0].equals("eq") || currentCommand[0].equals("gt") || currentCommand[0].equals("lt")
				|| currentCommand[0].equals("lt") || currentCommand[0].equals("and") || currentCommand[0].equals("or")
				|| currentCommand[0].equals("or") || currentCommand[0].equals("not")) {
			return "C_ARITHMETIC";
		} else if (currentCommand[0].equals("push")) {
			return "C_PUSH";
		} else if (currentCommand[0].equals("pop")) {
			return "C_POP";
		} else if (currentCommand[0].equals("label")) {
			return "C_LABEL";
		} else if (currentCommand[0].equals("goto")) {
			return "C_GOTO";
		} else if (currentCommand[0].equals("if-goto")) {
			return "C_IF";
		} else if (currentCommand[0].equals("function")) {
			return "C_FUNCTION";
		} else if (currentCommand[0].equals("return")) {
			return "C_RETURN";
		} else if (currentCommand[0].equals("call")) {
			return "C_CALL";
		} else {
			throw new IllegalArgumentException("invalid command");
		}
	}

	protected String arg1() {
		if (commandType().equals("C_ARITHMETIC") || commandType().equals("C_RETURN")) {
			return currentCommand[0].trim();
		} else {
			return currentCommand[1].trim();
		}
	}

	protected String arg2() {
		return currentCommand[2].trim();
	}

}
