package com.gdelight.server.service;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.log4j.Logger;

import com.gdelight.domain.base.BaseRequestBean.TRANSACTION_TYPE;
import com.gdelight.domain.item.Item;
import com.gdelight.domain.request.FindAvailableRequestBean;
import com.gdelight.tools.json.JsonUtils;

public class PostServiceTest {
	
	private static String contentType = "text/xml; charset=ISO-8859-1";
	private static Logger log = Logger.getLogger(PostServiceTest.class);

	public static void main(String[] args) {
		
		String type = "internal";
		String email = "tomansley@gmail.com" + System.currentTimeMillis();

		if (type.equals("external")) {
			TestExternal();
		} else {
			TestSignupInternal(email);
			TestLoginInternal(email);
			TestMakeAvailableInternal(email);
			TestFindAvailableInternal(email);
		}
		printJsonObject();
	}
	
	public static void TestSignupInternal(String email) {
		
		log.debug("----------------------------------------------------------------------------------------------");
		String jsonData = "{\"transactionType\":\"SIGNUP\"," +
				   "\"userId\":\"" + email + "\"," +
				   "\"token\":\"password\"," +
			   	   "\"latitude\":\"41.866381\"," +
			       "\"longitude\":\"-87.621374\"}";

		makeRequest(jsonData);
		log.debug("----------------------------------------------------------------------------------------------");
	}
	
	public static void TestLoginInternal(String email) {
		
		log.debug("----------------------------------------------------------------------------------------------");
		String jsonData = "{\"transactionType\":\"LOGIN\"," +
						   "\"userId\":\"" + email + "\"," +
						   "\"token\":\"password\"}";
		
		makeRequest(jsonData);
		log.debug("----------------------------------------------------------------------------------------------");
	}
	
	public static void TestMakeAvailableInternal(String email) {
		
		log.debug("----------------------------------------------------------------------------------------------");
		String jsonData = "{\"transactionType\":\"MAKE_AVAILABLE\"," +
				   		  "\"userId\":\"" + email + "\"," +
				   		  "\"token\":\"password\"," +
				   		  "\"latitude\":\"41.866381\"," +
				   		  "\"longitude\":\"-87.621374\"," +
				   		  "\"available\":[" +
				   				"{\"name\":\"Seeds\",\"location\":\"Main Location\",\"items\":[" +
				   					"{\"amount\":\"Lots\",\"name\":\"Pear Seeds\"}," +
				   					"{\"amount\":\"Some\",\"name\":\"Apple Seeds\"}]" +
				   				"}," +
				   				"{\"name\":\"Fruits\",\"location\":\"Main Location\",\"items\":[" +
			   						"{\"amount\":\"Little\",\"name\":\"Apples\",\"subGroup\":\"Braeburn\"}," +
			   						"{\"amount\":\"Lots\",\"name\":\"Oranges\",\"subGroup\":\"Gardner\"}]" +
			   					"}]" +
				   		  "}";

		makeRequest(jsonData);
		log.debug("----------------------------------------------------------------------------------------------");
	}
	
	public static void TestFindAvailableInternal(String email) {
		
		log.debug("----------------------------------------------------------------------------------------------");
		String jsonData = "{\"transactionType\":\"FIND_AVAILABLE\"," +
				   		  "\"userId\":\"" + email + "\"," +
				   		  "\"token\":\"password\"," +
				   		  "\"latitude\":\"41.866381\"," +
				   		  "\"longitude\":\"-87.621374\"," +
				   		  "\"radius\":\"3\"," +
				   		  "\"findItems\":[" +
								"{\"name\":\"Apples\",\"subGroup\":\"\"}," +
								"{\"name\":\"Oranges\",\"subGroup\":\"Gardner\"}]" +
				   		  "}";

		makeRequest(jsonData);
		log.debug("----------------------------------------------------------------------------------------------");
	}
	
	private static String makeRequest(String data) {
		
		System.setProperty("JDBC_CONNECTION_STRING", "jdbc:mysql://localhost:3306/gdelight?user=root&password=");
		
		PostService ps = new PostService();
		
		Response result = ps.postService(data);
		
		String jsonResponse = (String) result.getEntity();
		
		System.out.println("Response - " + jsonResponse);
		
		return jsonResponse;
		
	}
	
	private static void printJsonObject() {
		
		log.debug("--------------------------------------------------------------------");
		log.debug("-------------------------- JSON OBJECTS ----------------------------");
		
		FindAvailableRequestBean bean = new FindAvailableRequestBean();
		bean.setTransactionType(TRANSACTION_TYPE.FIND_AVAILABLE);
		List<Item> items = new ArrayList<Item>();
		
		Item item = new Item();
		item.setAmount("Lots");
		item.setName("Pear Seeds");
		
		items.add(item);
		
		item = new Item();
		item.setAmount("Some");
		item.setName("Apple Seeds");
		
		items.add(item);
		
		bean.setFindItems(items);
		
		String json = JsonUtils.getJSonDocument(bean);
		
		log.debug("FindAvailableRequestBean - " + json);
		
		log.debug("--------------------------------------------------------------------");
	}
	
	public static void TestExternal() {
		
		//String url = "http://localhost:8080/GDelightService/services/testService/";
		String url = "http://gdelight.elasticbeanstalk.com/services/testService/";
		//String url = "http://cmvstaging.mobi/CMVPostService/services/postService/";
		//String url = "https://www.cmvpost.com/CMVPostService/services/postService/";

		
		String xmlData = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>" +
		"<POST></POST>";
		
		String response = "";
		PostMethod post = null;

		try {

			// Prepare HTTP post
			post = new PostMethod(url);
			String requestStr = xmlData;
			post.setRequestEntity(new StringRequestEntity(requestStr, contentType, null));
			post.setRequestHeader("Content-type", contentType);

			// Get HTTP client
			HttpClient httpclient = new HttpClient();

			//make the request
			httpclient.executeMethod(post);

			response = post.getResponseBodyAsString();

			System.out.println(response);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//post.releaseConnection();
		}
		
	}
	
}