package main.java;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class TvShow {
	private String name;
	private int numSeasons;
	private int tv_id;
	private List<Season> allSeasons;
	private Properties props;
	private Properties sensitive;
	private String fullPath;
	
	public TvShow(String name) {
		props = new Properties();
		sensitive = new Properties();
		try {
			props.load(new FileInputStream("./bin/main/resources/application.properties"));
			sensitive.load(new FileInputStream("./bin/main/resources/sensitive.properties"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.printf("new tvshow is: %s\n", name);
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String newName) {
		this.name = newName;
	}
}
