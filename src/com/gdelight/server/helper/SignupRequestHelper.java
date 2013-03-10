package com.gdelight.server.helper;

import org.apache.log4j.Logger;

import com.gdelight.domain.base.BaseRequestBean;
import com.gdelight.domain.base.BaseResponseBean;
import com.gdelight.domain.base.BaseResponseBean.STATUS_TYPE;
import com.gdelight.domain.base.ErrorException;
import com.gdelight.domain.base.RequestErrorBean;
import com.gdelight.domain.request.SignupRequestBean;
import com.gdelight.domain.response.SignupResponseBean;
import com.gdelight.server.dao.SignupDAO;
import com.gdelight.server.service.PostServiceException;
import com.gdelight.tools.json.JsonUtils;

/**
 * This class is a helper to sign a new user up.
 * @author tomansley
 */
public class SignupRequestHelper extends AbstractRequestHelper {

	private static Logger log = Logger.getLogger(SignupRequestHelper.class);

	private static boolean hasLoadedMessages = false;

	public SignupRequestHelper(String jsonData) {
		super(jsonData);
	}

	@Override
	public BaseRequestBean convertJsonToRequestBean() {

		BaseRequestBean request = (SignupRequestBean) JsonUtils.parseJSonDocument(jsonData, SignupRequestBean.class);

		return request;
	}

	@Override
	public BaseResponseBean process(BaseRequestBean dataInput) {

		SignupRequestBean data = (SignupRequestBean) dataInput;
		SignupResponseBean response = new SignupResponseBean();
				
		SignupDAO dao = new SignupDAO();
		
		try {
			
			//if username is available
			if (dao.isUsernameAvailable(data.getUserId())) {
				
				//create new user
				dao.createUser(data.getUserId(), data.getToken(), data.getLatitude(), data.getLongitude());
				
				//everything is good so set success status.
				response.setStatus(STATUS_TYPE.SUCCESS);

			} else {
				
				//problem so throw an error.
				response.setStatus(STATUS_TYPE.FAILED);
				response.addError(new RequestErrorBean(00401));
			}
			
		} catch (PostServiceException e) {
			response.addError(new RequestErrorBean(00402));
		}
		
		return response;
	}

	@Override
	public String convertResponseBeanToJson(BaseResponseBean bean) {
		
		SignupResponseBean response = (SignupResponseBean) bean;
		
		
		String json = JsonUtils.getJSonDocument(response);
		
		return json;
	}

	@Override
	public boolean addPostRequestResult(BaseRequestBean data) {
		boolean isPosted = true;
		SignupDAO dao = new SignupDAO();
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
			this.addErrorMessage(00401, "The username already exists");
			this.addErrorMessage(00402, "There was an error whilst trying to create the account");
			hasLoadedMessages = true;
		}
	}

}
