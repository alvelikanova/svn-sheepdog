package com.sheepdog.dal.providers.base;

import com.sheepdog.dal.providers.pagination.LoadOptions;
import com.sheepdog.dal.providers.pagination.PagedList;

public interface PageableDataProvider<K> {

	/**
	 * Gets {@link PagedList} of entries using given {@link LoadOptions}
	 * 
	 */
	public PagedList<K> getObjects(LoadOptions loadOptions, Class dalEntityClass, Class domainEntityClass);
}
