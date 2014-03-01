package com.sheepdog.frontend.beans.users;

import javax.faces.bean.SessionScoped;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sheepdog.business.domain.entities.User;
import com.sheepdog.dal.entities.UserEntity;
import com.sheepdog.dal.providers.UserDataProvider;
import com.sheepdog.dal.providers.pagination.LoadOptions;
import com.sheepdog.dal.providers.pagination.PagedList;
import com.sheepdog.utils.frontend.TableDataModel;

@Service
@SessionScoped
public class LazyUsersDataModel extends TableDataModel<User>{
	
	private static final long serialVersionUID = 5386259788419219637L;
	@Autowired
	private UserDataProvider dataProvider;
	
	@Override
	public Object getRowKey(User object) {
		return object.getId();
	}
	@Override
	protected PagedList<User> getData(LoadOptions loadOptions) {
		return dataProvider.getObjects(loadOptions, UserEntity.class, User.class);
	}

}
