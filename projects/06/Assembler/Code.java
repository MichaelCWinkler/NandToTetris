// Michael Winkler
// 6.26.16

package nand2tetrisAssembler;

import java.util.HashMap;

public class Code {

	private static HashMap<String, String> destination;
	private static HashMap<String, String> jump;
	private static HashMap<String, String> computation;
	
	static{
		destination = new HashMap<String, String>();
		jump = new HashMap<String, String>();
		computation = new HashMap<String, String>();
		
		destination.put("null", "000");
		destination.put("M", "001");
		destination.put("D", "010");
		destination.put("MD", "011");
		destination.put("A", "100");
		destination.put("AM", "101");
		destination.put("AD", "110");
		destination.put("AMD","111");
		
		jump.put("null", "000");
		jump.put("JGT", "001");
		jump.put("JEQ", "010");
		jump.put("JGE", "011");
		jump.put("JLT", "100");
		jump.put("JNE", "101");
		jump.put("JLE", "110");
		jump.put("JMP", "111");
		
		computation.put("M", "1110000");
		computation.put("!M", "1110001");
		computation.put("-M", "1110011");
		computation.put("M+1", "1110111");
		computation.put("M-1", "1110010");
		computation.put("D+M", "1000010");
		computation.put("D-M", "1010011");
		computation.put("M-D", "1000111");
		computation.put("D&M", "1000000");
		computation.put("D|M", "1010101");
		computation.put("0", "0101010");
		computation.put("1", "0111111");
		computation.put("-1", "0111010");
		computation.put("D", "0001100");
		computation.put("A", "0110000");
		computation.put("!D", "0001101");
		computation.put("!A", "0110001");
		computation.put("-D", "0001111");
		computation.put("-A", "0110011");
		computation.put("D+1", "0011111");
		computation.put("A+1", "0110111");
		computation.put("D-1", "0001110");
		computation.put("A-1", "0110010");
		computation.put("D+A", "0000010");
		computation.put("D-A", "0010011");
		computation.put("A-D", "0000111");
		computation.put("D&A", "0000000");
		computation.put("D|A", "0010101");
	}
	
	protected static String dest(String assembly) {
		return destination.get(assembly);
	}
	
	protected static String jump(String assembly) {	
		return jump.get(assembly);
	}
	
	protected static String comp(String assembly) {
		return computation.get(assembly);
	}
}
