package com.gdelight.server.dao;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.gdelight.domain.base.BaseRequestBean;
import com.gdelight.domain.item.Item;
import com.gdelight.domain.item.ItemGroup;
import com.gdelight.domain.user.UserBean;
import com.gdelight.server.service.PostServiceException;
import com.gdelight.tools.string.StringUtils;

public class HaveAvailableDAO extends AbstractGDelightDAO {

	private static Logger log = Logger.getLogger(HaveAvailableDAO.class);
	
	public HaveAvailableDAO(UserBean user) throws PostServiceException {
		super(user);
	}
	
	public List<ItemGroup> makeAvailable(List<ItemGroup> items) throws PostServiceException {
		log.debug("Starting makeAvailable");
		
		for (ItemGroup group: items) {
			insertItems(group);
		}
		
		log.debug("Finished makeAvailable");
		return items;
	}
	
	private ItemGroup insertItems(ItemGroup group) throws PostServiceException {
		
		try {
	
			for (Item item: group.getItems()) {
			
				StringBuffer sqlBuffer = new StringBuffer("INSERT INTO " + DatabaseNames.TABLE_AVAILABLE + " (name, amount, subGroup, group_name, email, location) VALUES (");
						
				sqlBuffer.append("'" + StringUtils.escapeIllegalSQLChars(item.getName()) + "',");
				sqlBuffer.append("'" + item.getAmount() + "',");
				sqlBuffer.append("'" + StringUtils.escapeIllegalSQLChars(item.getSubGroup()) + "',");
				sqlBuffer.append("'" + StringUtils.escapeIllegalSQLChars(group.getName()) + "',");
				sqlBuffer.append("'" + this.getUser().getEmail() + "',");
				sqlBuffer.append("'" + StringUtils.escapeIllegalSQLChars(group.getLocation()) + "'");

				sqlBuffer.append(")");
		
				log.debug("SQL = " + sqlBuffer.toString());
				
				PreparedStatement ps = this.getConnection().prepareStatement(sqlBuffer.toString());
				
				ps.execute();

			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new PostServiceException(e, "Failure while trying to get user data");
		} finally {
			close();
		}

		return group;
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
	
	/*private List<String> getGroupNames() throws PostServiceException {
		List<String> groupNames = new ArrayList<String>();
		try {
			
			List<SimpleData> dataList = this.getSimpleData(DatabaseNames.DB_GDELIGHT, DatabaseNames.AVAILABLE_GROUP, new Properties());
			
			for (SimpleData data: dataList) {
				groupNames.add(data.getProperty(DatabaseNames.FIELD_GROUP_NAME));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new PostServiceException(e, "Failure while trying to load group names");
		} finally {
			close();
		}

		return groupNames;
	}*/
	
}


