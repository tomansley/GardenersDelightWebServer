package com.gdelight.server.dao;

import com.gdelight.domain.user.UserBean;
import com.gdelight.tools.dao.BaseDAO;

public abstract class AbstractGDelightDAO extends BaseDAO {

	private UserBean user = null;
	
	public AbstractGDelightDAO(UserBean user) {
		this.setUser(user);
	}

	public UserBean getUser() {
		return user;
	}

	public void setUser(UserBean user) {
		this.user = user;
	}

}


