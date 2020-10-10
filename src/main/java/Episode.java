package main.java;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.management.InstanceNotFoundException;

public class Episode implements ANSIColors {
	private int episode_number;
	private String extension;
	private String episode_title;
	private String full_path;
	private Properties props;
	private Properties sensitive;
	private int tv_id;
	private String TV_name;
	private int season_num;
	private String formatted_name;
	
	public Episode(int episodeNum) {
		this.setEpisodeNumber(episodeNum);
		
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
		return episode_number;
	}

	public void setEpisodeNumber(int episode_number) {
		this.episode_number = episode_number;
	}



	public String getExtension() {
		return this.extension;
	}
	
	public void setExtension(String ext) {
		this.extension = ext;
		createFormattedName();
	}
	
	public String getFullPath() {
		return this.getFull_path();
	}
	
	public void setFullPath(String path) {
		this.setFull_path(path);
	}

	public int getTvId() {
		return this.tv_id;
	}

	public void setTvId(int id) {
		this.tv_id = id;
	}
	
	public String getTvName() {
		return this.TV_name;
	}
	
	public void setTvName(String name) {
		this.TV_name = name;
	}
	
	public int getSeasonNum() {
		return this.season_num;
	}

	public void setSeasonNum(int num) {
		this.season_num = num;
	}
	
	public String getFull_path() {
		return this.full_path;
	}
	
	public void setFull_path(String full_path) {
		this.full_path = full_path;
	}
	
	public void createFormattedName() {
		this.episode_title = this.episode_title.replaceAll("[\\/:*?\"<>|]", "");
		this.formatted_name = String.format("%s S%02dE%02d - %s%s", this.TV_name, this.season_num, this.getEpisodeNumber(), this.getEpisodeTitle(), this.extension);
	}
	
	public String getFormattedName() {
		return this.formatted_name;
	}
	
	public void setFormattedName(String formattedName) {
		this.formatted_name = formattedName;
	}

	public String getEpisodeTitle() {
		return episode_title;
	}

	public void setEpisodeTitle(String episode_title) {
		this.episode_title = episode_title;
	}
}
