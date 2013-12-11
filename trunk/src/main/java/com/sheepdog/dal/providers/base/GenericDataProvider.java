package com.sheepdog.dal.providers.base;

import java.io.Serializable;
import java.util.List;

public interface GenericDataProvider<T,K,ID extends Serializable> {
	public void save(K entity, Class dalEntityClass);
	public void merge(T entity);
	public void delete(T entity);
	public T findById(Class clazz, Integer id);
	public List<K> findAll(Class dalEntityClass, Class domainEntityClass);
}
