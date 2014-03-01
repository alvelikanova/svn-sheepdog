package com.sheepdog.dal.providers.base;

import java.io.Serializable;

import com.sheepdog.dal.providers.pagination.LoadOptions;
import com.sheepdog.dal.providers.pagination.PagedList;

public interface PageableDataProvider<T, K, ID extends Serializable> extends GenericDataProvider<T, K, ID>{

	/**
	 * Gets {@link PagedList} of entries using given {@link LoadOptions}
	 * 
	 */
	public PagedList<K> getObjects(LoadOptions loadOptions, Class<T> dalEntityClass, Class<K> domainEntityClass);
}
