package main.java;

public class Opts {
	private char name;
	private int numOpt;
	private int lastIndex;
	private String[] opts;
	private boolean value;
	
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
