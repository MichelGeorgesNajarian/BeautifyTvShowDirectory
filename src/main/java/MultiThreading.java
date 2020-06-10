package main.java;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MultiThreading implements Runnable {

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
			throw new FileNotFoundException("Directory " + this.directoryName + " does not exist\n");
		}
	}
	
	@Override
	public void run() {
		recursiveWalk(directory);
		renameFiles();
	}

	public void fileHandler(File fileName) {
		String filteredFileName = fileName.getName().replaceAll("(\\s)*[\\[|\\(](\\s)*.*?(\\s)*[\\]\\)](\\s)*", "");
		String getSeasonEpisode = "(.+?)(?=([\\.|\\s|\\-]+[s|S]\\d+))|([s|S]\\d+)|([e|E]\\d+)|(\\.(?:.(?!\\.))+$)";
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
			TvName = allMatches.get(0).replaceAll("[\\.|_|\\-]", " ");
			seasonNum = Integer.parseInt(allMatches.get(1).replaceFirst("s|S", ""));
			episodeNum = Integer.parseInt(allMatches.get(2).replaceFirst("e|E", ""));
			extension = allMatches.get(3);
		} catch(IndexOutOfBoundsException e) {
			System.out.printf("!!Error!!\n%s is not in a valid TV show naming format.\nUnable to get Tv Show name, Season number or the Episode number Format the file '%s' at this path: '%s' correctly and try again.\n", fileName.getName(), fileName.getName(), fileName.getAbsolutePath());
			return;
		}
		
		TvShow tv = null;
		try {
			if((tv = matchWithName(this.rawApiTVName.get(TvName))) == null) {
				tv = new TvShow(TvName.toLowerCase());
				tv = matchTvShowObject(tv);
				if (tv.getTvId() == 0) {
					return;
				}
				this.rawApiTVName.put(TvName.toLowerCase(), tv.getName());
			} else {
				tv = matchWithName(this.rawApiTVName.get(TvName));
				TvName = tv.getName();
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.printf("TV show name is: %s\n", tv.getName());	
		this.rawApiTVName.put(TvName, tv.getName());
		tv.setFullPath(fileName.getAbsolutePath());
		tv.createNewSeason(seasonNum).generateEpisode(episodeNum).setExtension(extension);
		
		//System.out.printf("Tv Show: %s Season %02d Episode %02d with extension: %s\n", TvName, seasonNum, episodeNum, extension);
	}
	
	private TvShow matchWithName(String tvName) {
		for (int i = 0; i < this.allTvShows.size(); i++) {
			if (this.allTvShows.get(i).getName() == tvName) {
				return this.allTvShows.get(i);
			}
		}
		return null;
	}

	public void recursiveWalk(File rootdir) {
		System.out.printf("Using directory: %s\n", rootdir.getName());
		for (File file : rootdir.listFiles()) {
			if (file.isFile()) {
				fileHandler(file);
			} else if (file.isDirectory()) {
				recursiveWalk(file);
			}
		}
	}
	
	private void renameFiles() {
		// TODO Auto-generated method stub
//		System.out.print(this.allTvShows);
		for (TvShow show : this.allTvShows) {
			
		}
	}
	
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
