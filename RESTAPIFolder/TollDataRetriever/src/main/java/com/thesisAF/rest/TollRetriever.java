package com.thesisAF.rest;
 
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import javax.ws.rs.core.Response;

 
@Path("/tolls")
public class TollRetriever {
	public String data = new String();
	
	private final String USER_AGENT = "Mozilla/5.0";
	
	@GET
	@Path("/toll")
	public  Response getTolls() throws Exception {	
		sendGet();
		System.out.println("hej");
		
		return Response.status(200).entity(data).build();
	}
//
//		public static void main(String[] args) throws Exception {
//
//			TollRetreiver http = new TollRetreiver();
//
//			System.out.println("Testing 1 - Send Http GET request");
//			http.sendGet();
//
//	}
//
		// HTTP GET request
		private void sendGet() throws Exception {

			String url = "https://tsopendata.azure-api.net/Passager/v0.1/ByBetalstation?Passagedatum=2015-10-01&PassageTidStart=06:00:00&PassageTidStopp=06:10:00";
			
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			// optional default is GET
			con.setRequestMethod("GET");

			//add request header
			con.setRequestProperty("User-Agent", USER_AGENT);

			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			//print result
			System.out.println(response.toString());
			data = response.toString();
		}

}
