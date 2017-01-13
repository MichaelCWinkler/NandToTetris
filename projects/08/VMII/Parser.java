package nand2tetrisVMI;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class Parser {

	Scanner scanner;
	String[] currentCommand;
	HashMap<String, Integer> commandTypes;
	
	public Parser(File inputFile) {
		
		commandTypes = new HashMap<String, Integer>();
		commandTypes.put("add", 0);
		commandTypes.put("sub", 0);
		commandTypes.put("neg", 0);
		commandTypes.put("eq", 0);
		commandTypes.put("gt", 0);
		commandTypes.put("lt", 0);
		commandTypes.put("and", 0);
		commandTypes.put("or", 0);
		commandTypes.put("not", 0);
		commandTypes.put("push", 1);
		commandTypes.put("pop", 2);
		commandTypes.put("label", 3);
		commandTypes.put("goto", 4);
		commandTypes.put("if-goto", 5);
		commandTypes.put("function", 6);
		commandTypes.put("return", 7);
		commandTypes.put("call", 8);

		try {
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
				// Using recursion here is theoretically bad practice, but I'm not
				// worried about having extremely long comment blocks that would create
				// a risk of stack overflow in this context. Similarly, I'm not worried
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

	protected int commandType() {
		return commandTypes.get(currentCommand[0]);
	}

	protected String arg1() {
		//0 and 7 correspond to arithmetic and return command types
		if (commandType() == 0 || commandType() == 7) {
			return currentCommand[0].trim();
		} else {
			return currentCommand[1].trim();
		}
	}

	protected String arg2() {
		return currentCommand[2].trim();
	}

}
