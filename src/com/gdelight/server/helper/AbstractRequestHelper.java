package com.gdelight.server.helper;

import com.gdelight.domain.base.BaseErrorMessages;
import com.gdelight.domain.base.BaseRequestBean;
import com.gdelight.domain.base.ErrorException;
import com.gdelight.server.service.PostServiceException;

public abstract class AbstractRequestHelper {

	public static final String HEADER = "HEADER";
	public static final String TRANSACTION_TYPE = "TRANSACTIONTYPE";
	public static final String TEST = "TEST";
	public static final String LICENSE = "LICENSE";
	public static final String TRANSACTIONID = "TRANSACTIONID";
	public static final String TRANSACTIONTIME = "TRANSACTIONTIME";
	public static final String EXTERNALID = "EXTERNALID";
	private static boolean hasLoadedMessages = false;

	protected String jsonData = null;

	public AbstractRequestHelper(String jsonData) {
		this.jsonData = jsonData;
		try {
			this.initErrorMessages();
			this.setErrorMessages();
		} catch (ErrorException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method to convert the XML request into a bean that can be processed.  This is one of the first methods
	 * called and is called before the bean is processed.
	 * @return a bean that extends BaseRequestBean
	 */
	public abstract BaseRequestBean convertJsonToRequestBean();
	
	/**
	 * Method to process the request.  This method gets called after the bean has created via the convertXMLToRequestBean() method.
	 * @param data the data being processed.  This data is received via the posting service and converted from XML.
	 * @return the request bean holding the new processed information.
	 */
	public abstract BaseRequestBean process(BaseRequestBean data);
	
	/**
	 * Method to convert the requests response back to XML format.  This XML is then returned in the response to the client.
	 * @param data the request bean holding the response data.
	 * @return the XML string 
	 */
	public abstract StringBuffer convertRequestBeanToJson(BaseRequestBean data);

	protected abstract void setErrorMessages() throws ErrorException;
	
	/**
	 * Method to allow for the addition of the request results to the database.  This is one of the last
	 * methods called before the response is returned to the user.  This method should store only that
	 * data which is specific to this helper and not data that is abstract and generically held by the 
	 * BaseRequestBean.
	 * @param data the bean holding the response data to be stored.
	 * @return true = stored successfully, false = unsuccessful request to store data.
	 */
	public abstract boolean addPostRequestResult(BaseRequestBean data) throws PostServiceException;
	
	/**
	 * Method to allow for the addition of the request metrics to the database.  This is one of the last
	 * methods called before the response is returned to the user.  This method should store only those
	 * metrics which are specific to this helper and not data that is abstract and generically held by the 
	 * CMVBaseRequestMetricBean.
	 * @param data the bean holding the metric data to be stored.
	 * @return true = stored successfully, false = unsuccessful request to store data.
	 */
	public abstract boolean addMetricRequestData(BaseRequestBean data) throws PostServiceException;


	protected void addErrorMessage(Integer code, String message) throws ErrorException {
		BaseErrorMessages.addErrorMessage(code, message);
	}

	private void initErrorMessages() throws ErrorException {
		if (!hasLoadedMessages) {
			addErrorMessage(20000, "The error is unknown");
			addErrorMessage(20001, "The XML request could not be correctly parsed.");
			addErrorMessage(20002, "The transaction type provided is not recognized as a supported transaction type for this service");
			addErrorMessage(20003, "The license key provided is invalid");
			hasLoadedMessages = true;
		}
	}
	
}
