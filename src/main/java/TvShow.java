package main.java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.*;

import javax.management.InstanceNotFoundException;

import org.json.JSONException;
import org.json.JSONObject;

public class TvShow implements ANSIColors {
	private String name;
	private int numSeasons;
	private int tv_id;
	private List<Season> allSeasons  = new ArrayList<Season>();
	private Properties props;
	private Properties sensitive;
	private String fullPath;
	private CLIOptions opts;
	static Scanner inp = new Scanner(System.in);
	
	public TvShow(String name, CLIOptions opts) throws IOException {
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
		this.opts = opts;
		this.name = name;
		this.inp = new Scanner(System.in);
		getInfoApi();
	}
	
	public void getInfoApi() throws IOException {
		URL getTvInfoUrl = null;
		try {
			//putting the request params in a hashmap
			Map<String, String> params = new HashMap<>();
			params.put("api_key", this.sensitive.getProperty("api.key"));
			params.put("language", "en-US");
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
        try {
			if(results.getInt("total_results") == 1 || opts.matchOpt('f').isValue()) {
				JSONObject temp = (JSONObject) results.getJSONArray("results").get(0);
				this.name = temp.getString("name");
				this.tv_id = temp.getInt("id");
			} else if (results.getInt("total_results") == 0) {
				System.out.printf(ANSI_YELLOW + "No results were found for a TV show with the name of '%s'\n" + ANSI_RESET, this.name);
				return;
			} else {
				multipleResults(results);
				if(this.tv_id == 0) {
					return;
				}
			}
		} catch (InstanceNotFoundException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        //System.out.printf("Matched TV show is '%s' with id: %d\nMatching now number of seasons", this.name, this.tv_id);
        Map<String, String> paramsTv = new HashMap<>();
		paramsTv.put("api_key", this.sensitive.getProperty("api.key"));
		paramsTv.put("language", "en-US");
        URL getTvInfoSeason = new URL(
				QueryBuilder.buildURL(
						props.getProperty("api.tvseasons.url") + this.tv_id, 
						QueryBuilder.formatParams(paramsTv)
				)
		);
        HttpURLConnection conTv = (HttpURLConnection) getTvInfoSeason.openConnection();
        conTv.setRequestMethod("GET");
        conTv.setConnectTimeout(10000);
        conTv.setReadTimeout(10000);
        conTv.setRequestProperty("Content-Type", "application/json");
        StringBuilder contentTv;

        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(conTv.getInputStream()))) {
        	//JSONObject results
        	String line;
        	contentTv = new StringBuilder();
        	while ((line = in.readLine()) != null) {
        		contentTv.append(line);
                contentTv.append(System.lineSeparator());
            }
        } finally {
            conTv.disconnect();
        }
        //System.out.printf("%s\n", contentTv.toString());
        JSONObject resultsTv = new JSONObject(contentTv.toString());
        this.numSeasons = resultsTv.getInt("number_of_seasons");
        //System.out.printf("num of seasons: %d\n", this.numSeasons);
		//System.out.printf("Response of '%s' request is %d\n", con.getRequestMethod(), responseCode);
        
		//TODO get request to the tv show id, num of seasons and name
		//setName(name result from get request);
		//setNumSeason(number of seasons);
		//setTvId(tv id value from response of get request);
	}
	
	public void multipleResults(JSONObject results) {
		JSONObject temp = (JSONObject) results.getJSONArray("results").get(0);
    	System.out.printf(ANSI_YELLOW + "\n\nThere are multiple possibilities when matching a TV show with the name '%s'\nIs it '%s' which started airing on %s?\n"
    			+ "(yes or no)\n" + ANSI_RESET, this.name, temp.getString("name"), temp.getString("first_air_date"));
    	
//    	while (!inp.hasNextLine());
    	String cin = TvShow.inp.nextLine();
    	    	
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
    			selectMatch = TvShow.inp.nextInt();
    		} catch(InputMismatchException e) {
    			System.out.printf(ANSI_RED + "Wrong input entered\nSkipping file...\n\n" + ANSI_RESET);
//    	    	inp.close();
    			return;
    		}
    		temp = (JSONObject) results.getJSONArray("results").get(selectMatch - 1);
    		this.name = temp.getString("name");
        	this.tv_id = temp.getInt("id");
//        	inp.close();
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
	
	public Season getSeason(Season newSeason) throws Exception {
		for (int i = 0; i < this.allSeasons.size(); i++) {
			if (this.allSeasons.get(i).getSeasonNum() == newSeason.getSeasonNum()) {
				this.allSeasons.get(i).setFullPath(this.fullPath);
				return this.allSeasons.get(i);
			}
		}
		newSeason.setTvId(this.tv_id);
		newSeason.setFullPath(this.fullPath);
		newSeason.setTvName(this.name);
		newSeason.getTitleAPI();
		this.allSeasons.add(newSeason);
		return newSeason;
	}

	public Season createNewSeason(int seasonNum) throws Exception {
		Season newSeason = new Season(seasonNum);
		newSeason = getSeason(newSeason);
		return newSeason;
	}
	
	public List<Season> getAllSeasons() {
		return this.allSeasons;
	}
	
	public void setAllSeasons(List<Season> allSeasons) {
		this.allSeasons = allSeasons;
	}
}
