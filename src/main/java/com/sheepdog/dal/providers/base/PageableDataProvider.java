package com.sheepdog.dal.providers.base;

import com.sheepdog.dal.providers.pagination.LoadOptions;
import com.sheepdog.dal.providers.pagination.PagedList;

public interface PageableDataProvider<T> {

	/**
	 * Gets {@link PagedList} of entries using given {@link LoadOptions}
	 * 
	 */
	PagedList<T> getObjects(LoadOptions loadOptions);
}
