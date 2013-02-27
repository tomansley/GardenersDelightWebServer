package com.gdelight.server.helper;

import java.util.Date;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.gdelight.domain.base.BaseRequestBean;
import com.gdelight.domain.base.ErrorException;
import com.gdelight.domain.base.RequestErrorBean;
import com.gdelight.tools.date.DateUtils;
import com.gdelight.tools.xml.XMLUtils;

/**
 * This helper is a bootstrap helper.  It is designed to be available and perform tasks up
 * until the request identifies the helper that should be associated with the request.
 * @author tomansley
 *
 */
public class ProcessingErrorRequestHelper extends AbstractRequestHelper {

	private static final String RESPONSE = "RESPONSE";
	private static final String DATA = "DATA";
	private static final String APPLICATIONID = "APPLICATIONID";
	private static final String STATUS = "STATUS";
	private static final String MESSAGES = "MESSAGES";
	private static final String MESSAGE = "MESSAGE";
	private static final String MESSAGENUMBER = "MESSAGENUMBER";
	private static final String MESSAGEDESC = "MESSAGEDESC";
	private static boolean hasLoadedMessages = false;

	public ProcessingErrorRequestHelper(Document xmlData) {
		super(xmlData);
	}
	
	public void setXMLDocument(Document xmlData) {
		this.xmlData = xmlData;
	}

	@Override
	public StringBuffer convertRequestBeanToXML(BaseRequestBean data) {

		StringBuffer responseStr = new StringBuffer();

		responseStr.append("<" + RESPONSE + ">");

			responseStr.append("<" + HEADER + ">");
				if (data.getTransactionType() == null && xmlData != null) {
					NodeList headerNodes = xmlData.getElementsByTagName(HEADER);
					Element element = (Element) headerNodes.item(0);
					responseStr.append("<" + TRANSACTION_TYPE + ">" + XMLUtils.getValueFromElement(TRANSACTION_TYPE, element) + "</" + TRANSACTION_TYPE + ">");
				} else if (data.getTransactionType() != null) {
					responseStr.append("<" + TRANSACTION_TYPE + ">" + data.getTransactionType() + "</" + TRANSACTION_TYPE + ">");
				} else {
					responseStr.append("<" + TRANSACTION_TYPE + "></" + TRANSACTION_TYPE + ">");
				}
				//responseStr.append("<" + LICENSE + ">" + data.getClient().getLicense() + "</" + LICENSE + ">");
				responseStr.append("<" + TRANSACTIONID + ">" + data.getInternalId() + "</" + TRANSACTIONID + ">");
				responseStr.append("<" + TRANSACTIONTIME + ">" +  DateUtils.convertDateToString(DateUtils.DATE_TIME_FORMAT, new Date()) + "</" + TRANSACTIONTIME + ">");
				responseStr.append("<" + EXTERNALID + ">" + data.getExternalId() + "</" + EXTERNALID + ">");
			responseStr.append("</" + HEADER + ">");
	
	
			//process response data
			responseStr.append("<" + DATA + ">");
				responseStr.append("<" + APPLICATIONID + ">" + data.getInternalId() + "</" + APPLICATIONID + ">");
				responseStr.append("<" + STATUS + ">" + data.getStatus().getStatusType() + "</" + STATUS + ">");
				responseStr.append("<" + MESSAGES + ">");
				if(data.getErrors().size()>0){
					for (RequestErrorBean error : data.getErrors()) { 
						responseStr.append("<" + MESSAGE + ">");
						responseStr.append("<" + MESSAGENUMBER + ">" + error.getErrorCode() + "</" + MESSAGENUMBER + ">");
						responseStr.append("<" + MESSAGEDESC + ">" + error.getErrorMessage()  + "</" + MESSAGEDESC + ">");
						responseStr.append("</" + MESSAGE + ">");
					}
				}
				responseStr.append("</" + MESSAGES + ">");
			responseStr.append("</" + DATA + ">");
		
		responseStr.append("</" + RESPONSE + ">");

		return responseStr;
	}

	//========================================================================================
	// Everything below this line is not implemented as this request helper is only used in
	// cases where there is an error at the early stages of processing.
	//========================================================================================
	
	@Override
	public BaseRequestBean convertXMLToRequestBean() {
		return null;
	}

	@Override
	public BaseRequestBean process(BaseRequestBean dataInput) {
		return null;
	}

	@Override
	protected void setErrorMessages() throws ErrorException {
		if (!hasLoadedMessages) {
			hasLoadedMessages = true;
		}
	}

	@Override
	public boolean addPostRequestResult(BaseRequestBean data) {
		boolean result = true;
//		try {
//			PostRequestDAO dao = new PostRequestDAO();
//			dao.addPostRequestResults(data);
//		} catch (Exception e) {
//			result = false;
//		}
		return result;
	}

	@Override
	public boolean addMetricRequestData(BaseRequestBean data) {
		return false;
	}


}
