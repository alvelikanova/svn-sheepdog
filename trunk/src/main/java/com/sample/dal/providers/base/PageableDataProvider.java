package com.sample.dal.providers.base;

import com.sample.dal.exceptions.DaoException;
import com.sample.dal.providers.pagination.LoadOptions;
import com.sample.dal.providers.pagination.PagedList;

/**
 * Base contract for server side pagination
 * 
 * @author tillias
 */
public interface PageableDataProvider<T> {

	/**
	 * Gets {@link PagedList} of entries using given {@link LoadOptions}
	 * 
	 */
	PagedList<T> getObjects(LoadOptions loadOptions);

	/**
	 * Persists given object. Treats it as new
	 */
	void create(T object) throws DaoException;
}
