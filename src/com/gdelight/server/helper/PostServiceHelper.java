package com.gdelight.server.helper;

import com.gdelight.domain.base.BaseRequestBean;
import com.gdelight.domain.user.UserBean;
import com.gdelight.server.dao.PostRequestDAO;
import com.gdelight.server.dao.UserProfileDAO;
import com.gdelight.server.service.PostServiceException;

public class PostServiceHelper {

	/**
	 * Method which posts the results of the request to the database.  This includes information
	 * like the request ID, the status or result of the request, who the request originally came 
	 * from (client) and the request time.
	 * @param data the bean holding the request data.
	 * @throws CMVPostServiceException
	 */
	public static void addPostRequestResult(BaseRequestBean data) throws PostServiceException {
		PostRequestDAO dao = new PostRequestDAO(data.getUser());
		dao.addPostRequestResults(data);
	}
	
	/**
	 * Method which gets the client from the license provided. Usually this would be
	 * done by performing CMVBaseRequestBean.getClient() but in the early stages of
	 * a request this data is not necessarily known. 
	 * @param xmlDoc the XML request data
	 * @return the client bean containing all client information
	 */
	public static UserBean getPostUser(BaseRequestBean request) {
		
		UserBean user = null;
		
		try {
			
			UserProfileDAO dao = new UserProfileDAO();
			user = dao.getUser(request.getUserId(), request.getToken());
		} catch (Exception e) {
			e.printStackTrace();
			user = new UserBean();
			user.setTokenValid(false);
		}
		return user;
	}

}
