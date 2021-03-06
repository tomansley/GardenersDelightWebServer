package com.gdelight.server.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.gdelight.domain.base.BaseRequestBean;
import com.gdelight.domain.item.Item;
import com.gdelight.domain.item.ItemGroup;
import com.gdelight.domain.user.UserBean;
import com.gdelight.server.service.PostServiceException;
import com.gdelight.tools.string.StringUtils;

public class FindAvailableDAO extends AbstractGDelightDAO {

	private static Logger log = Logger.getLogger(FindAvailableDAO.class);
	private static final String ALL = "all";
	
	public FindAvailableDAO(UserBean user) throws PostServiceException {
		super(user);
	}
	
	public List<ItemGroup> findAvailable(List<Item> items, Double latitude, Double longitude, Integer radius) throws PostServiceException {
		log.debug("Starting findAvailable");
		
		List<ItemGroup> availableItems = findItems(items, latitude, longitude, radius);
		
		log.debug("Finished findAvailable");
		return availableItems;
	}
	
	private List<ItemGroup> findItems(List<Item> items, Double latitude, Double longitude, Integer radius) throws PostServiceException {
		
		List<ItemGroup> availableItems = new ArrayList<ItemGroup>();
		
		try {
	
			//get the item names that will be searched for.
			StringBuffer itemNames = new StringBuffer();
			
			// Example: WHERE (item.name = 'Apples' AND subgroup = 'Braeburn') OR (item.name = 'Oranges') 
			for (Item item: items) {
				itemNames.append("(item.name LIKE '");
				if (item.getName().toLowerCase().equals(ALL)) {
					itemNames.append("");
				} else {
					itemNames.append(StringUtils.escapeIllegalSQLChars(item.getName()));
				}
				itemNames.append("%'");
				if (!item.getSubGroup().equals("")) {
					itemNames.append(" AND subgroup = '");
					itemNames.append(StringUtils.escapeIllegalSQLChars(item.getSubGroup()));
					itemNames.append("'");
				}
				itemNames.append(") OR ");
			}
			itemNames.delete(itemNames.lastIndexOf("OR "), itemNames.length()); //get rid of the last OR

			//http://www.scribd.com/doc/2569355/Geo-Distance-Search-with-MySQL
			StringBuffer sqlBuffer = new StringBuffer(
				"SELECT *, 3956 * 2 * ASIN(SQRT( POWER(SIN((" + latitude + " - abs(location.latitude)) * pi()/180 / 2),2) " +
					"+ COS(" + latitude + " * pi()/180 ) * COS(abs(location.latitude) *  pi()/180) * POWER(SIN((" + longitude + " - location.longitude) *  pi()/180 / 2), 2) )) as distance " +
				"FROM available item " +
					"INNER JOIN user_profile user " +
						"ON user.email = item.email " +
					"INNER JOIN user_location location " +
						"ON item.email = location.email " +
							"AND item.location = location.type " +
				"WHERE true = true AND " + itemNames + " " +
					"AND item.active = true " +
				"HAVING distance < " + radius + " " +
				"ORDER BY user.email, distance " +
				"LIMIT 50");
						
			log.debug("SQL = " + sqlBuffer.toString());
			
			PreparedStatement ps = this.getConnection().prepareStatement(sqlBuffer.toString());
			
			ResultSet rs = ps.executeQuery();

			availableItems = getDataFromResultSet(rs);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new PostServiceException(e, "Failure while trying to get user data");
		} finally {
			close();
		}

		return availableItems;
	}
	
	/**
	 * Method to turn a result set into a list of JobData objects
	 * @param rs the result set being translated.
	 * @return the list of JobData objects.
	 * @throws JobDataException
	 */
	private static List<ItemGroup> getDataFromResultSet(ResultSet rs) {

		List<ItemGroup> dataList = new ArrayList<ItemGroup>();
		
		try {

			//go through the rows adding the data
			ItemGroup items = new ItemGroup();
			Item item = null;

			while (rs.next()) {
				
				String username = rs.getString(DatabaseNames.FIELD_EMAIL);
				
				item = new Item();
				item.setActive(rs.getBoolean(DatabaseNames.FIELD_ACTIVE));
				item.setAmount(rs.getString(DatabaseNames.FIELD_AMOUNT));
				item.setDateCreated(rs.getTimestamp(DatabaseNames.FIELD_DATE_CREATED));
				item.setName(rs.getString(DatabaseNames.FIELD_NAME));
				item.setSubGroup(rs.getString(DatabaseNames.FIELD_SUB_GROUP));

				if (items.getUsername().equals("")) {
					items.setUsername(username);
					items.setLocation(rs.getString(DatabaseNames.FIELD_LOCATION));
					items.setLatitude(rs.getDouble(DatabaseNames.FIELD_LATITUDE));
					items.setLongitude(rs.getDouble(DatabaseNames.FIELD_LONGITUDE));
					items.setDistance(rs.getDouble(DatabaseNames.FIELD_DISTANCE));
					items.setOrganic(rs.getBoolean(DatabaseNames.FIELD_ORGANIC));
				}
				
				if (username.equals(items.getUsername())) {
					items.addItem(item);
				} else {
					dataList.add(items);
					items = new ItemGroup();
					items.addItem(item);				
				}
				
			}
			
			dataList.add(items);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return dataList;
	}

	/**
	 * Method which sets all necessary fields into the database for later retrieval and data analysis.
	 * @param bean the bean holding the data being stored into the database.
	 * @throws PostServiceException
	 */
	public void addPostRequestResults(BaseRequestBean bean) throws PostServiceException {
		
		try {

			Properties properties = new Properties();
			
			System.out.println(properties);
			this.addSimpleData(DatabaseNames.USER_PROFILE, properties);

		} catch (Exception e) {
			e.printStackTrace();
			throw new PostServiceException(e, "Failure while trying to write new loan request results.");
		}

	}
	
}


