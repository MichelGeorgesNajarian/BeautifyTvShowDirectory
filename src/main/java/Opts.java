package main.java;

public class Opts {
	private char name; //name of the option EG help
	private int numOpt; // max number of options the option accepts (might have less)
	private int lastIndex; // if multiple options last index is to keep track of the last index if there are less items in opts array than the max allowed
	private String[] opts; // Array of all the options being passed
	private boolean value; // boolean to see if option is active or not
	
	public Opts(char name, int num) {
		this.value = false; //false by default, becomes true if option is passed in command line
		this.name = name;
		this.numOpt = num;
		this.setLastIndex(0);
		opts = new String[this.numOpt];
	}
	
	public char getName() {
		return name;
	}
	public void setName(char name) {
		this.name = name;
	}
	public int getNumOpt() {
		return numOpt;
	}
	public void setNumOpt(int numOpt) {
		this.numOpt = numOpt;
	}
	public int getLastIndex() {
		return lastIndex;
	}

	public void setLastIndex(int lastIndex) {
		this.lastIndex = lastIndex;
	}

	public String[] getOpts() {
		return opts;
	}
	public void setOpts(String[] opts) {
		this.opts = opts;
	}
	public void addOpts(String newopts) {
		this.opts[this.getLastIndex()] = newopts; // adding new element as last of the array
		this.setLastIndex(this.getLastIndex() + 1); //updating max index
	}

	public boolean isValue() {
		return value;
	}

	public void setValue(boolean value) {
		this.value = value;
	}
}
