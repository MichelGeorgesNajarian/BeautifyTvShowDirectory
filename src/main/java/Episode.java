package main.java;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Episode {
	private int episode_number;
	private String extension;
	private String episode_title;
	private String full_path;
	private Properties props;
	private Properties sensitive;
	private int tv_id;
	
	public Episode(int episodeNum) {
		this.episode_number = episodeNum;
		
		props = new Properties();
		sensitive = new Properties();
		
		try {
			props.load(BeautifyTvShow.class.getClassLoader().getResourceAsStream("application.properties"));
			sensitive.load(BeautifyTvShow.class.getClassLoader().getResourceAsStream("sensitive.properties"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int getEpisodeNumber() {
		return this.episode_number;
	}
	
	public void setEpisodeNumber(int num) {
		this.episode_number = num;
	}
		
	public String getExtension() {
		return this.extension;
	}
	
	public void setExtension(String ext) {
		this.extension = ext;
	}
	
	public String getEpisodeTitle() {
		return this.episode_title;
	}
	
	public void setEpisodeTitle(String title) {
		this.episode_title = title;
	}
	
	public String getFullPath() {
		return this.getFull_path();
	}
	
	public void setFullPath(String path) {
		this.setFull_path(path);
	}

	public String getFull_path() {
		return full_path;
	}

	public int getTvId() {
		return this.tv_id;
	}

	public void setTvId(int id) {
		this.tv_id = id;
	}
	
	public void setFull_path(String full_path) {
		this.full_path = full_path;
	}	
}
