package com.gdelight.server.helper;

import org.apache.log4j.Logger;

import com.gdelight.domain.base.BaseRequestBean;
import com.gdelight.domain.base.ErrorException;
import com.gdelight.server.dao.LoginPostRequestDAO;
import com.gdelight.server.service.PostServiceException;

/**
 * This class is a helper to perform one task and that is to convert the received XML data into a
 * CMVNewLoanBean and back again.  All fields are populated as necessary on the request.  No rule 
 * checking is performed on the data.
 * @author tomansley
 */
public class LoginRequestHelper extends AbstractRequestHelper {

	private static Logger log = Logger.getLogger(LoginRequestHelper.class);

	private static final String RESPONSE = "RESPONSE";
	private static final String DATA = "DATA";

	private static boolean hasLoadedMessages = false;
	//private CMVNewLoanBean data = new CMVNewLoanBean();

	public LoginRequestHelper(String jsonData) {
		super(jsonData);
	}

	//=======================================================================================
	// Everything below this line is for processing an XML REQUEST into a BaseRequestBean 
	//=======================================================================================

	@Override
	public BaseRequestBean convertJsonToRequestBean() {

		//processUserData();

		return null;
	}

	@Override
	public BaseRequestBean process(BaseRequestBean dataInput) {

		//CMVNewLoanBean data = (CMVNewLoanBean) dataInput;

		//perform logic here for login
		//...
		
		return null;
	}

	@Override
	public StringBuffer convertRequestBeanToJson(BaseRequestBean bean) {

		StringBuffer responseStr = new StringBuffer();

		responseStr.append("<" + RESPONSE + ">");

		//process response data
		//responseStr.append("<" + DATA + ">");
		//if (bean.getStatus().equals(BaseRequestBean.STATUS_TYPE.ACCEPTED)) {
		//	responseStr.append(processAcceptedData((CMVNewLoanBean) bean));
		//} else if (bean.getStatus().equals(BaseRequestBean.STATUS_TYPE.REJECTED)
		//		|| bean.getStatus().equals(BaseRequestBean.STATUS_TYPE.DECLINED)) {
		//	responseStr.append(processRejectedData(bean));
		//}
		responseStr.append("</" + DATA + ">");
		responseStr.append("</" + RESPONSE + ">");

		return responseStr;
	}

	@Override
	public boolean addPostRequestResult(BaseRequestBean data) {
		boolean isPosted = true;
		LoginPostRequestDAO dao = new LoginPostRequestDAO();
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
			this.addErrorMessage(20203, "The request XML is invalid.  No 'USER' node is present. One 'USER' node is required for processing");
			this.addErrorMessage(20207, "This is a test error message.  The request was designed to fail");
			hasLoadedMessages = true;
		}
	}

}
