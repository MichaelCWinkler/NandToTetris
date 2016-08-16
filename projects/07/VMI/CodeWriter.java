package nand2tetrisVMI;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;

public class CodeWriter {

	private BufferedWriter writer;
	private String fileName;
	private HashMap<String, String> segments;
	private int labelCounter = 0;

	public CodeWriter(BufferedWriter writer) {
		this.writer = writer;
		segments = new HashMap<String, String>();
		segments.put("argument", "ARG");
		segments.put("local", "LCL");
		segments.put("this", "THIS");
		segments.put("that", "THAT");
		segments.put("pointer", "R3");
		segments.put("temp", "R5");
	}

	protected void setFileName(String fileName) {
		this.fileName = fileName;
	}

	private void unaryArithmetic() {
		try {
			writer.write("@SP\n");
			writer.write("A=M-1\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void binaryArithmetic() {
		try {
			writer.write("@SP\n");
			writer.write("AM=M-1\n");
			writer.write("D=M\n");
			writer.write("A=A-1\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void pushConstant(String index) {
		System.out.println("pushing constant");
		try {
			writer.write("@" + index + "\n");
			writer.write("D=A\n");
			writer.write("@SP\n");
			writer.write("A=M\n");
			writer.write("M=D\n");
			writer.write("@SP\n");
			writer.write("M=M+1\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void pushStatic(String index) {
		try {
			writer.write("@" + fileName + "." + index + "\n");
			writer.write("D=M\n");
			writer.write("@SP\n");
			writer.write("A=M\n");
			writer.write("M=D\n");
			writer.write("@SP\n");
			writer.write("M=M+1\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void pushGeneral(String index, String register, String offset) {
		try {
			writer.write("@" + index + "\n");
			writer.write("D=A\n");
			writer.write("@" + register + "\n");
			writer.write("A=" + offset + "\n");
			writer.write("D=M\n");
			writer.write("@SP\n");
			writer.write("A=M\n");
			writer.write("M=D\n");
			writer.write("@SP\n");
			writer.write("M=M+1\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void popStatic(String index) {
		try {
			writer.write("@SP\n");
			writer.write("AM=M-1\n");
			writer.write("D=M\n");
			writer.write("@" + fileName + "." + index + "\n");
			writer.write("M=D\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void popGeneral(String index, String register, String offset) {
		try {
			writer.write("@" + index + "\n");
			writer.write("D=A\n");
			writer.write("@" + register + "\n");
			writer.write("D=" + offset + "\n");
			writer.write("@R13\n");
			writer.write("M=D\n");
			writer.write("@SP\n");
			writer.write("AM=M-1\n");
			writer.write("D=M\n");
			writer.write("@R13\n");
			writer.write("A=M\n");
			writer.write("M=D\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void conditional(String condition){
		String label1 = generateNextLabel();
		String label2 = generateNextLabel();
		try {
			writer.write("@SP\n");
			writer.write("AM=M-1\n");
			writer.write("D=M\n");
			writer.write("A=A-1\n");
			writer.write("D=M-D\n");
			writer.write("@" + label1 + "\n");
			writer.write("D;" + condition + "\n");
			writer.write("D=0\n");
			writer.write("@" + label2 + "\n");
			writer.write("0;JMP\n");
			writer.write("(" + label1 + ")\n");
			writer.write("D=-1\n");
			writer.write("(" + label2 + ")\n");
			writer.write("@SP\n");
			writer.write("A=M-1\n");
			writer.write("M=D\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String generateNextLabel() {
		return "LABEL" + labelCounter++;
	}

	protected void writeArithmetic(String command) {
		try {
			if (command.equals("add")) {
				binaryArithmetic();
				writer.write("M=M+D\n");
			} else if (command.equals("sub")) {
				binaryArithmetic();
				writer.write("M=M-D\n");
			} else if (command.equals("neg")) {
				unaryArithmetic();
				writer.write("M=-M\n");
			} else if (command.equals("eq")) {
				conditional("JEQ");
			} else if (command.equals("gt")) {
				conditional("JGT");
			} else if (command.equals("lt")) {
				conditional("JLT");
			} else if (command.equals("and")) {
				binaryArithmetic();
				writer.write("M=M&D\n");
			} else if (command.equals("or")) {
				binaryArithmetic();
				writer.write("M=M|D\n");
			} else if (command.equals("not")) {
				unaryArithmetic();
				writer.write("M=!M\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void writePushPop(String command, String segment, String index) {
		System.out.println("Command = " + command);
		System.out.println("segment = " + segment);
		if (command.equals("C_PUSH")) {
			if (segment.equals("constant")) {
				System.out.println("pushing constant");
				pushConstant(index);
			} else if (segment.equals("static")) {
				System.out.println("pushing static");
				pushStatic(index);
			} else if (segment.equals("pointer") || segment.equals("temp")) {
				System.out.println("pushing pointer or temp");
				pushGeneral(index, segments.get(segment), "A+D");
			} else {
				System.out.println("pushing general");
				pushGeneral(index, segments.get(segment), "M+D");
			}
		} else { // command is C_POP
			if (segment.equals("static")) {
				popStatic(index);
			} else if (segment.equals("pointer") || segment.equals("temp")) {
				popGeneral(index, segments.get(segment), "A+D");
			} else {
				popGeneral(index, segments.get(segment), "M+D");
			}
		}

	}

	protected void finish() {
		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
