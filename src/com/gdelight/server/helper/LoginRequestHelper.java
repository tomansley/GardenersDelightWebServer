package com.gdelight.server.helper;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.gdelight.domain.base.BaseRequestBean;
import com.gdelight.domain.base.ErrorException;
import com.gdelight.domain.base.RequestErrorBean;
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
	private static final String MESSAGES = "MESSAGES";
	private static final String MESSAGE = "MESSAGE";
	private static final String MESSAGENUMBER = "MESSAGENUMBER";
	private static final String MESSAGEDESC = "MESSAGEDESC";

	private static boolean hasLoadedMessages = false;
	//private CMVNewLoanBean data = new CMVNewLoanBean();

	public LoginRequestHelper(Document xmlData) {
		super(xmlData);
	}

	//=======================================================================================
	// Everything below this line is for processing an XML REQUEST into a BaseRequestBean 
	//=======================================================================================

	@Override
	public BaseRequestBean convertXMLToRequestBean() {

		processRequestHeader(null);

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
	public StringBuffer convertRequestBeanToXML(BaseRequestBean bean) {

		StringBuffer responseStr = new StringBuffer();

		responseStr.append("<" + RESPONSE + ">");

		//process response header 
		responseStr.append(processResponseHeader(bean));

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

	private StringBuffer processRejectedData(BaseRequestBean data){

		StringBuffer responseStr = new StringBuffer();

		if(data.getErrors().size() > 0){
			responseStr.append("<" + MESSAGES + ">");
			for (RequestErrorBean bean : data.getErrors()) { 
				responseStr.append("<" + MESSAGE + ">");
				responseStr.append("<" + MESSAGENUMBER + ">" + bean.getErrorCode() + "</" + MESSAGENUMBER + ">");
				responseStr.append("<" + MESSAGEDESC + ">" + bean.getErrorMessage()  + "</" + MESSAGEDESC + ">");
				responseStr.append("</" + MESSAGE + ">");
			}
			responseStr.append("</" + MESSAGES + ">");
		}

		return responseStr;
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
