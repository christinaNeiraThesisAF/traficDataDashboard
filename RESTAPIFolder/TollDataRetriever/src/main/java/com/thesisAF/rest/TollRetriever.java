package com.thesisAF.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.sun.jersey.server.impl.uri.rules.automata.AutomataMatchingUriTemplateRules;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import java.util.TimeZone;


@Path("/tolls")
public class TollRetriever {
	public static String startDate = "2015-02-27 06:00:00";
	public static String data = new String();
	String startTime;
	public static int count = 1;
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private final String USER_AGENT = "Mozilla/5.0";

	@GET
	@Path("/toll")
	public  Response getTolls() throws Exception {
		Date currentDate= getDate();
		sendGet(currentDate);

		ArrayList<Passage> passages = filterPassages(); 
		
		data = new Gson().toJson(passages);
		
		startDate = dateFormat.format(currentDate);
		System.out.println("next startDate in toll "+ startDate);		
		return Response.status(200).entity(data).build();
	}

	private ArrayList<Passage> filterPassages() throws ParseException {
		ArrayList<Passage> passages = new ArrayList<Passage>();
		JSONArray jsonArray = new JSONArray(data);
		
		int nrOfPassages = jsonArray.length();
		
		System.out.println("Antalet bilar "+ nrOfPassages);
		
		if(nrOfPassages<59)
			filterLessThanOrEqualSixtyPassages(nrOfPassages,jsonArray, passages);
		else 
			filterMoreThanSixtyPassages(nrOfPassages,jsonArray, passages);
		
		return passages;
	}

	private void filterMoreThanSixtyPassages(int nrOfPassages, JSONArray jsonArray, ArrayList<Passage> passages) throws ParseException {
		int currentSeconds = 0 ,diff = 0,nrOfPassagesInSecond = 0;
		diff = nrOfPassages/60;
		boolean hasReachedSixtySecs = false;
		for(int i = 0 ; i < jsonArray.length() ; i++){ 
			JSONObject obj = jsonArray.getJSONObject(i);

			if(obj.getString("PassageTid").equals(startTime)){
					Passage passage = parsePassage(obj,passages,currentSeconds);
										
					nrOfPassagesInSecond++;
					passages.add(passage); 
					
					if(diff == nrOfPassagesInSecond && currentSeconds<=59 && !hasReachedSixtySecs){
						currentSeconds += 1;
						nrOfPassagesInSecond = 0;
					}	
					
					if(currentSeconds == 60){
						currentSeconds = 0;
						hasReachedSixtySecs = true;
					}
					
					if(hasReachedSixtySecs){
						currentSeconds += 1;
						nrOfPassagesInSecond = 0;
					}

			}
		}
	}

	private Passage parsePassage(JSONObject obj, ArrayList<Passage> passages, int currentSeconds) throws ParseException {
		
		Passage passage = new Passage(obj.getString("PassageDatum"), obj.getString("PassageTid"),obj.getString("Betalstation"),
					obj.getLong("Korfalt"),obj.getString("Riktning"),obj.getString("Lan"),
					obj.getString("Kommun"),obj.getString("Postnummer"),obj.getString("Fordonstyp"),obj.getString("Skatteobjekt"));
		passage.parseDateAndTime();
		
		passage.setGranularity(currentSeconds);
		//System.out.println("Granurality "+ passage.getGranularity());
		if(passage.getVehicleType().equals("Missing")){
			passage.setVehicleType("Okänd");
			passage.setCounty("Okänd");
			passage.setTownship("Okänd");
		}
		
		String geoPointKey = passage.getPaymentStation() + "-" + passage.getDirection();
		passage.setlocation(GeoDataLoad.geoData.get(geoPointKey));
		if(passage.getlocation().equals(""))
			System.out.println("NULL: "+ passage.getlocation() + " "+ passage.getPaymentStation() + " " + passage.getDirection());
			
		return passage;
	}

	private void filterLessThanOrEqualSixtyPassages(int nrOfPassages, JSONArray jsonArray,
			ArrayList<Passage> passages) throws ParseException {
		int currentSeconds = 0 ,diff = 0;
		if(nrOfPassages>0)
			diff = 60/nrOfPassages;
		
		for(int i = 0 ; i < jsonArray.length() ; i++){ 
			JSONObject obj = jsonArray.getJSONObject(i);
			if(obj.getString("PassageTid").equals(startTime)){
				Passage passage = parsePassage(obj,passages,currentSeconds);
				
				currentSeconds += diff;
				passages.add(passage); 
			} 
		}	
	}

	// HTTP GET request
	private void sendGet(Date currentDate) throws Exception {
		DateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

		String extractedDate = dateformat.format(dateFormat.parse(startDate)).toString();

		Date parsedStartDateFromString = dateFormat.parse(startDate);
		startTime = timeFormat.format(parsedStartDateFromString);
		System.out.println("startTime "+ startTime);


		String url = "https://tsopendata.azure-api.net/Passager/v0.1/ByBetalstation?Passagedatum="+ extractedDate + "&PassageTidStart="+startTime+"&PassageTidStopp="+startTime;

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		//add request header
		con.setRequestProperty("User-Agent", USER_AGENT);

		BufferedReader in = new BufferedReader(
				new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		data = response.toString();
	}
	

	public Date getDate() throws ParseException{
		Date enddate = dateFormat.parse(startDate);

		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		cal.setTime(enddate);
		cal.add(Calendar.MINUTE, 1);

		Date currentDate = cal.getTime();

		return currentDate;	 
	}
	
}
