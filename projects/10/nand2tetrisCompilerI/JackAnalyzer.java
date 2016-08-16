package nand2tetrisCompilerI;

import java.io.File;


public class JackAnalyzer {

	public static void main (String args[]) {		
		File fileOrDir = new File(args[0]);
		if (fileOrDir.isFile()) {
			if (!(args[0].split("\\.")[1].equals("jack"))) {
				throw new IllegalArgumentException("file type must be .jack");
			} else {
				compileFile(fileOrDir);
			}
		} else if (fileOrDir.isDirectory()) {
			for (File file : fileOrDir.listFiles()) {
				if (file.isFile() && file.getPath().split("\\.")[1].equals("jack")) {
					compileFile(file);
				}
			}
		} else {
			throw new IllegalArgumentException("input not recognzied as file or folder");
		}
	}
	
	private static void compileFile(File inputFile) {
		File outputFile = new File(inputFile.getName().split("\\.")[0]+".xml");
		CompilationEngine c = new CompilationEngine(inputFile, outputFile);
		c.CompileClass();
		c.finish();
	}
}
