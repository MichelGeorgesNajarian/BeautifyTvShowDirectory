package main.java;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class TvShow {
	private String name;
	private int numSeasons;
	private int tv_id;
	private List<Season> allSeasons;
	private Properties props;
	private Properties sensitive;
	private String fullPath;
	
	public TvShow(String name) throws IOException {
		sun.util.logging.PlatformLogger.getLogger("sun.net.www.protocol.http.HttpURLConnection").setLevel(sun.util.logging.PlatformLogger.Level.ALL);
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
		this.name = name;
		URL getTvInfoUrl = null;
		try {
			//getting url to make get request and find tv show
			getTvInfoUrl = new URL(props.getProperty("api.searchtv.url"));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpURLConnection con = (HttpURLConnection) getTvInfoUrl.openConnection();
		con.setRequestMethod("GET");
		
		//putting the request params in a hashmap
		Map<String, String> params = new HashMap<>();
		params.put("api_key", this.sensitive.getProperty("api.key"));
		params.put("query", this.name);
		con.setDoOutput(true);
		DataOutputStream out = new DataOutputStream(con.getOutputStream());
		out.writeBytes(QueryBuilder.formatParams(params));
		out.flush();
		out.close();
		
		int responseCode = con.getResponseCode();
		System.out.printf("Response of '%s' request is %d\nError is: %s", con.getRequestMethod(), responseCode, con.getErrorStream().toString());
        
		//TODO get request to the tv show id, num of seasons and name
		//setName(name result from get request);
		//setNumSeason(number of seasons);
		//setTvId(tv id value from response of get request);
		
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String newName) {
		this.name = newName;
	}
	
	public int getNumSeasons() {
		return this.numSeasons;
	}
	
	public void setNumSeason(int num) {
		this.numSeasons = num;
	}
	
	public int getTvId() {
		return this.tv_id;
	}
	
	public void setTvId(int num) {
		this.tv_id = num;
	}
	
	public String getFullPath() {
		return this.fullPath;
	}
	
	public void setFullPath(String path) {
		this.fullPath = path;
	}
	
	public void createSeason(int season_num) {
		Season newSeason = new Season(season_num);
		this.allSeasons.add(newSeason);
		newSeason.setTvId(this.tv_id);
		newSeason.setFullPath(this.fullPath);
	}
	
	
}
