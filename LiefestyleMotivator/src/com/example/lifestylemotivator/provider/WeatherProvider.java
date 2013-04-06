package com.example.lifestylemotivator.provider;

import java.io.IOException;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import com.example.lifestylemotivator.models.CityForecastBO;

import android.util.Log;

public class WeatherProvider {
	private static final String TAG = "FINDWEATHER";
	String NAMESPACE = "http://ws.cdyne.com/WeatherWS/";
    String METHOD_NAME = "GetCityWeatherByZIP";
    String SOAP_ACTION = "http://ws.cdyne.com/WeatherWS/GetCityWeatherByZIP";
    String URL = "http://wsf.cdyne.com/WeatherWS/Weather.asmx";
  
	
	public CityForecastBO getWeatherForecast(String... urls) {
		// 1. Create SOAP Envelope 
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		// 2. Set the request parameters
		SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
        CityForecastBO result = null;

		PropertyInfo pi = new PropertyInfo();
		pi.setNamespace("http://ws.cdyne.com/WeatherWS/"); 
		pi.setName("ZIP");
		pi.setValue("27560");
		Request.addProperty(pi);

		envelope.dotNet = true;

		envelope.setOutputSoapObject(Request);

		HttpTransportSE httpTransport = new HttpTransportSE(URL);

		httpTransport.debug = true;

		try {
			httpTransport.call(NAMESPACE + METHOD_NAME, envelope);
	        Log.d(TAG, "HTTP REQUEST:\n" + httpTransport.requestDump);
	        Log.d(TAG, "HTTP RESPONSE:\n" + httpTransport.responseDump);

	        if (envelope.bodyIn instanceof SoapObject) { // SoapObject = SUCCESS
	            SoapObject soapObject = (SoapObject) envelope.bodyIn;
	            result = parseSOAPResponse(soapObject);
	        } else if (envelope.bodyIn instanceof SoapFault) { // SoapFault = FAILURE
	            SoapFault soapFault = (SoapFault) envelope.bodyIn;
	            throw new Exception(soapFault.getMessage());
	        }
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

 

	private CityForecastBO parseSOAPResponse(SoapObject response) {
        CityForecastBO cityForecastResult = null;
        SoapObject cityForecastNode = (SoapObject) response.getProperty("GetCityWeatherByZIPResult");
        // see the wsdl for the definition of "ForecastReturn" (which can be null/empty)
        // i.e. <s:element name="GetCityForecastByZIPResponse"> element 
        if (cityForecastNode != null) {
            boolean isSuccess = Boolean.parseBoolean(cityForecastNode.getPrimitivePropertySafelyAsString("Success"));
            
            String responseText =  cityForecastNode.getPrimitivePropertySafelyAsString("ResponseText");
            String state =  cityForecastNode.getPrimitivePropertySafelyAsString("State");
            String city =  cityForecastNode.getPrimitivePropertySafelyAsString("City");
            String weatherStationCity =  cityForecastNode.getPrimitivePropertySafelyAsString("WeatherStationCity");
                        
        	String description = cityForecastNode.getPrimitivePropertySafelyAsString("Description");
        	String temperature = cityForecastNode.getPrimitivePropertySafelyAsString("Temperature");
        	String relativeHumidity = cityForecastNode.getPrimitivePropertySafelyAsString("RelativeHumidity");
        	String wind = cityForecastNode.getPrimitivePropertySafelyAsString("Wind");
        	String pressure = cityForecastNode.getPrimitivePropertySafelyAsString("Pressure");
        	String visibility = cityForecastNode.getPrimitivePropertySafelyAsString("Visibility");
        	String windChill = cityForecastNode.getPrimitivePropertySafelyAsString("WindChill");
        	String remarks = cityForecastNode.getPrimitivePropertySafelyAsString("Remarks");
 
            
            cityForecastResult = new CityForecastBO(isSuccess, state, city, weatherStationCity, responseText,
            		description, temperature, relativeHumidity, wind, pressure, visibility, windChill, remarks);

        }

        return cityForecastResult;
    }

}
