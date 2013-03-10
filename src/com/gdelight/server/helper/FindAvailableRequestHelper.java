package com.gdelight.server.helper;

import org.apache.log4j.Logger;

import com.gdelight.domain.base.BaseRequestBean;
import com.gdelight.domain.base.BaseResponseBean;
import com.gdelight.domain.base.BaseResponseBean.STATUS_TYPE;
import com.gdelight.domain.base.ErrorException;
import com.gdelight.domain.request.FindAvailableRequestBean;
import com.gdelight.domain.response.FindAvailableResponseBean;
import com.gdelight.server.dao.FindAvailableDAO;
import com.gdelight.server.dao.LoginDAO;
import com.gdelight.server.service.PostServiceException;
import com.gdelight.tools.json.JsonUtils;

/**
 * This class is a helper to save the new items that have been made available by the user.
 * @author tomansley
 */
public class FindAvailableRequestHelper extends AbstractRequestHelper {

	private static Logger log = Logger.getLogger(FindAvailableRequestHelper.class);

	private static boolean hasLoadedMessages = false;

	public FindAvailableRequestHelper(String jsonData) {
		super(jsonData);
	}

	@Override
	public BaseRequestBean convertJsonToRequestBean() {

		BaseRequestBean request = (FindAvailableRequestBean) JsonUtils.parseJSonDocument(jsonData, FindAvailableRequestBean.class);

		return request;
	}

	@Override
	public BaseResponseBean process(BaseRequestBean dataInput) {

		FindAvailableResponseBean response = new FindAvailableResponseBean();
		FindAvailableRequestBean data = (FindAvailableRequestBean) dataInput;
		
		//insert the groups of items here.
		try {
			FindAvailableDAO dao = new FindAvailableDAO(data.getUser());
			response.setItems(dao.findAvailable(data.getFindItems(), data.getLatitude(), data.getLongitude(), data.getRadius()));
			response.setStatus(STATUS_TYPE.SUCCESS);
		} catch (PostServiceException e) {
			e.printStackTrace();
			response.setStatus(STATUS_TYPE.FAILED);
		}
				
		return response;
	}

	@Override
	public String convertResponseBeanToJson(BaseResponseBean bean) {
		
		FindAvailableResponseBean response = (FindAvailableResponseBean) bean;
		
		
		String json = JsonUtils.getJSonDocument(response);
		
		return json;
	}

	@Override
	public boolean addPostRequestResult(BaseRequestBean data) {
		boolean isPosted = true;
		LoginDAO dao = new LoginDAO();
		try {
			dao.addPostRequestResults(data);
		} catch (PostServiceException e) {
			e.printStackTrace();
			log.debug("The loan output from the above stacktrace - " + data);
			isPosted = false;
		}
		return isPosted;
	}

	@Override
	public boolean addMetricRequestData(BaseRequestBean data) {
		boolean isPosted = true;
		//NewLoanPostRequestDAO dao = new NewLoanPostRequestDAO();
		//try {
		//	dao.addPostRequestMetricsResults(data);
		//} catch (CMVPostServiceException e) {
		//	e.printStackTrace();
		//	log.debug("The data output from the above stacktrace - " + data);
		//	isPosted = false;
		//}
		return isPosted;
	}

	@Override
	protected void setErrorMessages() throws ErrorException {
		if (!hasLoadedMessages) {
			this.addErrorMessage(00301, "The request XML is invalid.  No 'USER' node is present. One 'USER' node is required for processing");
			this.addErrorMessage(00302, "This is a test error message.  The request was designed to fail");
			hasLoadedMessages = true;
		}
	}

}
