package com.thesisAF.rest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Passage {

	private String paymentStation, vehicleType, county, township, postalCode, taxItem, passageDate, passageTime ,direction, passageDateAndTime, granularity, location;
	private long lane;
	
	public Passage() {

	}

	public Passage(String passageDate, String passageTime, String paymentStation, long lane, String direction, String county,
				   			String town, String postNr,  String vehicleType,  String taxItem) {
		super();
		this.paymentStation = paymentStation;
		this.vehicleType = vehicleType;
		this.county = county;
		this.township = town;
		this.postalCode = postNr;
		this.taxItem = taxItem;
		this.passageDate = passageDate;
		this.passageTime = passageTime;
		this.direction = direction;
		this.lane = lane;
		this.location = "";
	}

	
	public String getPaymentStation() {
		return paymentStation;
	}

	public void setPaymentStation(String paymentStation) {
		this.paymentStation = paymentStation;
	}

	public String getVehicleType() {
		return vehicleType;
	}

	public void setVehicleType(String vehicleType) {
		this.vehicleType = vehicleType;
	}

	public String getCounty() {
		return county;
	}

	public void setCounty(String county) {
		this.county = county;
	}

	public String getTownship() {
		return township;
	}

	public void setTownship(String township) {
		this.township = township;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getTaxItem() {
		return taxItem;
	}

	public void setTaxItem(String taxItem) {
		this.taxItem = taxItem;
	}

	public String getPassageDate() {
		return passageDate;
	}

	public void setPassageDate(String passageDate) {
		this.passageDate = passageDate;
	}

	public String getPassageTime() {
		return passageTime;
	}

	public void setPassageTime(String passageTime) {
		this.passageTime = passageTime;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getPassageDateAndTime() {
		return passageDateAndTime;
	}

	public void setPassageDateAndTime(String passageDateAndTime) {
		this.passageDateAndTime = passageDateAndTime;
	}

	public long getLane() {
		return lane;
	}

	public void setLane(long lane) {
		this.lane = lane;
	}

	
	public String getGranularity() {
		return granularity;
	}

	public void setGranularity(int seconds) {	
		if(seconds < 10 )
			this.granularity = passageDateAndTime.substring(0, 17) + "0" + seconds;
		else
			this.granularity = passageDateAndTime.substring(0, 17) + "" + seconds;

	}

	public String getlocation() {
		return location;
	}

	public void setlocation(String location) {
		this.location = location;
	}

	public void parseDateAndTime() throws ParseException{
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");		
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		format.setTimeZone(TimeZone.getTimeZone("GMT"));

		Date date = format.parse(passageDate.substring(0, 10) + " " + passageTime);
		passageDateAndTime = dateFormat.format(date);
		 
		//System.out.println("Parsed date and time "+passageDateAndTime);
	}

	@Override
	public String toString() {
		return "Passage [paymentStation=" + paymentStation + ", vehicleType=" + vehicleType + ", county=" + county
				+ ", township=" + township + ", postalCode=" + postalCode + ", taxItem=" + taxItem + ", passageDate="
				+ passageDate + ", passageTime=" + passageTime + ", direction=" + direction + ", passageDateAndTime="
				+ passageDateAndTime + ", granularity=" + granularity + ", lane=" + lane + "]";
	}
	
}