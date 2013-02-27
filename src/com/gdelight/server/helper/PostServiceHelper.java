package com.gdelight.server.helper;

import java.util.UUID;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.gdelight.domain.base.BaseRequestBean;
import com.gdelight.domain.base.BaseRequestBean.TRANSACTION_TYPE;
import com.gdelight.domain.user.PostUserBean;
import com.gdelight.server.dao.PostRequestDAO;
import com.gdelight.server.dao.PostUserProfileDAO;
import com.gdelight.server.service.PostServiceException;
import com.gdelight.tools.xml.XMLUtils;

public class PostServiceHelper {

	/**
	 * Method to get the transaction type provided in an XML request.  Usually this would be
	 * done by performing CMVBaseRequestBean.getTransactionType() but in the early stages of
	 * a request this data is not known.
	 * @param xmlDoc the XML request data.
	 * @return the transaction type of the request.
	 */
	public static TRANSACTION_TYPE getTransactionType(Document xmlDoc) {

		TRANSACTION_TYPE requestType = null;
		NodeList nList = xmlDoc.getElementsByTagName(AbstractRequestHelper.HEADER);
		Element eElement = (Element) nList.item(0);

		String transactionType = XMLUtils.getValueFromElement(AbstractRequestHelper.TRANSACTION_TYPE, eElement);

		TRANSACTION_TYPE[] transactionTypes = TRANSACTION_TYPE.values();
		for (TRANSACTION_TYPE type : transactionTypes) {
			if (type.getTransactionType().equals(transactionType)) {
				requestType = type;
				break;
			}
		}

		return requestType;
		
	}
	
	/**
	 * Method to get a unique ID for the request.
	 * @return a string containing a UUID.
	 * @throws CMVPostServiceException
	 */
	public static String getNextPostSequenceId() throws PostServiceException {
		
		return UUID.randomUUID().toString();
	}

	/**
	 * Method which posts the results of the request to the database.  This includes information
	 * like the request ID, the status or result of the request, who the request originally came 
	 * from (client) and the request time.
	 * @param data the bean holding the request data.
	 * @throws CMVPostServiceException
	 */
	public static void addPostRequestResult(BaseRequestBean data) throws PostServiceException {
		PostRequestDAO dao = new PostRequestDAO();
		dao.addPostRequestResults(data);
	}
	
	/**
	 * Method which gets the client from the license provided. Usually this would be
	 * done by performing CMVBaseRequestBean.getClient() but in the early stages of
	 * a request this data is not necessarily known. 
	 * @param xmlDoc the XML request data
	 * @return the client bean containing all client information
	 */
	public static PostUserBean getPostUser(Document xmlDoc) {
		
		PostUserBean user = null;
		
		try {
			
			NodeList nList = xmlDoc.getElementsByTagName(AbstractRequestHelper.HEADER);
			Element eElement = (Element) nList.item(0);
	
			String license = XMLUtils.getValueFromElement(AbstractRequestHelper.LICENSE, eElement);
	
			if (license == null || license.equals("")) {
				throw new PostServiceException("No valid license provided");
			}
			
			PostUserProfileDAO dao = new PostUserProfileDAO();
			user = dao.getPostClientFromLicense(license);
		} catch (Exception e) {
			e.printStackTrace();
			user = new PostUserBean();
			user.setLicenseValid(false);
		}
		return user;
	}

}
