package main.java;

import java.io.FileNotFoundException;

public class BeautifyTvShow {
	public static void main(String[] args) throws FileNotFoundException {
		Thread[] DirThread = new Thread[args.length];
		for (int i = 0; i < args.length; i++) {
			try {
				DirThread[i] = new Thread(new MultiThreading(args[i]));
				DirThread[i].start();
			} catch(FileNotFoundException e) {
				System.out.printf("\n\n!!File or Directory '%s' does not exist!!\n\n", args[i]);
			}
		}
		for (Thread individualThread : DirThread) {
			try {
				if (individualThread != null) {
					individualThread.join();
				}
			} catch (InterruptedException e) {
				System.out.printf("join failed!\n");
				e.printStackTrace();
			}
		}
		System.out.printf("All threads joined succesfully!\n");		
	}
}