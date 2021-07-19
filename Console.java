import java.awt.*;
import java.util.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Element;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class Console {
	public static JFrame frame;
	public static JTextArea console;

	public static HashMap<String, Integer> intMap;
	public static HashMap<String, String> strMap;
	public static HashMap<String, Double> doubleMap;
	public static HashMap<String, ArrayList<Object>> listMap;

	public static ScriptEngineManager manager;
	public static ScriptEngine scriptEngine;

	public static int lineNumber;

	public static ArrayList<String> code;

	public static void runCode(ArrayList<String> c) throws ScriptException {
		Init();
		manager = new ScriptEngineManager();
		scriptEngine = manager.getEngineByName("JavaScript");
		intMap = new HashMap<String, Integer>();
		doubleMap = new HashMap<String, Double>();
		strMap = new HashMap<String, String>();
		listMap = new HashMap<String, ArrayList<Object>>();
		code = new ArrayList<String>();
		for (String s : c) {
			code.add(s);
		}
		for (lineNumber = 0; lineNumber < code.size(); lineNumber++) {
			if (process(code.get(lineNumber)) == false) {
				break;
			}
		}
	}

	public static boolean process(String line) {
		int lineNum = lineNumber + 1;
		ArrayList<String> tokens = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(line);
		while (st.hasMoreTokens()) {
			tokens.add(st.nextToken());
		}
		// All the syntax
		if (tokens.size() == 0) {
			return true;
		} else if (tokens.get(0).indexOf("Display") == 0) {
			// Error handling
			if (line.indexOf("(") == -1 || line.indexOf(")") == -1) {
				giveError("Syntax Error");
				return false;
			}
			int indexParan = tokens.get(0).indexOf("Display");
			String displayedText = line.substring(indexParan + "Display".length() + 1, line.length() - 1);
			try {
				dumpVariables();
				Object ob = scriptEngine.eval(displayedText);
				Display(ob + "");
				return true;
			} catch (ScriptException e1) {
				giveError("Error on Line#" + lineNum + ". Value of 'Display' cannot be evaulated and displayed.");
				e1.printStackTrace();
				return false;
			}
		} else if (tokens.size() >= 2 && tokens.get(1).equals("<-")) {
			String varName = tokens.get(0);
			if (tokens.size() == 2) {
				giveError("Error on Line#" + lineNum + ". Variable " + varName
						+ " is declared, but value is not given. Variables must be declared with a value.");
				return false;
			}
			String evalLine = "";
			evalLine = line.substring(line.indexOf("<-") + 2, line.length());
			if (tokens.get(0).indexOf("[") != -1 && tokens.get(0).indexOf("]") != -1) {
				String listName = tokens.get(0).substring(0, tokens.get(0).indexOf("["));
				int num = 0;
				try {
					num = Integer.parseInt(
							tokens.get(0).substring(tokens.get(0).indexOf("[") + 1, tokens.get(0).indexOf("]")));
				} catch (Exception e) {
					giveError("Error on Line#" + lineNum + ". Invalid value for list accessing.");
					return false;
				}
				if (searchForList(listName)) {
					try {
						dumpVariables();
						Object tempVal = scriptEngine.eval(evalLine);
						listMap.get(listName).set(num, tempVal);
						return true;
					} catch (ScriptException e) {
						giveError("Error on Line#" + lineNum + ". Invalid value given to list.");
						e.printStackTrace();
						return false;
					}
				} else {
					giveError("Error on Line#" + lineNum + ". List name " + listName + " is not recognized.");
					return false;
				}
			} else if (evalLine.indexOf("[") != -1 && evalLine.indexOf("]") != -1) {
				evalLine = line.substring(line.indexOf("[") + 1, line.length() - 1);
				if(line.indexOf("[") + 1 == line.indexOf("]")) {
					ArrayList<Object> ob = new ArrayList<Object>();
					scriptEngine.put(varName, ob);
					listMap.put(varName, ob);
					return true;
				}
				if(evalLine.indexOf(",") == -1) {
					ArrayList<Object> ob = new ArrayList<Object>();
					dumpVariables();
					try {
						ob.add(scriptEngine.eval(evalLine));
						scriptEngine.put(varName, ob);
						listMap.put(varName, ob);
					} catch (ScriptException e) {
						giveError("Error on Line#" + lineNum +". Invalid value given to list declaration.");
						e.printStackTrace();
					}
					return true;
				}
				String[] templist = evalLine.split(",");
				ArrayList<Object> objs = new ArrayList<Object>();
				dumpVariables();
				for (String s : templist) {
					try {
						objs.add(scriptEngine.eval(s));
					} catch (ScriptException e) {
						giveError("Error on Line#" + lineNum + ". List + " + varName
								+ " is initialized with inconsistent, non-valid, or non-existent values");
						e.printStackTrace();
						return false;
					}
				}
				listMap.put(varName, objs);
				scriptEngine.put(varName, objs);
				return true;
			} else {
				try {
					dumpVariables();
					Object tempVal = scriptEngine.eval(evalLine);
					System.out.println(evalLine);
					if (tempVal instanceof Integer) {
						intMap.put(varName, (Integer) tempVal);
					} else if (tempVal instanceof Double) {
						doubleMap.put(varName, (Double) tempVal);
					} else if (tempVal instanceof String) {
						strMap.put(varName, (String) tempVal);
					} else {
						giveError("Error on Line#" + lineNum + ". Variable " + varName
								+ " is declared with an invalid value.");
						return false;
					}
					return true;
				} catch (ScriptException e) {
					giveError("Error on Line#" + lineNum + ". Variable " + varName
							+ " is declared with an invalid value.");
					e.printStackTrace();
					return false;
				}
			}
		} else if (line.indexOf(".append(") != -1) {
			String listName = line.substring(0, line.indexOf("."));
			if (searchForList(listName)) {
				String value = line.substring(line.indexOf("(") + 1, line.length() - 1);
				try {
					Object tempVal = scriptEngine.eval(value);
					listMap.get(listName).add(tempVal);
					return true;
				} catch (ScriptException e) {
					giveError("Error on Line#" + lineNum + ". Value + '" + value + "' is not able to be processed.");
					e.printStackTrace();
					return false;
				}
			} else {
				giveError("Error on Line#" + lineNum + ". List + " + listName + " is not found.");
				return false;
			}

		} else if (line.indexOf(".remove(") != -1) {
			String listName = line.substring(0, line.indexOf("."));
			if (searchForList(listName)) {
				String value = line.substring(line.indexOf("(") + 1, line.length() - 1);
				try {
					Object tempVal = scriptEngine.eval(value);
					listMap.get(listName).remove((int) tempVal);
					return true;
				} catch (ScriptException e) {
					giveError("Error on Line#" + lineNum + ". Value + '" + value + "' is not able to be processed.");
					e.printStackTrace();
					return false;
				}
			} else {
				giveError("Error on Line#" + lineNum + ". List + " + listName + " is not found.");
				return false;
			}
		} else if (line.indexOf(".insert(") != -1) {
			String listName = line.substring(0, line.indexOf("."));
			if (searchForList(listName)) {
				String value = line.substring(line.indexOf("(") + 1, line.length() - 1);
				String[] listLine = value.split(",");
				int insertPos;
				String insertVal;
				try {
					insertPos = Integer.parseInt(listLine[0]);
					System.out.println(insertPos);
					insertVal = listLine[1];
					System.out.println(insertVal);
				} catch (Exception e) {
					giveError("Error on Line#" + lineNum + ". Value of Insert is invalid.");
					e.getStackTrace();
					return false;
				}
				try {
					Object tempPos = scriptEngine.eval(insertPos + "");
					Object tempVal = scriptEngine.eval(insertVal);
					listMap.get(listName).add((int) tempPos, tempVal);
					return true;
				} catch (ScriptException e) {
					giveError("Error on Line#" + lineNum + ". Value + '" + insertVal + "' or '" + insertPos
							+ "' is not able to be processed.");
					e.printStackTrace();
					return false;
				}
			} else {
				giveError("Error on Line#" + lineNum + ". List + " + listName + " is not found.");
				return false;
			}
		} else if (line.indexOf("if (") != -1) {
			dumpVariables();
			String boolExpression = line.substring(line.indexOf("(") + 1, line.indexOf(") {"));
			try {
				ArrayList<String> linesOfCode = new ArrayList<String>();
				int numOfBrackets = 1;
				int startingLineNum = lineNumber+1;
				int counter = 0;
				lineNumber++;
				while (numOfBrackets > 0) {
					if (lineNumber >= code.size()) {
						giveError("Error on Line#" + lineNum + ". If statement is not completed.");
					}
					String l = code.get(lineNumber);
					if (l.indexOf("{") != -1) {
						numOfBrackets++;
					}
					if (l.indexOf("}") != -1) {
						numOfBrackets--;
					}
					System.out.println(lineNumber);
					System.out.println("Line: " +l);
					linesOfCode.add(l);
					counter++;
					if (counter > 500) {
						giveError("Error on Line#" + lineNum + ". If statement is not completed.");
					}
					lineNumber++;
				}
				int finalNumber = lineNumber;
				lineNumber = startingLineNum;
				if ((boolean) scriptEngine.eval(boolExpression)) {
					for(lineNumber = startingLineNum; lineNumber < finalNumber; lineNumber++) {
						System.out.println("if statement: " + code.get(lineNumber));
						if (!process(code.get(lineNumber))) {
							return false;
						}
					}
				}
				lineNumber = finalNumber-1;
				System.out.println("last: " + code.get(lineNumber));
				return true;
			} catch (ScriptException e) {
				giveError("Error on Line#" + lineNum + ". Boolean expression " + boolExpression
						+ " cannot be evaluated.");
				e.printStackTrace();
				return false;
			}
		} else if (line.indexOf("Repeat (") != -1) {
			dumpVariables();
			String strTimes = line.substring(line.indexOf("(")+1, line.indexOf(")"));
			ArrayList<String> linesCode = new ArrayList<String>();
			int numOfBrackets = 1;
			int startingLineNum = lineNumber+1;
			lineNumber = startingLineNum;
			int counter = 0;
			while (numOfBrackets > 0) {
				if (lineNumber >= code.size()) {
					giveError("Error on Line#" + lineNum + ". Loop is not completed.");
				}
				String l = code.get(lineNumber);
				if (l.indexOf("{") != -1) {
					numOfBrackets++;
				}
				if (l.indexOf("}") != -1) {
					numOfBrackets--;
				}
				System.out.println("line loop: " + l);
				linesCode.add(l);
				counter++;
				if (counter > 500) {
					giveError("Error on Line#" + lineNum + ". Loop is not completed.");
				}
				lineNumber++;
			}
			try {
				int finalNumber = lineNumber;
				int times = (int)scriptEngine.eval(strTimes);
				for(int i = 0; i < times; i++) {
					for(lineNumber = startingLineNum; lineNumber < finalNumber; lineNumber++) {
						System.out.println("Code: " + code.get(lineNumber));
						if(!process(code.get(lineNumber))) {
							return false;
						}
					}
					
				}
				lineNumber = finalNumber-1;
				return true;
			} catch (ScriptException e) {
				giveError("Error on Line#" + lineNumber +". Loop code malfunction.");
				e.printStackTrace();
				return false;
			}
		} else {
			if (line.equals("{") || line.equals("}")) {
				return true;
			}
			giveError("Syntax Error on Line#" + lineNum + ". Symbols are not recognized");
			return false;
		}
	}

	public static boolean isInt(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static void dumpVariables() {
		for (String s : intMap.keySet()) {
			manager.put(s, intMap.get(s));
		}
		for (String s : doubleMap.keySet()) {
			manager.put(s, doubleMap.get(s));
		}
		for (String s : strMap.keySet()) {
			manager.put(s, strMap.get(s));
		}
		for (String s : listMap.keySet()) {
			manager.put(s, listMap.get(s));
		}
	}

	public static void giveError(String error) {
		console.setText(console.getText() + "\n" + error);
	}

	public static void Display(String value) {
		console.setText(console.getText() + value + " ");
	}

	public static boolean searchForList(String val) {
		for (String s : listMap.keySet()) {
			if (s.equals(val)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isInMap(String value) {
		for (String s : intMap.keySet()) {
			if (s.equals(value)) {
				return true;
			}
		}
		for (String s : doubleMap.keySet()) {
			if (s.equals(value)) {
				return true;
			}
		}
		for (String s : strMap.keySet()) {
			if (s.equals(value)) {
				return true;
			}
		}
		return false;
	}

	public static boolean containsStrings(String s, String val) {
		if (s.length() < val.length()) {
			return false;
		} else {
			for (int i = 0; i < s.length() - val.length(); i++) {
				if (s.substring(i, i + val.length()).equals(val)) {
					return true;
				}
			}
		}
		return false;
	}

	public static void Init() {
		frame = new JFrame();
		console = new JTextArea();
		console.setEditable(false);
		frame.setSize(new Dimension(450, 300));
		frame.add(console);
		frame.setTitle("Console");
		frame.setVisible(true);
	}
}
