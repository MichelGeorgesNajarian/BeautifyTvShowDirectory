package main.java;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

public class QueryBuilder {

	public static String formatParams(Map<String, String> params) {
		
		StringBuilder formatted = new StringBuilder();
		//iterating through the map to get each param
		for(Map.Entry<String, String> param : params.entrySet()) {
			try {
				formatted.append(URLEncoder.encode(param.getKey(), "UTF-8"));
				formatted.append("=");
				formatted.append(URLEncoder.encode(param.getValue(), "UTF-8"));
				formatted.append("&");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return (formatted.toString().length() > 0) 
				? formatted.deleteCharAt(formatted.length()-1).toString() 
				: formatted.toString();
	}

}
