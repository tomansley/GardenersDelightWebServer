package com.gdelight.server.service;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

@Path("/testService")
public class TestService {

	private static Logger log = Logger.getLogger(TestService.class);

	@POST
	public Response postService(String data) {

		StringBuffer response = new StringBuffer();
		response.append("Test ZZ");
		return Response.status(201).entity(response.toString()).build();

	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}

}
