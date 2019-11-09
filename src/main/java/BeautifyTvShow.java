package main.java;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;


public class BeautifyTvShow {
		
	public static void main(String[] args) throws FileNotFoundException {
		Thread[] DirThread = new Thread[args.length];
		CLIOptions opt = new CLIOptions(args);
		return;
//		for (int i = 0; i < args.length; i++) {
//			try {
//				DirThread[i] = new Thread(new MultiThreading(args[i]));
//				DirThread[i].start();
//			} catch(FileNotFoundException e) {
//				System.out.printf("\n\n!!File or Directory '%s' does not exist!!\n\n", args[i]);
//			}
//		}
//		for (Thread individualThread : DirThread) {
//			try {
//				if (individualThread != null) {
//					individualThread.join();
//				}
//			} catch (InterruptedException e) {
//				System.out.printf("join failed!\n");
//				e.printStackTrace();
//			}
//		}
//		System.out.printf("All threads joined succesfully!\n");
//		Properties props = new Properties();
//		Properties sensitive = new Properties();
//		try {
//			props.load(BeautifyTvShow.class.getClassLoader().getResourceAsStream("application.properties"));
//			sensitive.load(BeautifyTvShow.class.getClassLoader().getResourceAsStream("sensitive.properties"));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		//System.out.printf("api url: %s\napi key: %s\n", props.getProperty("api.searchtv.url"), sensitive.getProperty("api.key"));
	}
}