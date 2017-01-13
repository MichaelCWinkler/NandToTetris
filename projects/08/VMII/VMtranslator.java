package nand2tetrisVMI;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class VMtranslator {

	public static final int C_ARITHMETIC = 0;
	public static final int C_PUSH = 1;
	public static final int C_POP = 2;
	public static final int C_LABEL = 3;
	public static final int C_GOTO = 4;
	public static final int C_IF = 5;
	public static final int C_FUNCTION = 6;
	public static final int C_RETURN = 7;
	public static final int C_CALL = 8;
	
	private static CodeWriter theCodeWriter;

	public static void main(String args[]) {

		File fileOrDir = new File(args[0]);
		String outputFileName = args[0].split("\\.")[0] + ".asm";
		BufferedWriter bufferedWriter;

		try {
			bufferedWriter = new BufferedWriter(new FileWriter(outputFileName));
			theCodeWriter = new CodeWriter(bufferedWriter);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (fileOrDir.isFile()) {
			if (!(args[0].split("\\.")[1].equals("vm"))) {
				throw new IllegalArgumentException("file type must be .vm");
			} else {
				System.out.println("process single file");
				theCodeWriter.setFileName(fileOrDir.getName());
				theCodeWriter.writeInit();
				processFile(fileOrDir);
				theCodeWriter.finish();
			}
		} else if (fileOrDir.isDirectory()) {
			System.out.println("processing directory");
			theCodeWriter.writeInit();
			for (File file : fileOrDir.listFiles()) {
				theCodeWriter.setFileName(file.getName());
				System.out.println("current file  = "+ file);
				if (file.isFile() && file.getName().split("\\.")[1].equals("vm")) {
					processFile(file);
				}
			}
			theCodeWriter.finish();
		} else {
			throw new IllegalArgumentException("input not recognzied as file or folder");
		}
	}

	private static void processFile(File file) {
		
		Parser parser = new Parser(file);
		
		//Use map (command type -> function name) + reflection + array of strings to be passed to the code writer function
		while (parser.hasMoreCommands()) {
			if (parser.commandType() == C_ARITHMETIC) {
				theCodeWriter.writeArithmetic((parser.arg1()));
			} else if (parser.commandType() == C_PUSH || parser.commandType() == C_POP) {
				theCodeWriter.writePushPop(parser.commandType(), parser.arg1(), parser.arg2());
			} else if (parser.commandType() == C_LABEL){
				theCodeWriter.writeLabel(parser.arg1());
			} else if (parser.commandType() == C_GOTO){
				theCodeWriter.writeGoto(parser.arg1());
			} else if (parser.commandType() == C_IF ){
				theCodeWriter.writeIf(parser.arg1());
			} else if (parser.commandType() == C_FUNCTION){
				theCodeWriter.writeFunction(parser.arg1(), parser.arg2());
			} else if (parser.commandType() == C_RETURN){
				theCodeWriter.writeReturn();
			} else {
				theCodeWriter.writeCall(parser.arg1(), parser.arg2());
			}
		}
	}
}
