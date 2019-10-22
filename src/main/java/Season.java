package main.java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONObject;

public class Season {
	
	private int season_num;
	private List<Episode> allEpisodes = new ArrayList<Episode>();
	private Properties props;
	private Properties sensitive;
	private int episode_num;
	private int tv_id;
	private String full_path;
	private JSONArray query_results;
	
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
	
	public void setSeasonNum(int num) {
		this.season_num = num;
	}
	
	public int getSeasonNum() {
		return this.season_num;
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
	
	public Episode generateEpisode(int episodeNum) {
		Episode new_ep = new Episode(episodeNum);
		new_ep = getEpisode(new_ep);
		new_ep.setTvId(this.tv_id);
		return new_ep;
	}

	public Episode getEpisode(Episode new_ep) {
		for (int i = 0; i < this.allEpisodes.size(); i++) {
			if(this.allEpisodes.get(i).getEpisodeNumber() == new_ep.getEpisodeNumber()) {
				return this.allEpisodes.get(i);
			}
		}
		this.allEpisodes.add(new_ep);
		new_ep.setFullPath(this.full_path);
		return new_ep;
	}

	public void getTitleAPI() {
		Map<String, String> params = new HashMap<>();
		params.put("api_key", this.sensitive.getProperty("api.key"));
		params.put("language", "en-US");
		URL getTvInfoUrl = null;
		try {
			getTvInfoUrl = new URL(
					QueryBuilder.buildURL(
							props.getProperty("api.tvepisode.url") + this.tv_id + "/" + "season/" + this.season_num, 
							QueryBuilder.formatParams(params)
					)
			);
		} catch (IOException e) {
			e.printStackTrace();
		}
		HttpURLConnection con = null;
		try {
			con = (HttpURLConnection) getTvInfoUrl.openConnection();
			con.setRequestMethod("GET");
			//request header JSON
			con.setRequestProperty("Content-Type", "application/json");
			//timeout after 10 seconds for both connect and read
			con.setConnectTimeout(10000);
			con.setReadTimeout(10000);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StringBuilder content = null;

        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()))) {
        	//JSONObject results
        	String line;
        	content = new StringBuilder();
        	while ((line = in.readLine()) != null) {
        		content.append(line);
                content.append(System.lineSeparator());
            }
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            con.disconnect();
        }
        //System.out.println(content.toString());
        JSONObject results = new JSONObject(content.toString());
        this.query_results = results.getJSONArray("episodes");
        for (int i = 0; i < this.query_results.length(); i++) {
        	System.out.printf("episode %d: %s\n", this.query_results.getJSONObject(i).getInt("episode_number"), this.query_results.getJSONObject(i).getString("name"));
        }
	}
}
