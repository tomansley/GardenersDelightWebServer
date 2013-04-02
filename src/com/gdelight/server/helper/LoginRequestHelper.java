package com.gdelight.server.helper;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.gdelight.domain.base.BaseRequestBean;
import com.gdelight.domain.base.BaseResponseBean;
import com.gdelight.domain.base.BaseResponseBean.STATUS_TYPE;
import com.gdelight.domain.base.ErrorException;
import com.gdelight.domain.item.Item;
import com.gdelight.domain.item.ItemGroup;
import com.gdelight.domain.request.LoginRequestBean;
import com.gdelight.domain.response.LoginResponseBean;
import com.gdelight.server.dao.LoginDAO;
import com.gdelight.server.service.PostServiceException;
import com.gdelight.tools.json.JsonUtils;
/**
 * This class is a helper to gather all data after the user has logged in and return it for display on
 * the home page.  All fields are populated as necessary on the request.
 * @author tomansley
 */
public class LoginRequestHelper extends AbstractRequestHelper {

	private static Logger log = Logger.getLogger(LoginRequestHelper.class);

	private static boolean hasLoadedMessages = false;

	public LoginRequestHelper(String jsonData) {
		super(jsonData);
	}

	@Override
	public BaseRequestBean convertJsonToRequestBean() {

		BaseRequestBean request = new LoginRequestBean();

		return request;
	}

	@Override
	public BaseResponseBean process(BaseRequestBean dataInput) {

		//no processing required yet as login is performed earlier for all requests.
		
		LoginRequestBean request = (LoginRequestBean) dataInput;
		
		LoginResponseBean response = new LoginResponseBean();
		response.setUser(request.getUser());
		return response;
	}

	@Override
	public BaseResponseBean processErrorResponse(BaseRequestBean data) {
		LoginResponseBean response = new LoginResponseBean();
		response.setUser(data.getUser());
		response.setErrors(data.getErrors());
		response.setStatus(STATUS_TYPE.FAILED);
		return response;
	}

	@Override
	public String convertResponseBeanToJson(BaseResponseBean bean) {

		LoginResponseBean response = new LoginResponseBean();
		
		List<ItemGroup> available = new ArrayList<ItemGroup>();
		
		ItemGroup group = new ItemGroup();
		group.setName("Seeds");
		
		Item seeds = new Item();
		seeds.setName("Awesome Apple Seeds");
		seeds.setAmount("Lots");
		group.addItem(seeds);
				
		seeds = new Item();
		seeds.setName("Awesome Cherry Seeds");
		seeds.setAmount("Little");
		group.addItem(seeds);
		
		available.add(group);

		response.setAvailable(available);
		
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
			this.addErrorMessage(00101, "The request XML is invalid.  No 'USER' node is present. One 'USER' node is required for processing");
			this.addErrorMessage(00102, "This is a test error message.  The request was designed to fail");
			hasLoadedMessages = true;
		}
	}

}
