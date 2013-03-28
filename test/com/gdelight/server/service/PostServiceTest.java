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
import com.gdelight.tools.string.StringUtils;

public class PostServiceTest {
	
	private static String contentType = "text/xml; charset=ISO-8859-1";
	private static Logger log = Logger.getLogger(PostServiceTest.class);

	public static void main(String[] args) {
		
		String type = "external";
		String email = "tomansley@gmail.com" + System.currentTimeMillis();
		String password = "test";
		String url = "http://localhost:8080/GDelightService/services/postService/";
		//String url = "http://gdelight.elasticbeanstalk.com/services/postService/";
		//String url = "http://cmvstaging.mobi/CMVPostService/services/postService/";
		//String url = "https://www.cmvpost.com/CMVPostService/services/postService/";


		if (type.equals("external")) {
			log.debug("----------------------------------------------------------------------------------------------");
			String jsonData = getSignupJson(email, password);
			String response = makeExternalRequest(url, jsonData);
			
			log.debug("----------------------------------------------------------------------------------------------");

			log.debug("----------------------------------------------------------------------------------------------");
			jsonData = getLoginJson(email, password);
			response = makeExternalRequest(url, jsonData);
			log.debug("----------------------------------------------------------------------------------------------");
			log.debug("----------------------------------------------------------------------------------------------");
			jsonData = getMakeAvailableJson(email, password);
			response = makeExternalRequest(url, jsonData);
			log.debug("----------------------------------------------------------------------------------------------");
			log.debug("----------------------------------------------------------------------------------------------");
			jsonData = getFindAvailableJson(email, password);
			response = makeExternalRequest(url, jsonData);
			log.debug("----------------------------------------------------------------------------------------------");
		} else if (type.equals("internal")){
			log.debug("----------------------------------------------------------------------------------------------");
			String jsonData = getSignupJson(email, password);
			String response = makeInternalRequest(jsonData);
			
			log.debug("----------------------------------------------------------------------------------------------");

			log.debug("----------------------------------------------------------------------------------------------");
			jsonData = getLoginJson(email, password);
			response = makeInternalRequest(jsonData);
			log.debug("----------------------------------------------------------------------------------------------");
			log.debug("----------------------------------------------------------------------------------------------");
			jsonData = getMakeAvailableJson(email, password);
			response = makeInternalRequest(jsonData);
			log.debug("----------------------------------------------------------------------------------------------");
			log.debug("----------------------------------------------------------------------------------------------");
			jsonData = getFindAvailableJson(email, password);
			response = makeInternalRequest(jsonData);
			log.debug("----------------------------------------------------------------------------------------------");
		} else if (type.equals("special")) {
			
			String jsonData = "json=%7B%22latitude%22%3A-1.0%2C%22longitude%22%3A-1.0%2C%22errors%22%3A%5B%5D%2C%22lastRequestTime%22%3A%22Dec+31%2C+1969+6%3A00%3A00+PM%22%2C%22metrics%22%3A%7B%22startTime%22%3A0%2C%22totalTime%22%3A0%7D%2C%22requestTime%22%3A%22Mar+19%2C+2013+3%3A51%3A30+PM%22%2C%22status%22%3A%22INITIAL%22%2C%22token%22%3A%22qwer%22%2C%22transactionType%22%3A%22SIGNUP%22%2C%22userId%22%3A%22qwerty%22%7D";
			String response = makeInternalRequest(jsonData);
		}
		//printJsonObject();
	}
	
	public static String getSignupJson(String email, String password) {
		
		return "{\"transactionType\":\"SIGNUP\"," +
				   "\"userId\":\"" + email + "\"," +
				   "\"token\":\"" + password + "\"," +
			   	   "\"latitude\":\"41.866381\"," +
			       "\"longitude\":\"-87.621374\"}";

	}
	
	public static String getLoginJson(String email, String password) {
		
		return "{\"transactionType\":\"LOGIN\"," +
						   "\"userId\":\"" + email + "\"," +
						   "\"token\":\"" + password + "\"}";
		

	}
	
	public static String getMakeAvailableJson(String email, String password) {
		
		return "{\"transactionType\":\"MAKE_AVAILABLE\"," +
				   		  "\"userId\":\"" + email + "\"," +
						   "\"token\":\"" + password + "\"," +
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

	}
	
	public static String getFindAvailableJson(String email, String password) {
		
		return "{\"transactionType\":\"FIND_AVAILABLE\"," +
				   		  "\"userId\":\"" + email + "\"," +
						   "\"token\":\"" + password + "\"," +
				   		  "\"latitude\":\"41.866381\"," +
				   		  "\"longitude\":\"-87.621374\"," +
				   		  "\"radius\":\"3\"," +
				   		  "\"findItems\":[" +
								"{\"name\":\"Apples\",\"subGroup\":\"\"}," +
								"{\"name\":\"Oranges\",\"subGroup\":\"Gardner\"}]" +
				   		  "}";

	}
	
	private static String makeInternalRequest(String data) {
		
		System.setProperty("JDBC_CONNECTION_STRING", "jdbc:mysql://localhost:3306/gdelight?user=root&password=Afr1cansky");
		
		PostService ps = new PostService();
		
		Response result = ps.postService(data);
		
		String jsonResponse = (String) result.getEntity();
		
		System.out.println("Response - " + jsonResponse);
		
		return jsonResponse;
		
	}
	
	private static String makeExternalRequest(String url, String data) {
		
		String response = "";
		PostMethod post = null;

		try {

			// Prepare HTTP post
			post = new PostMethod(url);
			String requestStr = data;
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
		
		return response;
		
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
	
}