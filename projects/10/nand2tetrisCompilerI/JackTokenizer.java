package nand2tetrisCompilerI;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JackTokenizer {
	
	Scanner scanner;
	
	private static String KEYWORDREGEX = "";
	private static String SYMBOLREGEX = "";
	private static String INTEGERREGEX = "\\d+";
	private static String STRINGREGEX = "\"[^\"]*\"";
	private static String IDENTIFIERREGEX = "[a-zA-Z_]\\w*";
	private static String COMPLETEREGEX;
	
	static {
		String[] keywords = {"class","constructor","function","method","field","static",
				"var","int","char","boolean","void","true","false","null","this","let",
				"do","if","else","while","return"};
		String [] symbols = {"{","}","(",")","[","]",".",",",";","+","-","*","/","&",
				"|","<",">","=","~"};
		
		for (String keyword: keywords){
			KEYWORDREGEX += keyword + "|";
		}
		
		SYMBOLREGEX += "[";
		for (String symbol: symbols){
			SYMBOLREGEX += "\\" + symbol;
		}
		SYMBOLREGEX += "]";
		
		COMPLETEREGEX = KEYWORDREGEX + SYMBOLREGEX + "|" + INTEGERREGEX + "|" + STRINGREGEX + "|" + IDENTIFIERREGEX;
	}
	
	private ArrayList<String> allTokens;
	private String currentToken;
	private int tokensWritten;
	
	public String getCurrentToken(){
		if (tokenType().equals("stringConstant")){
			return currentToken.substring(1, currentToken.length()-1);
		} else if (currentToken.equals("<")) {
			return ("&lt;");
		} else if (currentToken.equals(">")) {
			return ("&gt;");
		} else {
			return currentToken;
		}
	}
	
	public String lookAhead(){
		if (tokensWritten+1 < allTokens.size()){
			currentToken = allTokens.get(tokensWritten + 1);
			String lookAheadValue = getCurrentToken();
			currentToken = allTokens.get(tokensWritten);
			return lookAheadValue;
		} else {
			return "You tried to access an out of bounds index";
		}
	}
	
	public JackTokenizer (File file) {
		allTokens = new ArrayList<String>();
		String forRegexCheck = "";
		tokensWritten = 0;
		
		try {
			Scanner scanner = new Scanner(file);
			while (scanner.hasNextLine()){
				forRegexCheck += stripInLineComments(scanner.nextLine().trim());
			}
			scanner.close();
			forRegexCheck = stripBlockComments(forRegexCheck);
			System.out.println("after stripping block comments : " + forRegexCheck);
			Matcher matcher = Pattern.compile(COMPLETEREGEX).matcher(forRegexCheck);
			while (matcher.find()){
				allTokens.add(matcher.group());
			}
			currentToken = allTokens.get(0);
			for (String t : allTokens) {
				System.out.print(t + " ");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
	}

	public boolean hasMoreTokens(){
		return (tokensWritten < allTokens.size());
	}
	
	public void advance() {
		tokensWritten++;
		currentToken = allTokens.get(tokensWritten);
	}
	
	public String tokenType () {
		if (currentToken.matches(KEYWORDREGEX)){
			return "keyword";
		} else if (currentToken.matches(SYMBOLREGEX)){
			return "symbol";
		} else if (currentToken.matches(INTEGERREGEX)){
			return "integerConstant";
		} else if (currentToken.matches(STRINGREGEX)){
			return "stringConstant";
		} else if (currentToken.matches(IDENTIFIERREGEX)){
			return "identifier";
		} else {
			throw new IllegalArgumentException ("The current token doesn't match any known token patterns");
		}
	}

	private String stripBlockComments(String forRegexCheck) {
		String commentsRemoved = forRegexCheck.replaceAll("(?:/\\*(?:[^*]|(?:\\*+[^*/]))*\\*+/)|(?://.*)","");
		/*Credit to Stephen Ostermiller for this regex pattern found at 
		 * http://blog.ostermiller.org/find-comment*/		
		return commentsRemoved;
	}

	private String stripInLineComments(String nextLine) {
		if (nextLine.indexOf("//") == -1){
			return nextLine;
		} else {
			return nextLine.substring(0, nextLine.indexOf("//"));
		}
	}

	public boolean currentTokenIsFinal() {
		return tokensWritten == allTokens.size()-1;
	}
	
}
