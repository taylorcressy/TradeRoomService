package http_controllers_test;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

public class TestSearch {

	
	private CookieManager cookies;
	
	
	@Test
	public void register() throws Exception {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("username", "upshutdown");
		params.put("password", "SomePassword1");
		params.put("email", "someguy@yahoo.com");
		params.put("firstName", "Taylor");
		params.put("lastName", "Cressy");
		sendPost("user/register", params);
	}
	
	public void login() throws Exception {
		cookies = new CookieManager();
		CookieManager.setDefault(cookies);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("username", "upshutdown");
		params.put("password", "SomePassword1");
		sendPost("user/login", params);
	}
	
	/*@Test
	public void searchByUsername() throws Exception {
		login();
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("query", "");
		sendPost("search/searchForUser", params);
	}*/
	
	@Test
	public void searchForItems() throws Exception {
		login();
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("query", "firstTag");
		sendPost("search/searchForItem", params);
	}
	

	// HTTP POST request
		private void sendPost(String userRequest, Map<String, String> params) throws Exception {
			
			String userpass = "user:2FCC2E5118FAAEA725449AD74E2FAEC450842311D9F045C610FEA8A8EA55FA76";
			String basicAuth = "Basic " + new String(new Base64().encode(userpass.getBytes()));
			
			String url = "http://127.0.0.1:8090/" + userRequest;
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			// add reuqest header
			con.setRequestProperty("Authorization", basicAuth);			
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
