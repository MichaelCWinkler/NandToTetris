// Michael Winkler
// 6.26.16

package nand2tetrisAssembler;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Parser {
	
	Scanner scanner;
	String currentCommand;

	public Parser (String inputFileName) {
			try {
				scanner = new Scanner(new File(inputFileName));
			} catch (IOException e) {
				e.printStackTrace();
			}	
	}
	
	//I rolled the hasMoreCommands and advance routines from the nand2tetris 
	//Parser API into one function. The functionality is the same.
	protected boolean hasMoreCommands() {
		if (scanner.hasNextLine()){
			currentCommand = scanner.nextLine().trim();
			return true;
		}
		else 
			scanner.close();
			return false;
		
	}
	
	protected String commandType (){
		if (currentCommand.substring(0,1).equals("@")){
			return ("A_COMMAND");
		}
		else if (currentCommand.substring(0,1).equals("(")){
			return ("L_COMMAND");
		}
		else {
			return ("C_COMMAND");
		}
		
	}
	
	protected String symbol () {
		if (commandType().equals("A_COMMAND")){
			return (currentCommand.substring(1));
		}
		else {
			return (currentCommand.substring(1,currentCommand.length()-1));
		}
	}
	
	protected String dest () {
		if (currentCommand.contains("=")){
			return currentCommand.split("=")[0].trim();
		}
		else {
			return "null";
		}
	}
	
	protected String jump () {
		if (currentCommand.contains(";")){
			return currentCommand.split(";")[1].trim();
		}
		else {
			return "null";
		}
	}
		
	protected String comp () {
		if (currentCommand.contains("=")){
			return currentCommand.split("=")[1].trim();
		}
		else {
			return currentCommand.split(";")[0];
		}
	}


	//Checks if the currentCommand is a comment and also strips any
	// end-of-line comments
	public boolean currentLineIsComment() {
		if (currentCommand.contains("//")){
			currentCommand = currentCommand.split("//")[0];
		}
		if (currentCommand.length() == 0 ){
			return true;
		}
		else {
			return false;
		}
	}

	public String currentSymbolAsBinaryString(int symbol) {
		System.out.println("In binaryString, input = " + symbol);
		String symbolAsBinaryString = Integer.toBinaryString(symbol);
		System.out.println("symbolAsBinaryString = " + symbolAsBinaryString);
		while (symbolAsBinaryString.length() < 16){
			symbolAsBinaryString = "0" + symbolAsBinaryString;
		}
		return symbolAsBinaryString;
	}
	
	
}
