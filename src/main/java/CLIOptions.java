package main.java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.management.InstanceNotFoundException;

public class CLIOptions {
	
	private List<Opts> allOpt = new ArrayList<Opts>(); //object with all the CLI options
	private List<String> dirs2beautify = new ArrayList<String>(); // list of all the dirs given to beautify
	private HashMap<String, Character> longNameMatchings = new HashMap<>();

	public CLIOptions(String[] args) {
		//putting all options in HashMap
		allOpt.add(new Opts('a', 0));
		allOpt.add(new Opts('l', 0));
		allOpt.add(new Opts('f', 0));
		allOpt.add(new Opts('h', 0));
		allOpt.add(new Opts('m', 0));
		allOpt.add(new Opts('d', 1));
		this.longNameMatchings.put("--append", 'a');
		this.longNameMatchings.put("--log", 'l');
		this.longNameMatchings.put("--first", 'f');
		this.longNameMatchings.put("--help", 'h');
		this.longNameMatchings.put("--move", 'm');
		this.longNameMatchings.put("--destination", 'd');
		
		for (int i = 0; i < args.length; i++) {
			if (args[i].charAt(0) == '-') { //handle option declaration
				Opts currOpt = null;
				if (args[i].charAt(1) == '-') { //handle full name declaration
					try {
						currOpt = matchOpt(this.longNameMatchings.get(args[i]));
					} catch (InstanceNotFoundException e1) {
						System.out.printf("%s is not a recognized option.\nif you need help or to see all the available options, use the -h option to open the help page\n" , args[i]);
						return;
					}
					currOpt.setValue(true);
					try {
						if (currOpt.getNumOpt() == 1) { // only do this if there is exactly 1 argument to pass for this option
							currOpt.addOpts(args[i].split("=")[1]);
						}
					} catch (IndexOutOfBoundsException e) {
						System.out.printf("Wrong format passed for %s. Expected format is:\n--option=argument\n", args[i]);
					}
				} else { //handle short notation
					try {
						currOpt = matchOpt(args[i].charAt(1));
					} catch (InstanceNotFoundException e1) {
						System.out.printf("%s is not a recognized option.\nif you need help or to see all the available options, use the -h option to open the help page\n" , args[i]);
						return;
					}
					currOpt.setValue(true);
					int temp = ++i;
					try {
						for (; i < temp + currOpt.getNumOpt(); i++) {
							if (args[i].charAt(0) == '-') { //if less arguments passed with option than required
								currOpt.setValue(false); //disable the option
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
				if (currOpt.getName() == 'h') { //requested help page, need to invalidate all other options, print helppage and exit
					for (int k = 0; k < this.allOpt.size(); k++) {
						this.allOpt.get(k).setValue(false);
					}
					currOpt.setValue(true);
					return;
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

	private Opts matchOpt(char arg) throws InstanceNotFoundException {
		for (int i = 0; i < this.allOpt.size(); i++) {
			if (this.allOpt.get(i).getName() == arg) {
				return this.allOpt.get(i);
			}
		}
		throw new InstanceNotFoundException();
	}

	public List<Opts> getAllOpt() {
		return allOpt;
	}

	public void setAllOpt(List<Opts> allOpt) {
		this.allOpt = allOpt;
	}

	public List<String> getDirs2Beautify() {
		return dirs2beautify;
	}

	public void setDirs2Beautify(List<String> dir) {
		this.dirs2beautify = dir;
	}
	
	public HashMap<String, Character> getLongNameMatchings() {
		return longNameMatchings;
	}

	public void setLongNameMatchings(HashMap<String, Character> longNameMatchings) {
		this.longNameMatchings = longNameMatchings;
	}

	public void printHelpPage() {
		System.out.printf("Help page printed\n");
	}

	public boolean helpRequested() {
		for (int i = 0; i < this.allOpt.size(); i++) {
			Opts currOpt = this.allOpt.get(i);
			if (currOpt.getName() == 'h') {
				return currOpt.isValue();
			}
		}
		return false;
	}

}
