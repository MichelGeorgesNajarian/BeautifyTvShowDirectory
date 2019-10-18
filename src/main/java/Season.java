package main.java;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class Season {
	
	private int season_num;
	private List<Episode> allSeasons;
	private Properties props;
	private Properties sensitive;
	private int episode_num;
	private int tv_id;
	private String full_path;
	
	public Season(int season_num) {
		this.season_num = season_num;
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
	
	public void setTvId(int id) {
		this.tv_id = id;
	}
	
	public int getTvId() {
		return this.tv_id;
	}
	
	public String getFullPath() {
		return this.full_path;		
	}

	public void setFullPath(String fullPath) {
		this.full_path = fullPath;		
	}


}
