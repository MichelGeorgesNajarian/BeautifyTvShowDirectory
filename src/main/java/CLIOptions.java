package main.java;

import java.util.ArrayList;
import java.util.List;

public class CLIOptions {
	
	private List<Opts> allOpt = new ArrayList<Opts>(); //object with all the CLI options
	private List<String> dirs2beautify = new ArrayList<String>(); // list of all the dirs given to beautify

	public CLIOptions(String[] args) {
		//putting all options in HashMap
		allOpt.add(new Opts('a', 0));
		allOpt.add(new Opts('l', 0));
		allOpt.add(new Opts('f', 0));
		allOpt.add(new Opts('h', 0));
		allOpt.add(new Opts('m', 0));
		allOpt.add(new Opts('d', 1));
		if (args[0].equals("-h") || args[0].contentEquals("--help")) {
			printHelpPage();
			return;
		}
		for (int i = 0; i < args.length; i++) {
			if (args[i].charAt(0) == '-') {
				Opts currOpt = null;
				if (args[i].charAt(1) == '-') {
					currOpt = matchOpt(args[i].charAt(2));
					try {
						currOpt.addOpts(args[i].split("=")[1]);
					} catch (IndexOutOfBoundsException e) {
						System.out.printf("Wrong format passed for %s. Expected format is:\n--option=argument\n", args[i]);
					}
				} else {
					currOpt = matchOpt(args[i].charAt(1));
					for (i++; i < i + currOpt.getNumOpt(); i++) {
						currOpt.addOpts(args[i]);
					}
					i--; // to not skip record which comes after all of the arguments of an option are parsed
				}
			} else {
				dirs2beautify.add(args[i]);
			}
		}
	}

	private Opts matchOpt(char arg) {
		for (int i = 0; i < this.allOpt.size(); i++) {
			if (this.allOpt.get(i).getName() == arg) {
				return this.allOpt.get(i);
			}
		}
		return null;
	}

	public List<Opts> getAllOpt() {
		return allOpt;
	}

	public void setAllOpt(List<Opts> allOpt) {
		this.allOpt = allOpt;
	}

	private void printHelpPage() {
		// TODO Auto-generated method stub
		
	}

}
