package com.thesisAF.rest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class GeoDataLoad implements ServletContextListener{
	public static Map<String, String> geoData = new HashMap<String, String>();
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		System.out.println("Inside listener");
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader("/Users/christinantis/Documents/TIDAA/Thesis/TraficDataDashboardFolder/RESTAPIFolder/TollDataRetriever/geoData.txt"));
            String line = bufferedReader.readLine();
            while (line != null){
            	String str[] = line.split(":"); 
            	String township = str[0]; 
            	String coordinates = str[1];
            	geoData.put(township, coordinates);
                System.out.println("GEODAT: "+ geoData.get(township));
                line = bufferedReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
        	 try {
                 if (bufferedReader != null)
                     bufferedReader.close();
             } catch (IOException e) {
                 e.printStackTrace();
             }
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub
		
	}
	
	

}
