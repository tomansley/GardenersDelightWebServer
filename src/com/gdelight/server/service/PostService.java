package com.gdelight.server.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;

import com.gdelight.domain.base.BaseRequestBean;
import com.gdelight.domain.base.BaseRequestBean.STATUS_TYPE;
import com.gdelight.domain.base.BaseRequestBean.TRANSACTION_TYPE;
import com.gdelight.domain.base.BaseResponseBean;
import com.gdelight.domain.base.RequestErrorBean;
import com.gdelight.domain.base.StarterRequestBean;
import com.gdelight.domain.user.UserBean;
import com.gdelight.server.helper.AbstractRequestHelper;
import com.gdelight.server.helper.FindAvailableRequestHelper;
import com.gdelight.server.helper.LoginRequestHelper;
import com.gdelight.server.helper.HaveAvailableRequestHelper;
import com.gdelight.server.helper.PostServiceHelper;

import com.gdelight.server.helper.ProcessingErrorRequestHelper;
import com.gdelight.server.helper.SignupRequestHelper;
import com.gdelight.tools.json.JsonUtils;
import com.gdelight.tools.xml.XMLUtils;

@Path("/postService")
public class PostService {

	private static Logger log = Logger.getLogger(PostService.class);

	@POST
	public Response postService(String jsonData) {

		Long startTime = System.currentTimeMillis();
		log.debug("postService Request - " + jsonData);
		UserBean user = null;
		String responseStr = null;
		BaseRequestBean request = new StarterRequestBean();
		BaseResponseBean response = null;
		
		AbstractRequestHelper helper = null;

		//first thing we do is make the helper an "error" helper in cases where there 
		//is an early stage error and we have not instantiated an associated helper class.
		helper = new ProcessingErrorRequestHelper(null);
		request.setRequestTime(new Date(startTime));

		try {
						
			jsonData = this.cleanInputData(jsonData);
			//replace any ampersands that might exist before parsing
			jsonData = XMLUtils.escapeIllegalXMLCharacters(jsonData);
			log.debug("Clean postService Request - " + jsonData);
			
			request = (StarterRequestBean) JsonUtils.parseJSonDocument(jsonData, StarterRequestBean.class);

			//if the XML doc parsed successfully then place into helper
			((ProcessingErrorRequestHelper) helper).setJsonDocument(jsonData);
			
			//now that we know the transaction type we can create the corresponding bean
			if (request.getTransactionType() == null) {
				request.addError(new RequestErrorBean(20002), STATUS_TYPE.REJECTED);
			} else if (request.getTransactionType().equals(TRANSACTION_TYPE.LOGIN)) {
				helper = new LoginRequestHelper(jsonData);
			} else if (request.getTransactionType().equals(TRANSACTION_TYPE.HAVE_AVAILABLE)) {
				helper = new HaveAvailableRequestHelper(jsonData);
			} else if (request.getTransactionType().equals(TRANSACTION_TYPE.FIND_AVAILABLE)) {
				helper = new FindAvailableRequestHelper(jsonData);
			} else if (request.getTransactionType().equals(TRANSACTION_TYPE.SIGNUP)) {
				helper = new SignupRequestHelper(jsonData);
			}
				
			//now we can ensure its a valid user and get their details
			if (!request.hasErrors() && request.getTransactionType() != TRANSACTION_TYPE.SIGNUP) {
				user = PostServiceHelper.getPostUser(request);
				if (!user.isTokenValid()) {
					request.addError(new RequestErrorBean(20003), STATUS_TYPE.REJECTED);
				}
				
			}
		
			//convert Json to bean. After conversion we will have an entirely new bean 
			//so we need to re-add all the data we previously added to the temp bean
			if (!request.hasErrors()) {
				request = helper.convertJsonToRequestBean();
				request.getMetrics().setStartTime(startTime);
				request.setUser(user);
			}
				
			//we now have a bean that is fully populated and ready for processing.
			if (!request.hasErrors()) {
				response = helper.process(request);
			} else {
				response = helper.processErrorResponse(request);
			}
	
		} catch (Exception e) {
			e.printStackTrace();
			List<String> args = new ArrayList<String>();
			args.add(ExceptionUtils.getStackTrace(e));
			request.addError(new RequestErrorBean(20001, args), STATUS_TYPE.REJECTED);
		}
		
		responseStr = helper.convertResponseBeanToJson(response);

		//the last thing we do is log the request and response.
		try {
			
			//long totalTime = System.currentTimeMillis() - startTime;
			//data.getMetrics().setTotalTime(totalTime);
			
			//log basic request data
			//PostServiceHelper.addPostRequestResult(data);
			
			//log specific request results and metrics.
			//helper.addPostRequestResult(data);
			//helper.addMetricRequestData(data);
			
			//Properties props = new Properties();
			//props.put(MongoLogger.REQUEST, xmlData);
			//props.put(MongoLogger.RESPONSE, response.toString());
			//props.put(MongoLogger.INTERNAL_ID, data.getInternalId());
			//props.put(MongoLogger.START_TIME, new Long(startTime));
			//props.put(MongoLogger.TOTAL_TIME, new Long(totalTime));
			//MongoLogger.log(props, this);
			
		} catch (Exception e) { e.printStackTrace(); }
	
		log.debug("Response - " + responseStr);
		
		return Response.status(201).entity(responseStr).build();

	}
	
	private String cleanInputData(String jsonData) throws UnsupportedEncodingException {

		//if coming from android the request comes through as a parameter
		jsonData = jsonData.replaceFirst("json=", "");
		
		//get rid of all special characters
		jsonData = URLDecoder.decode(jsonData, "UTF-8");
		
		//replace any ampersands that might exist before parsing
		jsonData = XMLUtils.escapeIllegalXMLCharacters(jsonData);
		
		return jsonData;
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}

}
