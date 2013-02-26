package com.gdelight.server.service;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

public class TestServiceTest {
	
	private static String contentType = "text/xml; charset=ISO-8859-1";

	public static void main(String[] args) {
		
		Test();
	}
	
	public static void Test() {
		
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
			int result = httpclient.executeMethod(post);

			response = post.getResponseBodyAsString();

			System.out.println(response);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//post.releaseConnection();
		}
		
	}
	
}