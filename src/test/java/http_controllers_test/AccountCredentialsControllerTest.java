package http_controllers_test;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


import org.junit.Test;

public class AccountCredentialsControllerTest {
	
	private CookieManager cookies;
	
	@Test
	public void testRegister() throws Exception {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("username", "upshutdown");
		params.put("password", "SomePassword1");
		params.put("email", "someguy@yahoo.com");
		params.put("firstName", "Taylor");
		params.put("lastName", "Cressy");
		sendPost("user/register", params);
	}
	
	@Test
	public void testLogin() throws Exception {
		cookies = new CookieManager();
		CookieManager.setDefault(cookies);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("username", "upshutdown");
		params.put("password", "SomePassword1");
		sendPost("user/login", params);
	}
	
	@Test
	public void testUpdatePreferences() throws Exception {
		testLogin();
		
		HashMap<String, String> params = new HashMap<String, String>();
		
		params.put("password", "SomePassword1");	//Keep it the same for future tests
		params.put("firstName", "Bob");
		params.put("lastName", "Hope");
		params.put("dob", "01-11-1991");
		sendPost("user/updatePreferences", params);
	}
	
	@Test
	public void testUpdateAddress() throws Exception {
		testLogin();
		
		HashMap<String, String> params = new HashMap<String, String>();

		params.put("streetName", "Orion");
		params.put("streetNumber", "1234");
		params.put("areaCode", "91406");
		params.put("country", "United States");
		params.put("county", "Los Angeles");
		params.put("geoLocation", "fakeLocation");
		params.put("city", "Van Nuys");
		
		sendPost("user/updateAddress", params);
	}
	
	@Test
	public void testUpdateGeoLocation() throws Exception {
		testLogin();
		
		HashMap<String, String> params = new HashMap<String, String>();

		params.put("geoLocation", "fakeLocation");
		
		sendPost("user/updateCurrentLocation", params);
	}
	
	

	// HTTP POST request
	private void sendPost(String userRequest, Map<String, String> params) throws Exception {

		String url = "http://127.0.0.1:8090/" + userRequest;
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		

		String urlParameters = "";
		Iterator<Map.Entry<String, String>> it = params.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<String, String> pairs = (Map.Entry<String, String>)it.next();
	        System.out.println(pairs.getKey() + " = " + pairs.getValue());
	        urlParameters += pairs.getKey() + "=" + pairs.getValue();
	        
	        if(it.hasNext() == true)
	        	urlParameters += "&";
	        
	        it.remove(); // avoids a ConcurrentModificationException
	    }

		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + urlParameters);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// print result
		System.out.println(response.toString());

	}
}
