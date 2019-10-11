package main.java;

import java.io.File;
import java.io.FileNotFoundException;

public class MultiThreading implements Runnable {

	private String directoryName;
	private File directory;
	private File[] contents;
	
	public MultiThreading(String dir) throws FileNotFoundException {
//		if(dir.equals("~")) {
//			this.directoryName = System.getProperty("user.home");
//		} else {
			this.directoryName = dir;
//		}
		this.directory = new File(this.directoryName);	
		if (this.directory.exists()) { 
			this.contents = this.directory.listFiles();
		} else {
			throw new FileNotFoundException("Directory " + this.directoryName + " does not exist");
		}
	}
	
	@Override
	public void run() {
		System.out.printf("using directory: %s\n", this.directoryName);
		for (int i = 0; i < this.contents.length; i++) {
			System.out.printf("File is: %s\n", this.contents[i].getName());
		}

	}

}
