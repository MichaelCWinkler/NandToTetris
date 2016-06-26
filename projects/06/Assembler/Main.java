// Michael Winkler
// 6.26.16

// Execute this module to assemble .asm files.
// Pass this module the name and path of the file you 
// wish to assemble as the only argument.

package nand2tetrisAssembler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {

	public static void main(String args[]) {

		String lineTranslated = "";
		BufferedWriter writer = null;
		int symbolTranslated;
		Parser parser = new Parser(args[0]);
		
		String outputFileName = args[0].split("\\.")[0] + ".hack";
		File outputFile = new File(outputFileName);
		
		SymbolTable symbolTable = new SymbolTable();
		
		int nextAvailableROMAddress = 0;
		int nextAvailableRAMAddress = 16;
		
		
		
		try {
			writer = new BufferedWriter(new FileWriter(outputFile));

			// first pass - building the symbol table so that when a-commands
			// referencing labels are encountered on the second pass, we know
			// where in memory we should move
			while (parser.hasMoreCommands()) {
				if (!parser.currentLineIsComment()) {
					if (parser.commandType().equals("L_COMMAND")) {
						symbolTable.addEntry(parser.symbol(), nextAvailableROMAddress);
					} else {
						nextAvailableROMAddress++;
					}
				}
			}

			parser = new Parser(args[0]);
			// second pass - this time, execute the c- and a-commands we find.
			// Deal with a commands that contain symbols using the symbol table
			while (parser.hasMoreCommands()) {
				if (!parser.currentLineIsComment() && parser.commandType()!="L_COMMAND") {

					if (parser.commandType().equals("C_COMMAND")) {
						lineTranslated = "111" + Code.comp(parser.comp()) + Code.dest(parser.dest())
								+ Code.jump(parser.jump());
					} else if (parser.commandType().equals("A_COMMAND")) {
						// If the current symbol has anything other than digits, we know that we need 
						// to deal with a symbolic a-command, so we check if the current symbol is in 
						// the symbol table yet, If it isn't we add it.
						if (!parser.symbol().matches("^[0-9]+$")) {
							if (!symbolTable.contains(parser.symbol())){
								symbolTable.addEntry(parser.symbol(), nextAvailableRAMAddress++);
							}
							symbolTranslated = symbolTable.getAddress(parser.symbol());
						}
						else {
							symbolTranslated = Integer.parseInt(parser.symbol());
						}
						lineTranslated = parser.currentSymbolAsBinaryString(symbolTranslated);
					}
					writer.write(lineTranslated);
					writer.newLine();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
