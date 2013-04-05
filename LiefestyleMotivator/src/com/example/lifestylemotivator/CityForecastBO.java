package com.example.lifestylemotivator;

public class CityForecastBO {
	boolean success;
	String responseText;
	String state;
	String city;
	String weatherStationCity;
	String description;
	String temperature;
	String relativeHumidity;
	String wind;
	String pressure;
	String visibility;
	String windChill;
	String remarks;

	public CityForecastBO(boolean isSuccess, String state, String city,
			String weatherStationCity, String responseText, String description2, 
			String temperature2, String relativeHumidity2, String wind2, String pressure2, 
			String visibility2, String windChill2, String remarks2) 
	{
		success = isSuccess;
		this.state = state;
		this.city = city;
		this.weatherStationCity = weatherStationCity;
		this.responseText = responseText;
		this.description = description2;
		this.temperature = temperature2;
		this.relativeHumidity = relativeHumidity2;
		this.wind = wind2;
		this.pressure = pressure2;
		this.visibility = visibility2;
		this.windChill = windChill2;
		this.remarks = remarks2;

	}

}
