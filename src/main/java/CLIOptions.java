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
//		if (args[0].equals("-h") || args[0].contentEquals("--help")) {
//			printHelpPage();
//			return;
//		}
		for (int i = 0; i < args.length; i++) {
			if (args[i].charAt(0) == '-') { //handle option declaration
				Opts currOpt = null;
				if (args[i].charAt(1) == '-') { //handle full name declaration
					currOpt = matchOpt(args[i].charAt(2));
					currOpt.setValue(true);
					try {
						currOpt.addOpts(args[i].split("=")[1]);
					} catch (IndexOutOfBoundsException e) {
						System.out.printf("Wrong format passed for %s. Expected format is:\n--option=argument\n", args[i]);
					}
				} else { //handle short notation
					currOpt = matchOpt(args[i].charAt(1));
					currOpt.setValue(true);
					int temp = ++i;
					try {
						for (; i < temp + currOpt.getNumOpt(); i++) {
							if (args[i].charAt(0) == '-') {
								currOpt.setValue(false);
								throw new IndexOutOfBoundsException();
							} else {
								currOpt.addOpts(args[i]);
							}
						}
					} catch (IndexOutOfBoundsException e) {
						System.out.printf("wrong use of options %s. Expected %d argument(s) after the option declaration but got %d\n", args[i-1], currOpt.getNumOpt(), currOpt.getLastIndex());
					}
					i--; // to not skip record which comes after all of the arguments of an option are parsed
				}
			} else {
				dirs2beautify.add(args[i]);
			}
		}
		for (int i = 0; i < this.allOpt.size(); i++) {
			Opts temp = this.allOpt.get(i);
			System.out.printf("option %d : %c | active/not active: %b | number of options: %d\n", i, temp.getName(), temp.isValue(), temp.getLastIndex());
			if (temp.getLastIndex() != 0) {
				for (int j = 0; j < temp.getLastIndex(); j++) {
					System.out.printf("option %d is %s\n", j, temp.getOpts()[j]);
				}
			}
		}
		for (int i = 0; i < this.dirs2beautify.size(); i++) {
			System.out.printf("dirs to beautify: %s\n", this.dirs2beautify.get(i));
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
