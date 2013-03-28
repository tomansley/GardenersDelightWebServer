package com.gdelight.server.helper;

import com.gdelight.domain.base.BaseRequestBean;
import com.gdelight.domain.base.BaseResponseBean;
import com.gdelight.domain.base.ErrorException;

/**
 * This helper is a bootstrap helper.  It is designed to be available and perform tasks up
 * until the request identifies the helper that should be associated with the request.
 * @author tomansley
 *
 */
public class ProcessingErrorRequestHelper extends AbstractRequestHelper {

	private static boolean hasLoadedMessages = false;

	public ProcessingErrorRequestHelper(String jsonData) {
		super(jsonData);
	}
	
	public void setJsonDocument(String jsonData) {
		this.jsonData = jsonData;
	}

	@Override
	public String convertResponseBeanToJson(BaseResponseBean data) {

		String responseStr = new String();

		return responseStr;
	}

	//========================================================================================
	// Everything below this line is not implemented as this request helper is only used in
	// cases where there is an error at the early stages of processing.
	//========================================================================================
	
	@Override
	public BaseRequestBean convertJsonToRequestBean() {
		return null;
	}

	@Override
	public BaseResponseBean process(BaseRequestBean dataInput) {
		return null;
	}

	@Override
	public BaseResponseBean processErrorResponse(BaseRequestBean data) {
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
