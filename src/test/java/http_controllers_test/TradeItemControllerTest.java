package http_controllers_test;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import database_entities.User;

public class TradeItemControllerTest {

	
	private User user;
	
	private HttpClient client;
	
	@Before
	public void setup() {
		client = HttpClientBuilder.create().build();
	}
	
	public void register() throws Exception {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("username", "upshutdown");
		params.put("password", "SomePassword1");
		params.put("email", "someguy@yahoo.com");
		params.put("firstName", "Taylor");
		params.put("lastName", "Cressy");
		sendPost("/user/register", params);
	}
	
	public void login() throws Exception{
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("username", "upshutdown");
		params.put("password", "SomePassword1");
		String userString = sendPost("/user/login", params);
		Map<String, String> serverMessage = new Gson().fromJson(userString, new TypeToken<Map<String, String>>() {}.getType());
		user = new Gson().fromJson(serverMessage.get("data"), User.class);
	}
	
	@Test
	public void testAddItem() throws Exception {
		this.register();
		this.login();
		
		List<String> tags = new ArrayList<String>();
		tags.add("Phone");
		tags.add("Clothes");
		tags.add("Selfies");
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("name", "first Item");
		params.put("description", "Some description would definitely go here.");
		params.put("condition", "poor");
		params.put("count", "1");
		params.put("tags", new Gson().toJson(tags));
		sendPost("/user/items/addTradeItem", params);
	}
	
	@Test
	public void testRetrievingTradeItems() throws Exception {
		this.login();
		
		List<String> ids = new ArrayList<String>();
		
		for(String next: user.getTradeRoomMeta().getItemIds())
			ids.add(next);
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("itemIds", new Gson().toJson(ids));
		
		this.sendPost("/user/items/retrieveItemsFromList", params);
	}
	
	@Test
	public void testUpdateTradeItem() throws Exception {
		this.login();
		
		String itemToUpdateId = user.getTradeRoomMeta().getItemIds().get(0);
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("name", "Updated Name");
		params.put("itemId", itemToUpdateId);
		
		this.sendPost("/user/items/updateTradeItem", params);
	}
	
	@Test
	public void testAddImageToItem() throws Exception {
		this.login();
		
		String idToAddImage = user.getTradeRoomMeta().getItemIds().get(0);
				
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("itemId", idToAddImage);
		
		ClassPathResource resource = new ClassPathResource("test-image.png");
		
		
		sendPostImage("addImageToTradeItem", params, resource.getFile() );
		
	}
	
	@Test
	public void testGetImageOfTradeItem() throws Exception {
		this.login();
				
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("imageId", "535d38da300416a1dad8b6e1");
		
		retrievePostImage("getImageById", params);
	}
	
	
	@Test
	public void testRemoveImageOfItem() throws Exception {
		//this.login();
		
		//HashMap<String, String> param = new HashMap<String, String>();
	}
	
	private String sendPost(String userRequest, Map<String, String> params) throws Exception {
		HttpPost post = new HttpPost("http://localhost:8090/" + userRequest);

		List<NameValuePair> nameValues = new ArrayList<NameValuePair>();
		
		for(String key: params.keySet()) {
			nameValues.add(new BasicNameValuePair(key, params.get(key)));
		}
		
		post.setEntity(new UrlEncodedFormEntity(nameValues));
		
		String inputLine, totalLines ="";
		HttpResponse response = client.execute(post);
		BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		 try {
		       while ((inputLine = in.readLine()) != null) {
		    	   totalLines += inputLine;
		       }
		       in.close();
		  } catch (IOException e) {
		       e.printStackTrace();
		  }
		 System.out.println(totalLines);
		 return totalLines;
	}
	
	public String sendPostImage(String userRequest, HashMap<String, String> params, File imageFile) throws Exception{
		HttpPost post = new HttpPost("http://localhost:8090/user/items/" + userRequest);
		
		MultipartEntity entity = new MultipartEntity();
		entity.addPart("itemId", new StringBody(params.get("itemId")));
		entity.addPart("imageData", new FileBody(imageFile));
		
		post.setEntity(entity);
		
		String inputLine, totalLines = "";
		HttpResponse response = client.execute(post);
		BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		 try {
		       while ((inputLine = in.readLine()) != null) {
		              totalLines += inputLine;
		       }
		       in.close();
		  } catch (IOException e) {
		       e.printStackTrace();
		  }
		
		 System.out.println(totalLines);
		 return totalLines;
		 
	}
	
	public void retrievePostImage(String userRequest, HashMap<String, String> params) throws Exception {
		HttpPost post = new HttpPost("http://localhost:8090/user/items/" + userRequest);
		
		List<NameValuePair> nameValues = new ArrayList<NameValuePair>();
		
		for(String key: params.keySet()) {
			nameValues.add(new BasicNameValuePair(key, params.get(key)));
		}
		
		post.setEntity(new UrlEncodedFormEntity(nameValues));
		
		HttpResponse response = client.execute(post);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    response.getEntity().writeTo(baos);
	    byte[] bytes = baos.toByteArray();
		
		FileOutputStream fos = new FileOutputStream(new File("post-image.png"));
		IOUtils.write(bytes, fos);
	}
}
