package com.sheepdog.dal.providers.base;

import java.io.Serializable;
import java.util.List;

import com.sheepdog.business.domain.entities.PersistentEntity;
import com.sheepdog.dal.entities.GenericDalEntity;

public interface GenericDataProvider<T extends GenericDalEntity<ID>, 
									K extends PersistentEntity<ID>, 
									ID extends Serializable> {
	public void save(K entity, Class<T> dalEntityClass);
	public void merge(K entity, Class<T> dalEntityClass);
	public void delete(K entity, Class<T> dalEntityClass); 
	public K findById(Class<T> dalEntityClass, Class<K> domainEntityClass, ID id);
	public List<K> findAll(Class<T> dalEntityClass, Class<K> domainEntityClass);
}
