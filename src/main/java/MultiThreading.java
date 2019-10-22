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
	private File[] contents;
	private Map<String, String> rawApiTVName = new HashMap();
	
	public MultiThreading(String dir) throws FileNotFoundException {
		this.directoryName = dir;
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
			if (this.contents[i].isFile()) {
				//System.out.printf("File is: %s\n", this.contents[i].getName());
				fileHandler(this.contents[i]);
			} else if (this.contents[i].isDirectory()) {
				//System.out.printf("Directory is: %s\n", this.contents[i].getName());
				recursiveWalk(this.directoryName + "/" + this.contents[i].getName());
			}
		}

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
				this.rawApiTVName.put(TvName.toLowerCase(), tv.getName());
			} else {
				tv = matchWithName(this.rawApiTVName.get(TvName));
				TvName = tv.getName();
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.printf("TV show name is: %s\n", tv.getName());	
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

	public void recursiveWalk(String rootdir ) {
		File root = new File(rootdir);

		//TvShow show = new TvShow(rootdir.split("/")[1]);
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
