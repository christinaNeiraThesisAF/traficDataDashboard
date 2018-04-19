package com.thesisAF.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;

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
	public static String startDate = "2015-02-28 00:00:00";
	public static String data = new String();
	public static int count = 1;
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private final String USER_AGENT = "Mozilla/5.0";

	@GET
	@Path("/toll")
	public  Response getTolls() throws Exception {
		System.out.println("count "+count++);
		System.out.println("Start "+startDate);
		Date currentDate= getDate();
		sendGet(currentDate);
		
		ArrayList<Passage> passages = new ArrayList<Passage>();
		
		JSONArray jsonArray = new JSONArray(data);
		
		for(int i = 0 ; i < jsonArray.length() ; i++){ 
			JSONObject obj = jsonArray.getJSONObject(i);
			Passage passage = new Passage(obj.getString("PassageDatum"), obj.getString("PassageTid"),obj.getString("Betalstation"),
				   					obj.getLong("Korfalt"),obj.getString("Riktning"),obj.getString("Lan"),
				   					obj.getString("Kommun"),obj.getString("Postnummer"),obj.getString("Fordonstyp"),obj.getString("Skatteobjekt"));
			passage.parseDateAndTime();
			if(passage.getVehicleType().equals("Missing")){
				passage.setVehicleType("Okänd");
				passage.setCounty("Okänd");
				passage.setTownship("Okänd");
			}
			passages.add(passage);  
		}
		
		data = new Gson().toJson(passages);
		
		startDate = dateFormat.format(currentDate);
		System.out.println("startDate in toll "+ startDate);		
		return Response.status(200).entity(data).build();
	}

	// HTTP GET request
	private void sendGet(Date currentDate) throws Exception {
		DateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

		String extractedDate = dateformat.format(dateFormat.parse(startDate)).toString();

		String endTime = timeFormat.format(currentDate).toString();
		System.out.println("currentTime "+ endTime);
		System.out.println("start date in send get "+ startDate);
		Date parsedStartDateFromString = dateFormat.parse(startDate);
		String startTime = timeFormat.format(parsedStartDateFromString);
		System.out.println("startTime "+ startTime);


		String url = "https://tsopendata.azure-api.net/Passager/v0.1/ByBetalstation?Passagedatum="+ extractedDate + "&PassageTidStart="+startTime+"&PassageTidStopp="+endTime;

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
