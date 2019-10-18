package main.java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import org.json.JSONObject;

public class TvShow {
	private String name;
	private int numSeasons;
	private int tv_id;
	private List<Season> allSeasons;
	private Properties props;
	private Properties sensitive;
	private String fullPath;
	
	public TvShow(String name) throws IOException {
		//sun.util.logging.PlatformLogger.getLogger("sun.net.www.protocol.http.HttpURLConnection").setLevel(sun.util.logging.PlatformLogger.Level.ALL);
		this.props = new Properties();
		this.sensitive = new Properties();
		try {
			this.props.load(BeautifyTvShow.class.getClassLoader().getResourceAsStream("application.properties"));
			this.sensitive.load(BeautifyTvShow.class.getClassLoader().getResourceAsStream("sensitive.properties"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.name = name;
		getInfoApi();
	}
	
	public void getInfoApi() throws IOException {
		URL getTvInfoUrl = null;
		try {
			//putting the request params in a hashmap
			Map<String, String> params = new HashMap<>();
			params.put("api_key", this.sensitive.getProperty("api.key"));
			params.put("query", this.name);
			params.put("page", "1");
			//getting url to make get request and find tv show
			getTvInfoUrl = new URL(
					QueryBuilder.buildURL(
							props.getProperty("api.searchtv.url"), 
							QueryBuilder.formatParams(params)
					)
			);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpURLConnection con = (HttpURLConnection) getTvInfoUrl.openConnection();
		//GET request
		
		con.setRequestMethod("GET");
		//request header JSON
		con.setRequestProperty("Content-Type", "application/json");
		//timeout after 10 seconds for both connect and read
		con.setConnectTimeout(10000);
		con.setReadTimeout(10000);
		StringBuilder content;

        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()))) {
        	//JSONObject results
        	String line;
        	content = new StringBuilder();
        	while ((line = in.readLine()) != null) {
        		content.append(line);
                content.append(System.lineSeparator());
            }
        } finally {
            con.disconnect();
        }
        //System.out.println(content.toString());
        JSONObject results = new JSONObject(content.toString());
        int page = results.getInt("page");
        
        if(results.getInt("total_results") == 1) {
        	JSONObject temp = (JSONObject) results.getJSONArray("results").get(0);
        	this.name = temp.getString("name");
        	this.tv_id = temp.getInt("id");
        } else if (results.getInt("total_results") == 0) {
        	System.out.printf("No results were found for a TV show with the name of '%s'\n", this.name);
        	return;
        } else {
        	multipleResults(results);
        	if(this.tv_id == 0) {
        		return;
        	}
        }
        
        System.out.printf("Matched TV show is '%s' with id: %d\n", this.name, this.tv_id);
        
		//System.out.printf("Response of '%s' request is %d\n", con.getRequestMethod(), responseCode);
        
		//TODO get request to the tv show id, num of seasons and name
		//setName(name result from get request);
		//setNumSeason(number of seasons);
		//setTvId(tv id value from response of get request);
	}
	
	public void multipleResults(JSONObject results) {
		JSONObject temp = (JSONObject) results.getJSONArray("results").get(0);
    	System.out.printf("There are multiple possibilities when matching a TV show with the name '%s'\nIs it '%s' which started airing on %s?\n(yes or no)\n", this.name, temp.getString("name"), temp.getString("first_air_date"));
    	Scanner inp = new Scanner(System.in);
    	String cin = inp.nextLine();
    	cin.toLowerCase();
    	if (cin.equals("y") || cin.equals("yes") || cin.equals("ye")) {
    		this.name = temp.getString("name");
        	this.tv_id = temp.getInt("id");
    	} else {
    		System.out.printf("Please be more specific in the name of your file and try again\nHere are the top %d results:\n", results.getInt("total_results")/results.getInt("total_pages"));
    		for (int i = 0; i < results.getInt("total_results")/results.getInt("total_pages"); i++) {
    			JSONObject tempList = (JSONObject) results.getJSONArray("results").get(i);
    			System.out.printf("%d) %s (started airing: %s)\nOverview: %s\n\n--\n\n", i+1, tempList.getString("name"), tempList.getString("first_air_date"), tempList.getString("overview"));
    		}
    		System.out.printf("\n\n----------------------------------------------------\n\nSelect the most suitable match (%d-%d):\n", 1, results.getInt("total_results")/results.getInt("total_pages"));
    		int selectMatch = 0;
    		try {
    			selectMatch = inp.nextInt();
    			if (selectMatch > results.getInt("total_results")/results.getInt("total_pages") || selectMatch <= 0) {
    				throw new InputMismatchException();
    			}
    		} catch(InputMismatchException e) {
    			System.out.printf("Wrong input entered\nSkipping file...\n\n");
    			return;
    		}
    		temp = (JSONObject) results.getJSONArray("results").get(selectMatch - 1);
    		this.name = temp.getString("name");
        	this.tv_id = temp.getInt("id");
    		return;
    	}
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
