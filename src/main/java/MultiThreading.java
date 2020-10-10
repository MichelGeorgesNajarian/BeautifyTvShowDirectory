package main.java;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.management.InstanceNotFoundException;

import org.json.JSONObject;

public class MultiThreading implements Runnable, ANSIColors {

	private List<TvShow> allTvShows = new ArrayList<TvShow>();
	private String directoryName;
	private File directory;
	private Map<String, String> rawApiTVName = new HashMap<>();
	private CLIOptions opts;
	
	public MultiThreading(CLIOptions opt, int i) throws FileNotFoundException {
		this.opts = opt;
		this.directoryName = opt.getDirs2Beautify().get(i);
		this.directory = new File(this.directoryName);	
		if (!this.directory.exists()) {
			throw new FileNotFoundException(ANSI_RED + "Directory " + this.directoryName + " does not exist\n" + ANSI_RESET);
		}
	}
	
	@Override
	public void run() {
		recursiveWalk(directory);
		renameFiles();
	}

	public void fileHandler(File fileName) {
		try {
			if (this.opts.matchOpt('v').isValue()) System.out.printf("Found video file %s\n", fileName.getName());
		} catch (InstanceNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.printf(ANSI_RED + "An unknown error just occured with -v\n" + ANSI_RESET);
		}
		String filteredFileName = fileName.getName().replaceAll("(\\s)*[\\[|\\(](\\s)*.*?(\\s)*[\\]\\)](\\s)*", ""); //remove any garbage unwanted patterns, keep only relevant info
		String getSeasonEpisode = "(.+?)(?=([\\.|\\s|\\-|\\_]+[s|S]\\d+))|([s|S]\\d+)|([e|E]\\d+)|((?:.(?!\\.))+$)"; // get the season and episode numbers
		Pattern seasonEpisode = Pattern.compile(getSeasonEpisode);
		Matcher m = seasonEpisode.matcher(filteredFileName);
		List<String> allMatches = new ArrayList<String>();
		while(m.find()) {
			//System.out.printf("match %s\n", m.group());
			allMatches.add(m.group());
		}
		String TvName = null;
		int seasonNum = 0;
		int episodeNum = 0;
		String extension = null;
		try {
			TvName = allMatches.get(0).replaceAll("[\\.|_|\\-]", " "); //match and get the tv show name
			seasonNum = Integer.parseInt(allMatches.get(1).replaceFirst("s|S", "")); // get the season num
			episodeNum = Integer.parseInt(allMatches.get(2).replaceFirst("e|E", "")); // get the episode num
			extension = allMatches.get(3); //get the extension
		} catch(IndexOutOfBoundsException e) { //catch happens if no matches were found IE file is not formatted in a way to extract the info needed
			System.out.printf(ANSI_RED + "!!Error!!\n%s is not in a valid TV show naming format.\n"
					+ "Unable to get Tv Show name, Season number or the Episode number Format the file '%s' at this path: '%s' correctly and try again.\n"
					+ ANSI_RESET, fileName.getName(), fileName.getName(), fileName.getAbsolutePath());
			return;
		}
		
		TvShow tv = null;
		try {
			if((tv = matchWithName(this.rawApiTVName.get(TvName))) == null) { // if null then first time seeing a specific pattern in filename
				tv = new TvShow(TvName.toLowerCase(), opts); //create TV show object, api call happens in there, matches with actual TV show
				tv = matchTvShowObject(tv); // match with TV show if it already existed before but with different naming pattern
				if (tv.getTvId() == 0) {
					return; //TV show not found on API nothing left to do
				}
				this.rawApiTVName.put(TvName.toLowerCase(), tv.getName()); // add file pattern to hashmap so that we can match with future files with same naming pattern 
			} else {
				//match with the TV show object, since pattern was seen before 
				tv = matchWithName(this.rawApiTVName.get(TvName)); 
				TvName = tv.getName(); //updtae to correct name from API
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.printf("TV show name is: %s\n", tv.getName());	
		this.rawApiTVName.put(TvName, tv.getName()); //update name hashmap
		tv.setFullPath(fileName.getAbsolutePath());
		try {
			tv.createNewSeason(seasonNum).generateEpisode(episodeNum).setExtension(extension); //start handling seasons and episodes
		} catch (Exception e) {
			
		}
		
		try {
			if (opts.matchOpt('v').isValue()) System.out.printf("Tv Show: %s Season %02d Episode %02d with extension: %s\n", TvName, seasonNum, episodeNum, extension);
		} catch (InstanceNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.printf(ANSI_RED + "An unknown error just occured with -v\n" + ANSI_RESET);
		}
	}
	
	//function that matches TV show object with parameter passed tvName
	private TvShow matchWithName(String tvName) {
		for (int i = 0; i < this.allTvShows.size(); i++) {
			if (this.allTvShows.get(i).getName() == tvName) {
				return this.allTvShows.get(i);
			}
		}
		return null; //TV show object not found
	}

	//recursively go through all subdirectories and handle any valid file  
	public void recursiveWalk(File rootdir) {
		try {
			if (opts.matchOpt('v').isValue()) System.out.printf("Using directory: %s\n", rootdir.getName());
		} catch (InstanceNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.printf(ANSI_RED + "An unknown error just occured with -v\n" + ANSI_RESET);
		}
//		System.out.printf("Using directory: %s\n", rootdir.getName());
		for (File file : rootdir.listFiles()) {
			if (file.isFile()) {
				String fileType;
				try {
					fileType = Files.probeContentType(file.toPath()); //finding the type of files
					if (fileType != null && fileType.startsWith("video")) fileHandler(file); //handle file if it is a avideo file
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.printf(ANSI_RED + "An error happened when trying to find the type of file %s\nSkipping it\n" + ANSI_RESET, file.getName());
				}
			} else if (file.isDirectory()) {// if it's a directory, go through it and beautify it
				recursiveWalk(file);
			}
		}
	}
	
	//rename all files with pretty name
	private void renameFiles() {
		// TODO Auto-generated method stub
//		System.out.print(this.allTvShows);
		File resultDir = null; //resulting directory
		boolean log = false; 
		try {
			log = opts.matchOpt('l').isValue(); //returns true if -l was one of the options
			if (opts.matchOpt('d').isValue()) { //is true if destination directory was specified
					resultDir = new File(opts.matchOpt('d').getOpts()[0]); //open resultant directory
			} else {
				resultDir = new File (this.directory.getAbsolutePath() + "/beautified"); //default directory if nothing was selected
			}
		} catch (InstanceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (!resultDir.exists()) resultDir.mkdir(); //create directory if it doesn't exist
		File logFile = null;
		if (log) { // if logging is desired, create the log file inside the resultant directory
			logFile = new File(resultDir.getAbsoluteFile() + "/BEAUTIFY.LOG");
			try {
				logFile.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				System.out.printf(ANSI_RED + "An error occured when creating logfile BEATUIFY.LOG in directory %s\n"
						+ "Make sure that you have the right permissions and try again\n" + ANSI_RESET, logFile.getParent());
				return;
			}
		}
		FileWriter logEvents = null;
		JSONObject logJSON = null; // log will be in JSON format then saved to file
		try {
			if (log) {
				logEvents = new FileWriter(logFile.getAbsolutePath());
				List<Opts> allOpts = opts.getAllOpt();
				logJSON = new JSONObject();
				DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
				LocalDateTime now = LocalDateTime.now();
				logJSON.put("date", dtf.format(now)); //first item in LOG is the date
				JSONObject optsJSON = new JSONObject();
				for (Opts opt : allOpts) {
					optsJSON.put(Character.toString(opt.getName()), opt.isValue());
				}
				//put option as key and JSON array of the opts and their vals as value
				logJSON.put("options", optsJSON); 
				/*
				 * ie:
				 * "options": {
    			 * 				"append": "true",
    			 *				"destination": "/path/to/dest",
    			 *				"first": "true",
    			 *				"help": "false",
    			 *				"log": "true",
    			 *				"undo": "true",
    			 *				"verbose": "false"
  				 *	}
				 * */
			}
			
			for (TvShow show : this.allTvShows) {
				if (show.getAllSeasons().size() > 0) { //if successfully matched with seasons
					File currTV = new File(resultDir.getAbsolutePath() + "/" + show.getName()); //create directory name Season X (x is season num)
					if (!currTV.exists()) currTV.mkdir();
					for (Season s : show.getAllSeasons()) {
						if (s.getAllEpisodes().size() > 0) { // if successfully matched with episodes
							File currSeason = new File(currTV.getAbsolutePath() + "/" + "Season " + s.getSeasonNum());
							if (!currSeason.exists()) currSeason.mkdir();
							for (Episode e : s.getAllEpisodes()) {
								File newEpisode = new File(currSeason.getAbsolutePath() + "/" + e.getFormattedName());
								File oldEpisode = new File(e.getFull_path());
								if (log) {
									JSONObject entry = new JSONObject();
									entry.put("old", oldEpisode.getAbsolutePath());
									entry.put("new", newEpisode.getAbsolutePath());
									logJSON.append("beautified", entry);
								}
								oldEpisode.renameTo(newEpisode); //rename old episode to new one
								try {
									if (this.opts.matchOpt('v').isValue()) {
										if (newEpisode.exists()) System.out.printf(ANSI_GREEN + "%s created successfully\n" + ANSI_RESET, e.getFormattedName());
										else System.out.printf(ANSI_RED + "%s created successfully\n" + ANSI_RESET, e.getFormattedName());
									}
								} catch (InstanceNotFoundException e1) {
									// TODO Auto-generated catch block
									System.out.printf(ANSI_RED + "An unknown error just occured with -v\n" + ANSI_RESET);
								}
//								if (log) logEvents.write(oldEpisode.getAbsolutePath() + "|" + newEpisode.getAbsolutePath() + "\n");
							}
						}
					}
				}
			}
			if (log) {
				logEvents.write(logJSON.toString());
				logEvents.close(); //close log file
			}
		} catch (IOException e1) {
			System.out.printf(ANSI_RED + "An error occured when trying to write to logfile BEATUIFY.LOG in directory %s\n"
					+ "Make sure that you have the right permissions and try again\n" + ANSI_RESET, logFile.getParent());
			return;
		}
	}
	
	//match with the TV show object
	public TvShow matchTvShowObject(TvShow tv) {
		for (int i = 0; i < this.allTvShows.size(); i++) {
			if(tv.getTvId() == this.allTvShows.get(i).getTvId()) {
				return this.allTvShows.get(i);
			}
		}
		this.allTvShows.add(tv);
		return tv;
	}

}
