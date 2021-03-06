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
import org.json.JSONException;
import org.json.JSONObject;

public class Season implements ANSIColors {
	
	private int season_num;
	private List<Episode> allEpisodes = new ArrayList<Episode>();
	private Properties props;
	private Properties sensitive;
	private int tv_id;
	private String full_path;
	private String tv_name; 
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
	
	public Episode generateEpisode(int episodeNum) throws Exception {
		Episode new_ep = new Episode(episodeNum);
		new_ep = getEpisode(new_ep);
		new_ep.setTvId(this.tv_id);
		try {
			new_ep.setEpisodeTitle(this.query_results.getJSONObject(episodeNum - 1).getString("name"));	
		} catch (JSONException e) {
			System.out.printf(ANSI_RED + "\n\nAn error occured with file: %s\nit seems that %s (id: %d) does not have an episode %d in season %d."
					+ "\nPlease verify your information and try again\n\n" + ANSI_RESET, this.full_path, this.tv_name, this.tv_id, episodeNum, this.season_num);
			this.allEpisodes.remove(new_ep);
			throw new Exception();
		}
		new_ep.setFullPath(this.full_path);
		new_ep.setSeasonNum(this.season_num);
		new_ep.setTvName(this.tv_name);
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

	public void getTitleAPI() throws Exception {
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
			con = (HttpURLConnection) getTvInfoUrl.openConnection();
			con.setRequestMethod("GET");
			//request header JSON
			con.setRequestProperty("Content-Type", "application/json");
			//timeout after 10 seconds for both connect and read
			con.setConnectTimeout(10000);
			con.setReadTimeout(10000);
			if (con.getResponseCode() > 399) {
				System.out.printf(ANSI_RED + "\n\nAn error occured with file: %s\nit seems that %s (id: %d) does not have a season %d."
						+ "\nPlease verify your information and try again\n\n" + ANSI_RESET, this.full_path, this.tv_name, this.tv_id, this.season_num);
				throw new Exception();
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
//        for (int i = 0; i < this.query_results.length(); i++) {
//        	System.out.printf("episode %d: %s\n", this.query_results.getJSONObject(i).getInt("episode_number"), this.query_results.getJSONObject(i).getString("name"));
//        }
	}

	public String getTvName() {
		return tv_name;
	}

	public void setTvName(String tv_name) {
		this.tv_name = tv_name;
	}
	
	public List<Episode> getAllEpisodes() {
		return this.allEpisodes;
	}
	
	public void setAllEpisodes(List<Episode> allEpisodes) {
		this.allEpisodes = allEpisodes;
	} 
}
