package main.java;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import javax.management.InstanceNotFoundException;

import org.json.JSONArray;
import org.json.JSONObject;


public class BeautifyTvShow implements ANSIColors {
		
	public static void main(String[] args) throws FileNotFoundException, InstanceNotFoundException {
		Thread[] DirThread = new Thread[args.length];
		CLIOptions opt = new CLIOptions(args);
		if (opt.helpRequested()) {
			opt.printHelpPage();
			return;
		}
//		if (opt.getAllOpt().get(0).isValue() && opt.getAllOpt().get(4).isValue()) {
//			System.out.printf(ANSI_RED + "\n\n!!Options 'append' and 'move' cannot both be selected together.\n"
//					+ "Check the manual page to see how the options work.\n\n" + ANSI_RESET);
//			opt.printHelpPage();
//			return;
//		}
		if (opt.matchOpt('u').isValue() && (opt.getNumOptTrue() == 1 || opt.matchOpt('v').isValue() && opt.getNumOptTrue() == 2)) { //either undo only or undo and verbose
			undoBeautification(opt.matchOpt('u'), opt.matchOpt('v'));
			return;
		}
		if (opt.matchOpt('d').isValue()) {
			File resDir = new File(opt.matchOpt('d').getOpts()[0]); //getting the result directory if selected
			if (!resDir.exists()) {
				if (!resDir.mkdir()) {
					System.out.printf(ANSI_RED + "An error occured while creating the directory %s.\nExiting...\n" + ANSI_RESET, opt.getAllOpt().get(5).getOpts()[0]);
				}
				if (opt.matchOpt('v').isValue()) {
					System.out.printf("Created directory %s\n", resDir.getPath());
				}
			} else {
				if (opt.matchOpt('v').isValue()) {
					System.out.printf("Using existing directory %s\n", resDir.getPath());
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
		if (opt.matchOpt('v').isValue()) {
			System.out.printf(ANSI_GREEN + "All threads joined succesfully!\n" + ANSI_RESET);
		}
		
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
	
	@SuppressWarnings("unchecked")
	public static void undoBeautification(Opts opt, Opts v) { //function that undoes any changes that were done, by providing LOG file
		for (String logfiles : opt.getOpts()) {
			File log = new File(logfiles);
			if (!log.exists()) { //wrong log file passed
				System.out.printf(ANSI_RED + "Logfile %s does not exist.\nSkipping it...\n" + ANSI_RESET, log.getName());
			} else {
				Scanner reader = null;
				try {
					reader = new Scanner(log);
					StringBuilder logs = new StringBuilder();
					while (reader.hasNextLine()) {
						logs.append(reader.nextLine()); //read entire logfile
					}
					JSONObject jsonLOG = new JSONObject(logs.toString()); //parse as JSON
					List<JSONObject> beautified = new ArrayList<>();
					JSONArray jarr = (JSONArray) jsonLOG.get("beautified");
					for (int i = 0; i < jarr.length(); i++) {
						beautified.add((JSONObject)jarr.get(i));
					}
					for (JSONObject j : beautified) {
						File old = new File(j.getString("old"));
						File nw = new File(j.getString("new"));
						nw.renameTo(old);
						if (v.isValue()) {
							System.out.printf("Undid beautification for %s to %s\n", nw.getName(), old.getName());
						}
					}
					if (v.isValue()) {
						System.out.printf("Undo done\n");
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					System.out.printf(ANSI_RED + "An error occured when reading logfile %s\nVerify integrity of file and try again\n" + ANSI_RESET, log.getAbsolutePath());
				}
			}
		}
	}
}