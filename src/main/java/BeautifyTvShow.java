package main.java;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;


public class BeautifyTvShow implements ANSIColors {
		
	public static void main(String[] args) throws FileNotFoundException {
		Thread[] DirThread = new Thread[args.length];
		CLIOptions opt = new CLIOptions(args);
		if (opt.helpRequested()) {
			opt.printHelpPage();
			return;
		}
		if (opt.getAllOpt().get(0).isValue() && opt.getAllOpt().get(4).isValue()) {
			System.out.printf(ANSI_RED + "\n\n!!Options 'append' and 'move' cannot both be selected together.\n"
					+ "Check the manual page to see how the options work.\n\n" + ANSI_RESET);
			opt.printHelpPage();
			return;
		}
		if (opt.getAllOpt().get(5).isValue()) {
			File resDir = new File(opt.getAllOpt().get(5).getOpts()[0]); //getting the result directory if selected
			if (!resDir.exists()) {
				if (!resDir.mkdir()) {
					System.out.printf(ANSI_RED + "An error occured while creating the directory %s.\nExiting...\n" + ANSI_RESET, opt.getAllOpt().get(5).getOpts()[0]);
				}
			}
		}
		for (int i = 0; i < opt.getDirs2Beautify().size(); i++) {
			try {
				DirThread[i] = new Thread(new MultiThreading(opt, i));
				DirThread[i].start();
			} catch(FileNotFoundException e) {
				System.out.printf(ANSI_RED + "\n\n!!File or Directory '%s' does not exist!!\n\n" + ANSI_RESET, args[i]);
			}
		}
		for (Thread individualThread : DirThread) {
			try {
				if (individualThread != null) {
					individualThread.join();
				}
			} catch (InterruptedException e) {
				System.out.printf(ANSI_RESET + "join failed!\n" + ANSI_RESET);
				e.printStackTrace();
			}
		}
		System.out.printf(ANSI_GREEN + "All threads joined succesfully!\n" + ANSI_RESET);
		Properties props = new Properties();
		Properties sensitive = new Properties();
		try {
			props.load(BeautifyTvShow.class.getClassLoader().getResourceAsStream("application.properties"));
			sensitive.load(BeautifyTvShow.class.getClassLoader().getResourceAsStream("sensitive.properties"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.printf("api url: %s\napi key: %s\n", props.getProperty("api.searchtv.url"), sensitive.getProperty("api.key"));
	}
}