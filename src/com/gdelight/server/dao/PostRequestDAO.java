package com.gdelight.server.dao;

import java.text.SimpleDateFormat;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.gdelight.domain.base.BaseRequestBean;
import com.gdelight.server.service.PostServiceException;
import com.gdelight.tools.dao.BaseDAO;

public class PostRequestDAO extends BaseDAO {

	public static final String FIELD_ID = "id";
	public static final String FIELD_REQUEST_ID = "request_id";
	public static final String FIELD_STATUS = "status";
	public static final String FIELD_REQUEST_TOTAL_TIME = "request_total_time";
	public static final String FIELD_REQUEST_TIME = "request_time";
	public static final String FIELD_CLIENT_ID = "client_id";

	private static Logger log = Logger.getLogger(PostRequestDAO.class);
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat(BaseDAO.DATE_TIME_FORMAT);	

	public void addPostRequestResults(BaseRequestBean data) throws PostServiceException {
		try {

			Properties properties = new Properties();
			properties.put(FIELD_REQUEST_ID, data.getInternalId());
			properties.put(FIELD_STATUS, data.getStatus().getStatusType());
			properties.put(FIELD_REQUEST_TIME, dateFormat.format(data.getRequestTime()));
			properties.put(FIELD_REQUEST_TOTAL_TIME, data.getMetrics().getTotalTime());
			
			this.addSimpleData(BaseDAO.DB_VIRALMESH, TableNames.POST_RESULT_METRICS, properties);

		} catch (Exception e) {
			e.printStackTrace();
			log.debug(null,e);
			throw new PostServiceException(e, "Failure while trying to write request results.");
		}

	}
	
}


