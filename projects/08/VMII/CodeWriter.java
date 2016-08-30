package nand2tetrisVMI;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;

public class CodeWriter {

	public static final int C_PUSH = 1;
	public static final int C_POP = 2;
	
	private BufferedWriter writer;
	private String fileName;
	private HashMap<String, String> segments;
	private int labelCounter = 0;
	private String currentFunctionName;

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

	private void preFunctionCallPush() {
		try {
			writer.write("@SP\n");
			writer.write("A=M\n");
			writer.write("M=D\n");
			writer.write("@SP\n");
			writer.write("AM=M+1\n");
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

	private void conditional(String condition) {
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

	protected void writePushPop(int command, String segment, String index) {
		System.out.println("Command = " + command);
		System.out.println("segment = " + segment);
		if (command == C_PUSH ) {
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
		} else {
			if (segment.equals("static")) {
				popStatic(index);
			} else if (segment.equals("pointer") || segment.equals("temp")) {
				popGeneral(index, segments.get(segment), "A+D");
			} else {
				popGeneral(index, segments.get(segment), "M+D");
			}
		}

	}

	protected void writeInit() {
		try {
			writer.write("@256\n");
			writer.write("D=A\n");
			writer.write("@SP\n");
			writer.write("M=D\n");
			writeCall("Sys.init", "0");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void writeLabel(String label) {
		try {
			writer.write("(" + currentFunctionName + "$" + label + ")\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void writeGoto(String label) {
		try {
			writer.write("@" + currentFunctionName + "$" + label + "\n");
			writer.write("0;JMP\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void writeIf(String label) {
		try {
			writer.write("@SP\n");
			writer.write("AM=M-1\n");
			writer.write("D=M\n");
			writer.write("@" + currentFunctionName + "$" + label + "\n");
			writer.write("D;JNE\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void writeCall(String functionName, String numArgs) {
		String currentFunctionLabel = generateNextLabel();
		try {
			//push return address
			writer.write("@" + currentFunctionLabel + "\n");
			writer.write("D=A\n");
			preFunctionCallPush();
			
			//push LCL
			writer.write("@LCL\n");
			writer.write("D=M\n");
			preFunctionCallPush();
			
			//push ARG
			writer.write("@ARG\n");
			writer.write("D=M\n");
			preFunctionCallPush();
			
			//push THIS
			writer.write("@THIS\n");
			writer.write("D=M\n");
			preFunctionCallPush();
			
			//push THAT
			writer.write("@THAT\n");
			writer.write("D=M\n");
			preFunctionCallPush();
			
			//reposition ARG
			writer.write("@SP\n");
			writer.write("D=M\n");
			writer.write("@" + (Integer.parseInt(numArgs)+5) + "\n");
			writer.write("D=D-A\n");
			writer.write("@ARG\n");
			writer.write("M=D\n");
			
			//reposition LCL
			writer.write("@SP\n");
			writer.write("D=M\n");
			writer.write("@LCL\n");
			writer.write("M=D\n");
			
			//transfer control
			writer.write("@" + functionName + "\n");
			writer.write("0;JMP\n");
			
			//declare label for return address
			writer.write("(" + currentFunctionLabel + ")\n");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void writeReturn() {
		try {
			//FRAME = LCL, store FRAME in R14
			writer.write("@LCL\n");
			writer.write("D=M\n");
			writer.write("@R14\n");
			writer.write("M=D\n");
			
			//RET = (FRAME-5), store RET in R15
			writer.write("@5\n");
			writer.write("A=D-A\n");
			writer.write("D=M\n");
			writer.write("@R15\n");
			writer.write("M=D\n");
			
			//Reposition the return value for the caller
			popGeneral("0", "ARG", "M+D");
			
			//Restore SP of the caller 
			writer.write("@ARG\n");
			writer.write("D=M+1\n");
			writer.write("@SP\n");
			writer.write("M=D\n");
			
			//Restore THAT of the caller
			writer.write("@1\n");
			writer.write("D=A\n");
			writer.write("@R14\n");
			writer.write("A=M-D\n");
			writer.write("D=M\n");
			writer.write("@THAT\n");
			writer.write("M=D\n");
			
			//Restore THIS of the caller
			writer.write("@2\n");
			writer.write("D=A\n");
			writer.write("@R14\n");
			writer.write("A=M-D\n");
			writer.write("D=M\n");
			writer.write("@THIS\n");
			writer.write("M=D\n");
			
			//Restore ARG of the caller
			writer.write("@3\n");
			writer.write("D=A\n");
			writer.write("@R14\n");
			writer.write("A=M-D\n");
			writer.write("D=M\n");
			writer.write("@ARG\n");
			writer.write("M=D\n");
			
			//Restore LCL of the caller
			writer.write("@4\n");
			writer.write("D=A\n");
			writer.write("@R14\n");
			writer.write("A=M-D\n");
			writer.write("D=M\n");
			writer.write("@LCL\n");
			writer.write("M=D\n");
			
			//goto RET
			writer.write("@R15\n");
			writer.write("A=M\n");
			writer.write("0;JMP\n");
			
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

	protected void writeFunction(String functionName, String numLocals) {
		currentFunctionName = functionName;
		try {
			writer.write("(" + functionName + ")\n");
			for (int i = 0; i < Integer.parseInt(numLocals); i++) {
				writePushPop(C_PUSH, "constant", "0");
			}
		} catch (IOException e) {
			e.printStackTrace();
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
