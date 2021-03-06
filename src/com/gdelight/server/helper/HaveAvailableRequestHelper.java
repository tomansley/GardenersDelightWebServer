package com.gdelight.server.helper;

import org.apache.log4j.Logger;

import com.gdelight.domain.base.BaseRequestBean;
import com.gdelight.domain.base.BaseResponseBean;
import com.gdelight.domain.base.BaseResponseBean.STATUS_TYPE;
import com.gdelight.domain.base.ErrorException;
import com.gdelight.domain.request.HaveAvailableRequestBean;
import com.gdelight.domain.response.HaveAvailableResponseBean;
import com.gdelight.server.dao.LoginDAO;
import com.gdelight.server.dao.HaveAvailableDAO;
import com.gdelight.server.service.PostServiceException;
import com.gdelight.tools.json.JsonUtils;

/**
 * This class is a helper to save the new items that have been made available by the user.
 * @author tomansley
 */
public class HaveAvailableRequestHelper extends AbstractRequestHelper {

	private static Logger log = Logger.getLogger(HaveAvailableRequestHelper.class);

	private static boolean hasLoadedMessages = false;

	public HaveAvailableRequestHelper(String jsonData) {
		super(jsonData);
	}

	@Override
	public BaseRequestBean convertJsonToRequestBean() {

		BaseRequestBean request = (HaveAvailableRequestBean) JsonUtils.parseJSonDocument(jsonData, HaveAvailableRequestBean.class);

		return request;
	}

	@Override
	public BaseResponseBean process(BaseRequestBean dataInput) {

		HaveAvailableResponseBean response = new HaveAvailableResponseBean();
		HaveAvailableRequestBean data = (HaveAvailableRequestBean) dataInput;
		
		//insert the groups of items here.
		try {
			HaveAvailableDAO dao = new HaveAvailableDAO(data.getUser());
			dao.makeAvailable(data.getAvailable());
			response.setStatus(STATUS_TYPE.SUCCESS);
		} catch (PostServiceException e) {
			e.printStackTrace();
			response.setStatus(STATUS_TYPE.FAILED);
		}
				
		return response;
	}

	@Override
	public BaseResponseBean processErrorResponse(BaseRequestBean data) {
		HaveAvailableResponseBean response = new HaveAvailableResponseBean();
		response.setUser(data.getUser());
		response.setErrors(data.getErrors());
		response.setStatus(STATUS_TYPE.FAILED);
		return response;
	}
	
	@Override
	public String convertResponseBeanToJson(BaseResponseBean bean) {
		
		HaveAvailableResponseBean response = (HaveAvailableResponseBean) bean;
		
		
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
			this.addErrorMessage(00201, "The request XML is invalid.  No 'USER' node is present. One 'USER' node is required for processing");
			this.addErrorMessage(00202, "This is a test error message.  The request was designed to fail");
			hasLoadedMessages = true;
		}
	}

}
