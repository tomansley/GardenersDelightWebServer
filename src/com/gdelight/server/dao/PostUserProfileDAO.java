package com.gdelight.server.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import com.gdelight.domain.user.PostUserBean;
import com.gdelight.server.service.PostServiceException;
import com.gdelight.tools.dao.BaseDAO;

public class PostUserProfileDAO extends BaseDAO {

	public static final String FIELD_ID = "id";
	public static final String FIELD_NAME = "name";
	public static final String FIELD_ACTIVE = "active";
	public static final String FIELD_LICENSE = "license";
	public static final String FIELD_REVENUE_SHARE = "revenue_share";
	public static final String FIELD_AFFILIATE_ID = "affiliate_id";
	public static final String FIELD_DEFAULT_TIER = "default_tier";
	public static final String IS_ACTIVE = "1";
	public static final String IS_NOT_ACTIVE = "0";

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat(BaseDAO.DATE_TIME_FORMAT);	
	private static Logger log = Logger.getLogger(PostUserProfileDAO.class);

	static {
		TimeZone estTimeZone = TimeZone.getTimeZone("CST");
		dateFormat.setTimeZone(estTimeZone);
	}

	/**
	 * Method which given a license returns a PostUserBean.  This method only returns
	 * those clients that are active.
	 * @param license the license used to determine the client.
	 * @return the list of clients who's license matches the 
	 * @throws PostServiceException
	 */
	public PostUserBean getPostClientFromLicense(String license) throws PostServiceException {
		Properties properties = new Properties();
		properties.setProperty(FIELD_ACTIVE, IS_ACTIVE);
		properties.setProperty(FIELD_LICENSE, license);
		List<PostUserBean> clients = getPostClients(properties);
		
		PostUserBean client = null;
		
		if (clients.size() != 1) {
			client = new PostUserBean();
			client.setLicense(license);
			client.setLicenseValid(false);
		} else {
			client = clients.get(0);
			client.setLicenseValid(true);
		}
		
		return client;
	}

	/**
	 * Method which given arguments returns a list of PostUserBeans
	 * @param properties the arguments given to the SQL statement.
	 * @return a list of PostUserBeans retrieved using the SQL statement.
	 * @throws PostServiceException if an error was caused while trying to retrieve the data.
	 */
	public List<PostUserBean> getAllPostClients() throws PostServiceException {
		return getPostClients(new Properties());
	}

	/**
	 * Method which given arguments returns a list of PostUserBeans
	 * @param properties the arguments given to the SQL statement.
	 * @return a list of PostUserBeans retrieved using the SQL statement.
	 * @throws PostServiceException if an error was caused while trying to retrieve the data.
	 */
	public List<PostUserBean> getClients(Properties properties) throws PostServiceException {
		return getPostClients(properties);
	}

	/**
	 * Method which given a SQL statement and arguments returns a list of PostUserBeans
	 * @param properties the arguments given to the SQL statement.
	 * @param sqlString the SQL statement to be executed.  If this is empty then one will be created.
	 * @return a list of PostUserBeans retrieved using the SQL statement.
	 * @throws PostServiceException if an error was caused while trying to retrieve the data.
	 */
	private List<PostUserBean> getPostClients(Properties properties) throws PostServiceException {
		log.debug("Starting getLenders");

		List<PostUserBean> dataList = null;
		try {

			//put the properties into a linked hashmap which is ordered.
			Map<String,Object> orderedProps = new LinkedHashMap<String,Object>();
			for (Object field: properties.keySet()) {
				orderedProps.put((String) field, properties.getProperty((String) field));
			}

			StringBuffer sqlBuffer = new StringBuffer("SELECT * FROM " + TableNames.POST_USER_PROFILE + " clients WHERE true = true AND ");

			for (String key: orderedProps.keySet()) {
				sqlBuffer.append(key + "=? AND ");

			}
			sqlBuffer.delete(sqlBuffer.lastIndexOf("AND"), sqlBuffer.length()); //get rid of the last AND

			log.debug("SQL = " + sqlBuffer.toString());
			PreparedStatement ps = this.getConnection(BaseDAO.DB_VIRALMESH).prepareStatement(sqlBuffer.toString());

			//set the properties into the query
			int i = 1;
			for (String key: orderedProps.keySet()) {
				ps.setObject(i, properties.get(key));
				i++;
			}

			ResultSet rs = ps.executeQuery();

			dataList = getPostUserBeansFromResultSet(rs);

		} catch (Exception e) {
			e.printStackTrace();
			throw new PostServiceException(e, "Failure while trying to get lender data");
		} finally {
			close();
		}

		return dataList;
	}

	/**
	 * Method to turn a result set into a list of PostUserBean objects
	 * @param rs the result set being translated.
	 * @return the list of PromoReportData objects.
	 * @throws PostServiceException if there was an error while translating the result set.
	 */
	private static List<PostUserBean> getPostUserBeansFromResultSet(ResultSet rs) throws PostServiceException {

		List<PostUserBean> dataList = new ArrayList<PostUserBean>();

		try {

			//go through the rows adding the data
			PostUserBean data = null;

			while (rs.next()) {

				data = new PostUserBean();

				data.setId(rs.getInt(FIELD_ID));
				data.setName(rs.getString(FIELD_NAME));
				data.setActive(rs.getBoolean(FIELD_ACTIVE));
				data.setLicense(rs.getString(FIELD_LICENSE));
				data.setRevenueShare(rs.getDouble(FIELD_REVENUE_SHARE));
				data.setAffiliateId(rs.getInt(FIELD_AFFILIATE_ID));
				data.setDefaultTier(rs.getInt(FIELD_DEFAULT_TIER));

				dataList.add(data);

			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new PostServiceException(e, "Failure while trying to get lender beans from result set");
		}
		log.debug("Finished creating list of PostUserBean's and size = " + dataList.size());
		return dataList;
	}

}


