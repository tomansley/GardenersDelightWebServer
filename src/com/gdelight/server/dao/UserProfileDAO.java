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

import com.gdelight.domain.user.UserBean;
import com.gdelight.server.service.PostServiceException;
import com.gdelight.tools.dao.BaseDAO;

public class UserProfileDAO extends BaseDAO {

	public static final String IS_ACTIVE = "1";
	public static final String IS_NOT_ACTIVE = "0";

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat(BaseDAO.DATE_TIME_FORMAT);	
	private static Logger log = Logger.getLogger(UserProfileDAO.class);

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
	public UserBean getUser(String email, String password) throws PostServiceException {
		Properties properties = new Properties();
		properties.setProperty(DatabaseNames.FIELD_ACTIVE, IS_ACTIVE);
		properties.setProperty(DatabaseNames.FIELD_EMAIL, email);
		properties.setProperty(DatabaseNames.FIELD_PASSWORD, password);
		List<UserBean> users = getUsers(properties);
		
		UserBean user = null;
		
		if (users.size() != 1) {
			user = new UserBean();
			user.setEmail(email);
			user.setTokenValid(false);
		} else {
			user = users.get(0);
			user.setTokenValid(true);
		}
		
		return user;
	}

	/**
	 * Method which given an email address indicates whether that email address can be used for an account.
	 * @param email the email to be verified.
	 * @return true = email available, false = email not available 
	 * @throws PostServiceException
	 */
	public Boolean isUsernameAvailable(String email) throws PostServiceException {
		Properties properties = new Properties();
		properties.setProperty(DatabaseNames.FIELD_EMAIL, email);
		List<UserBean> users = getUsers(properties);
		Boolean isAvailable = false;
		
		if (users.size() == 0) {
			isAvailable = true;
		}
		
		return isAvailable;
	}

	/**
	 * Method which given arguments returns a list of PostUserBeans
	 * @param properties the arguments given to the SQL statement.
	 * @return a list of PostUserBeans retrieved using the SQL statement.
	 * @throws PostServiceException if an error was caused while trying to retrieve the data.
	 */
	public List<UserBean> getUsers(Properties properties) throws PostServiceException {
		return getUserBeans(properties);
	}
	
	public UserBean createUser(Properties properties) throws PostServiceException {
		return createUserBean(properties);
	}
	
	private UserBean createUserBean(Properties props) throws PostServiceException {
		log.debug("Starting createUserBean");

		UserBean user = null;
		
		try {
	
			StringBuffer sqlBuffer = new StringBuffer("INSERT INTO " + DatabaseNames.USER_PROFILE + " (email, password, first_name, longitude, latitude) VALUES ('" + 
				props.getProperty(DatabaseNames.FIELD_EMAIL) + "','" + 
				props.getProperty(DatabaseNames.FIELD_PASSWORD) + "','" +
				props.getProperty(DatabaseNames.FIELD_FIRST_NAME) + "'," +
				props.get(DatabaseNames.FIELD_LONGITUDE) + "," +
				props.get(DatabaseNames.FIELD_LATITUDE) + ")");
	
			log.debug("SQL = " + sqlBuffer.toString());
			
			PreparedStatement ps = this.getConnection().prepareStatement(sqlBuffer.toString());
			
			ps.execute();
			
			sqlBuffer = new StringBuffer("INSERT INTO " + DatabaseNames.USER_LOCATION + " (email, type, longitude, latitude) VALUES ('" + 
					props.getProperty(DatabaseNames.FIELD_EMAIL) + "','Main Location'," +
					props.get(DatabaseNames.FIELD_LONGITUDE) + "," +
					props.get(DatabaseNames.FIELD_LATITUDE) + ")");
		
			log.debug("SQL = " + sqlBuffer.toString());
			
			ps = this.getConnection().prepareStatement(sqlBuffer.toString());
			
			ps.execute();
				
			user = new UserBean();
			user.setEmail(props.getProperty(DatabaseNames.FIELD_EMAIL));
			user.setToken(props.getProperty(DatabaseNames.FIELD_PASSWORD));
			user.setLatitude((Double) props.get(DatabaseNames.FIELD_LATITUDE));
			user.setLongitude((Double) props.get(DatabaseNames.FIELD_LONGITUDE));
			user.setFirstName(props.getProperty(DatabaseNames.FIELD_FIRST_NAME));
			user.setActive(true);
			user.setTokenValid(true);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new PostServiceException(e, "Failure while trying to get user data");
		} finally {
			close();
		}

		return user;

	}

	/**
	 * Method which given a SQL statement and arguments returns a list of PostUserBeans
	 * @param properties the arguments given to the SQL statement.
	 * @param sqlString the SQL statement to be executed.  If this is empty then one will be created.
	 * @return a list of PostUserBeans retrieved using the SQL statement.
	 * @throws PostServiceException if an error was caused while trying to retrieve the data.
	 */
	private List<UserBean> getUserBeans(Properties properties) throws PostServiceException {
		log.debug("Starting getUserBeans");

		List<UserBean> dataList = null;
		try {

			//put the properties into a linked hashmap which is ordered.
			Map<String,Object> orderedProps = new LinkedHashMap<String,Object>();
			for (Object field: properties.keySet()) {
				orderedProps.put((String) field, properties.getProperty((String) field));
			}

			StringBuffer sqlBuffer = new StringBuffer("SELECT * FROM " + DatabaseNames.USER_PROFILE + " users WHERE true = true AND ");

			for (String key: orderedProps.keySet()) {
				sqlBuffer.append(key + "=? AND ");

			}
			sqlBuffer.delete(sqlBuffer.lastIndexOf("AND"), sqlBuffer.length()); //get rid of the last AND

			log.debug("SQL = " + sqlBuffer.toString());
			PreparedStatement ps = this.getConnection().prepareStatement(sqlBuffer.toString());

			//set the properties into the query
			int i = 1;
			for (String key: orderedProps.keySet()) {
				ps.setObject(i, properties.get(key));
				i++;
			}

			ResultSet rs = ps.executeQuery();

			dataList = getUserBeansFromResultSet(rs);

		} catch (Exception e) {
			e.printStackTrace();
			throw new PostServiceException(e, "Failure while trying to get user data");
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
	private static List<UserBean> getUserBeansFromResultSet(ResultSet rs) throws PostServiceException {

		List<UserBean> dataList = new ArrayList<UserBean>();

		try {

			//go through the rows adding the data
			UserBean data = null;

			while (rs.next()) {

				data = new UserBean();

				data.setFirstName(rs.getString(DatabaseNames.FIELD_FIRST_NAME));
				data.setLastName(rs.getString(DatabaseNames.FIELD_LAST_NAME));
				data.setActive(rs.getBoolean(DatabaseNames.FIELD_ACTIVE));
				data.setEmail(rs.getString(DatabaseNames.FIELD_EMAIL));
				data.setToken(rs.getString(DatabaseNames.FIELD_PASSWORD));
				data.setLatitude(rs.getDouble(DatabaseNames.FIELD_LATITUDE));
				data.setLongitude(rs.getDouble(DatabaseNames.FIELD_LONGITUDE));

				dataList.add(data);

			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new PostServiceException(e, "Failure while trying to get user beans from result set");
		}
		log.debug("Finished creating list of UserBean's and size = " + dataList.size());
		return dataList;
	}

}


