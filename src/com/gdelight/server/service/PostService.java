package com.gdelight.server.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

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
import com.gdelight.domain.base.RequestErrorBean;
import com.gdelight.domain.base.StarterRequestBean;
import com.gdelight.domain.user.PostUserBean;
import com.gdelight.server.helper.AbstractRequestHelper;
import com.gdelight.server.helper.LoginRequestHelper;
import com.gdelight.server.helper.PostServiceHelper;
import com.gdelight.server.helper.ProcessingErrorRequestHelper;
import com.gdelight.tools.json.JsonUtils;
import com.gdelight.tools.xml.XMLUtils;

@Path("/postService")
public class PostService {

	private static Logger log = Logger.getLogger(PostService.class);

	@POST
	public Response postService(String xmlData) {

		Long startTime = System.currentTimeMillis();
		log.debug("postService Request - " + xmlData);
		PostUserBean client = null;
		StringBuffer response = new StringBuffer();
		BaseRequestBean data = new StarterRequestBean();
		AbstractRequestHelper helper = null;

		//first thing we do is make the helper an "error" helper in cases where there 
		//is an early stage error and we have not instantiated an associated helper class.
		helper = new ProcessingErrorRequestHelper(null);
		data.setRequestTime(new Date(startTime));

		try {
						
			//replace any ampersands that might exist before parsing
			xmlData = XMLUtils.escapeIllegalXMLCharacters(xmlData);
			
			xmlDoc = JsonUtils.parseJSonDocument(xmlData);

			//if the XML doc parsed successfully then place into helper
			((ProcessingErrorRequestHelper) helper).setXMLDocument(xmlDoc);
			
			//get the transaction type
			data.setTransactionType(PostServiceHelper.getTransactionType(xmlDoc));
			
			//now that we know the transaction type we can create the corresponding bean
			if (data.getTransactionType() == null) {
				data.addError(new RequestErrorBean(20002), STATUS_TYPE.REJECTED);
			} else if (data.getTransactionType().equals(TRANSACTION_TYPE.LOGIN)) {
				helper = new LoginRequestHelper(xmlDoc);
			}
				
			//now we can get the client that made the request
			if (!data.hasErrors()) {
				client = PostServiceHelper.getPostClient(xmlDoc);
				if (!client.isLicenseValid()) {
					data.addError(new RequestErrorBean(20003), STATUS_TYPE.REJECTED);
				}
				
			}
		
			//convert XML to bean. After conversion we will have an entirely new bean 
			//so we need to re-add all the data we previously added to the temp bean
			if (!data.hasErrors()) {
				data = helper.convertXMLToRequestBean();
				data.setRequestTime(new Date(startTime));
				data.getMetrics().setStartTime(startTime);
				data.setUser(client);
			}
				
			//we now have a bean that is fully populated and ready for processing.
			if (!data.hasErrors()) {
				data = helper.process(data);
			}
	
		} catch (Exception e) {
			e.printStackTrace();
			List<String> args = new ArrayList<String>();
			args.add(ExceptionUtils.getStackTrace(e));
			data.addError(new RequestErrorBean(20001, args), STATUS_TYPE.REJECTED);
		}
		
		response = helper.convertRequestBeanToXML(data);

		//the last thing we do is log the request and response.
		try {
			
			long totalTime = System.currentTimeMillis() - startTime;
			data.getMetrics().setTotalTime(totalTime);
			
			//log basic request data
			PostServiceHelper.addPostRequestResult(data);
			
			//log specific request results and metrics.
			helper.addPostRequestResult(data);
			helper.addMetricRequestData(data);
			
			Properties props = new Properties();
			//props.put(MongoLogger.REQUEST, xmlData);
			//props.put(MongoLogger.RESPONSE, response.toString());
			//props.put(MongoLogger.INTERNAL_ID, data.getInternalId());
			//props.put(MongoLogger.START_TIME, new Long(startTime));
			//props.put(MongoLogger.TOTAL_TIME, new Long(totalTime));
			//MongoLogger.log(props, this);
			
		} catch (Exception e) { e.printStackTrace(); }
	
		log.debug("Response - " + response.toString());
		
		return Response.status(201).entity(response.toString()).build();

	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}

}
