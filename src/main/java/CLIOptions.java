package main.java;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CLIOptions {
	
	private boolean append = false; // in case of multiple tv shows with the same episodes, keep all episodes and append -1 to first version, -2 to second, etc... | by default appending is flase and will ask user for each conflict
	private boolean log = false; // enable logging on not | by default logging is disabled | logs will group by TV show -> By Season --> old episode path to new episode path
	private boolean first = false; // always match with first option of API results | by default first match is disabled
	private boolean move = false; // if true, move renamed file (aka copy renamed file + delete original) | if false create a copy of of episode in correct structure BUT do NOT touch the original file   
	private File dest = null; // Path to the  root directory where all the formatted files must be
	private List<String> dirs2beautify = new ArrayList<String>(); // list of all the dirs given to beautify

	public CLIOptions(String[] args) {
		if (args[0].equals("-h") || args[0].contentEquals("--help")) {
			printHelpPage();
			return;
		}
		for (String arg : args) {
			if (arg.charAt(0) == '-') {
				if (arg.charAt(1) == '-') {
					handleFullOptionName(arg);
				} else {
					handleSingleLetter(arg);
				}
			} else {
				dirs2beautify.add(arg);
			}
		}
	}

	private void printHelpPage() {
		// TODO Auto-generated method stub
		
	}

	private void handleSingleLetter(String arg) {
		switch(arg.charAt(1)) {
			case 'a':
				setAppend(true);
				break;
			case 'l':
				setLog(true);
				break;
			case 'f':
				setFirst(true);
				break;
			case 'm':
				setMove(true);
				break;
			case 'd':
				if (arg.charAt(2) != '=') {
					System.out.printf("correct usage of -d is -d=$ResultDirectory");
				} else {
					setDest(new File(arg.split("=")[1]));
				}
				break;
			default:
				System.out.printf("Incorrect use of option '%s'\nAvailable options are:\n\t-a: append\n\t-l: logs\n\t-f: first match\n\t-m: move\n\t-d: directory\n", arg);
		}
	}

	private void handleFullOptionName(String arg) {
		switch(arg) {
			case "--append":
				setAppend(true);
				break;
			case "--log":
				setLog(true);
				break;
			case "--first":
				setFirst(true);
				break;
			case "--move":
				setMove(true);
				break;
			case "--directory":
				setDest(new File(arg.split("=")[0]));
		}
	}

	public boolean isAppend() {
		return append;
	}

	public void setAppend(boolean append) {
		this.append = append;
	}

	public boolean isLog() {
		return log;
	}

	public void setLog(boolean log) {
		this.log = log;
	}

	public boolean isFirst() {
		return first;
	}

	public void setFirst(boolean first) {
		this.first = first;
	}

	public boolean isMove() {
		return move;
	}

	public void setMove(boolean move) {
		this.move = move;
	}

	public File getDest() {
		return dest;
	}

	public void setDest(File dest) {
		this.dest = dest;
	}

}
