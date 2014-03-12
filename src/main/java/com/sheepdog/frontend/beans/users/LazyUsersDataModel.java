package com.sheepdog.frontend.beans.users;

import javax.faces.bean.SessionScoped;

import org.hibernate.exception.JDBCConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.CannotCreateTransactionException;

import com.sheepdog.business.domain.entities.User;
import com.sheepdog.dal.entities.UserEntity;
import com.sheepdog.dal.exceptions.DaoException;
import com.sheepdog.dal.providers.UserDataProvider;
import com.sheepdog.dal.providers.impl.BaseDataProviderImpl;
import com.sheepdog.dal.providers.pagination.LoadOptions;
import com.sheepdog.dal.providers.pagination.PagedList;
import com.sheepdog.utils.frontend.TableDataModel;

@Service
@SessionScoped
public class LazyUsersDataModel extends TableDataModel<User>{
	
	protected static final Logger LOG = LoggerFactory.getLogger(LazyUsersDataModel.class);

	private static final long serialVersionUID = 5386259788419219637L;
	@Autowired
	private UserDataProvider dataProvider;
	
	@Override
	public Object getRowKey(User object) {
		return object.getId();
	}
	@Override
	protected PagedList<User> getData(LoadOptions loadOptions) {
		PagedList<User> result = new PagedList<>();
		try {
			result = dataProvider.getObjects(loadOptions, UserEntity.class, User.class);
		} catch (DaoException ex) {
			LOG.error("Data access error occured while getting objects for table", ex.getMessage());
		} catch (JDBCConnectionException ex) {
			LOG.error("Connection error occured while getting objects for table", ex.getMessage());
		} catch (CannotCreateTransactionException ex) {
			LOG.error("Connection error occured while getting objects for table", ex.getMessage());
		} catch (Exception ex) {
			LOG.error("Unknown error occured while getting objects for table", ex.getMessage());
		}
		return result;
	}

}
