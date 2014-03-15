package com.sheepdog.dal.providers.base;

import java.io.Serializable;

import com.sheepdog.business.domain.entities.PersistentEntity;
import com.sheepdog.dal.entities.GenericDalEntity;
import com.sheepdog.dal.providers.pagination.LoadOptions;
import com.sheepdog.dal.providers.pagination.PagedList;

public interface PageableDataProvider<T extends GenericDalEntity<ID>, 
										K extends PersistentEntity<ID>, 
										ID extends Serializable> 
		extends GenericDataProvider<T, K, ID> {

	/**
	 * Gets {@link PagedList} of entries using given {@link LoadOptions}
	 * 
	 */
	public PagedList<K> getObjects(LoadOptions loadOptions, Class<T> dalEntityClass, Class<K> domainEntityClass);
}
