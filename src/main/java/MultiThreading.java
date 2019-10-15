package main.java;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MultiThreading implements Runnable {

	private List<TvShow> allTvShows = new ArrayList<TvShow>();
	private String directoryName;
	private File directory;
	private File[] contents;
	
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
				System.out.printf("File is: %s\n", this.contents[i].getName());
				fileHandler(this.contents[i].getName());
			} else if (this.contents[i].isDirectory()) {
				System.out.printf("Directory is: %s\n", this.contents[i].getName());
				recursiveWalk(this.directoryName + "/" + this.contents[i].getName());
			}
		}

	}
	
	public void fileHandler(String fileName) {
		String filteredFileName = fileName.replaceAll("(\\s)*[\\[|\\(](\\s)*.*?(\\s)*[\\]\\)](\\s)*", "");
		String getSeasonEpisode = "(.+?)(?=([\\.|\\s|\\-]+[s|S]\\d+))|([s|S]\\d+)|([e|E]\\d+)|(\\.(?:.(?!\\.))+$)";
		Pattern seasonEpisode = Pattern.compile(getSeasonEpisode);
		Matcher m = seasonEpisode.matcher(filteredFileName);
		List<String> allMatches = new ArrayList<String>();
		while(m.find()) {
			System.out.printf("match %s\n", m.group());
			allMatches.add(m.group());
		}
		String TvName = allMatches.get(0).replaceAll("[\\.|_|\\-]", " ");
		int seasonNum = Integer.parseInt(allMatches.get(1).replaceFirst("s|S", ""));
		int episodeNum = Integer.parseInt(allMatches.get(2).replaceFirst("e|E", ""));
		String extension = allMatches.get(3);
		
		System.out.printf("Tv Show: %s Season %02d Episode %02d with extension: %s\n", TvName, seasonNum, episodeNum, extension);
	}
	
	public void recursiveWalk(String rootdir ) {
		File root = new File(rootdir);
		TvShow show = new TvShow(rootdir.split("/")[1]);
	}
	
	public boolean isExisting(TvShow tv) {
		for (int i = 0; i < this.allTvShows.size(); i++) {
			if(tv.getName().equals(this.allTvShows.get(i).getName())) {
				return true;
			}
		}
		return false;
	}

}
