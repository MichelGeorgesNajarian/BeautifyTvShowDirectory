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
		// Creating all of the opt objects with the options that are supported
		allOpt.add(new Opts('a', 0));
		allOpt.add(new Opts('l', 0));
		allOpt.add(new Opts('f', 0));
		allOpt.add(new Opts('h', 0));
		allOpt.add(new Opts('m', 0));
		allOpt.add(new Opts('d', 1));
		allOpt.add(new Opts('u', 1)); // pass log file as argument and will undo any changes that were done
		//matching the option's long name and short name AKA --help is the same as -h
		this.longNameMatchings.put("--append", 'a');
		this.longNameMatchings.put("--log", 'l');
		this.longNameMatchings.put("--first", 'f');
		this.longNameMatchings.put("--help", 'h');
		this.longNameMatchings.put("--move", 'm');
		this.longNameMatchings.put("--destination", 'd');
		this.longNameMatchings.put("--undo", 'u');
		//expected options are: --destination=/path/to/dest/
		//other options for those that do not accept options is: --first=true OR --first=false
		//finally if multiple options are allowed: --name=opt1,opt2,opt3
		for (int i = 0; i < args.length; i++) {
			if (args[i].charAt(0) == '-') { //handle option declaration
				Opts currOpt = null;
				if (args[i].charAt(1) == '-') { //handle full name declaration
					try {
						currOpt = matchOpt(this.longNameMatchings.get(args[i].split("=")[0])); //matching with the correct opt object split in case of "=" of --destination
					} catch (InstanceNotFoundException e1) {
						System.out.printf("%s is not a recognized option.\nif you need help or to see all the available options, use the -h option to open the help page\n" , args[i]);
						return;
					}
					currOpt.setValue(true); //set the opt object as active
					for (int j = 0; j < currOpt.getNumOpt(); j++) {
						currOpt.addOpts(args[i].split("=")[1]);
					} 
					try {
						if (currOpt.getNumOpt() == 1) { // only do this if there is exactly 1 argument to pass for this option
							currOpt.addOpts(args[i].split("=")[1]);
						} else if (currOpt.getNumOpt() == 0) {
							String[] temp = args[i].split("=");
							if (temp.length > 1) {
								currOpt.setValue(Boolean.getBoolean(temp[1]));
							} else {
								currOpt.setValue(true);
							}
							
						} else {
							String[] options = args[i].split("=")[1].split(",");
							if (options.length == currOpt.getNumOpt()) {
								currOpt.setOpts(options); //setting the options
								currOpt.setLastIndex(options.length); //updating the last index
							} else { //else if wrong number of arguments passed
								throw new IndexOutOfBoundsException();
							}
						}
					} catch (IndexOutOfBoundsException e) {
						System.out.printf("Wrong format passed for %s.\nExpected format is:\n--option=argument1,argument2,argument3\n", args[i]);
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
				if (currOpt.getName() == 'h') { //requested help page, need to invalidate all other options, print help page and exit
					for (int k = 0; k < this.allOpt.size(); k++) {
						this.allOpt.get(k).setValue(false); //setting all other options as false
					}
					currOpt.setValue(true); //setting help requested option as true
					return; //returning as nothing else needs to be done
				}
			} else { //case of no longer reading options, then add them as directories to beautify
				dirs2beautify.add(args[i]);
			}
		} //printing parsed command line arguments
//		for (int i = 0; i < this.allOpt.size(); i++) {
//			Opts temp = this.allOpt.get(i);
//			System.out.printf("option %d : %c | active/not active: %b | number of options: %d\n", i, temp.getName(), temp.isValue(), temp.getLastIndex());
//			if (temp.getLastIndex() != 0) {
//				for (int j = 0; j < temp.getLastIndex(); j++) {
//					System.out.printf("option %d is %s\n", j+1, temp.getOpts()[j]);
//				}
//			}
//		}//printing the parsed directories to beautify
//		for (int i = 0; i < this.dirs2beautify.size(); i++) {
//			System.out.printf("dirs to beautify: %s\n", this.dirs2beautify.get(i));
//		}
	}
	//function which matches the option passed with the correct opt object
	public Opts matchOpt(char arg) throws InstanceNotFoundException {
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
		System.out.printf("Usage: BeautifyTvShow [--Options -o] directory1/ directory2/ directory3/\n"
				+ "Options are:\n"
				+ "\t--append / -a: append \"_old\" at the end of the old file name\n"
				+ "\t--log / -l: log all the changes done in file BeautifyTvShow.log in the current directory\n"
				+ "\t--first / -f: always match with the first result from the API\n"
				+ "\t--help / -h: print this page\n"
				+ "\t--move / -m: do NOT keep the old file, just the new file\n"
				+ "\t--destination / -d: directory where the beautified files are to be put. EG --destination=TV OR -d TV\n"
				+ "\t\t if no destination directory is given, then the results will be in the default directory named 'beautified'\n"
				+ "\t--undo / -u: undo any changes that were done by using logfile generated by using the option -l. EG --undo=BeautifyTvShow.log OR -u BeautifyTvShow.log\n"
				+ "\t             When selecting the undo option, no beautification will happen and a log file UNDO.log will be generated\n"
                + "directory1/ directory2/ directory3/ are the directories to beautify\n\n"
				+ "For additional help or issues, please visit GitHub page: https://github.com/MichelGeorgesNajarian/BeautifyTvShowDirectory and submit a new issue\n");
	}
	//if help is requested print help page and stop the program
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
