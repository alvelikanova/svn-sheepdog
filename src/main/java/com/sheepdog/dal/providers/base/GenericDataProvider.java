package com.sheepdog.dal.providers.base;

import java.io.Serializable;
import java.util.List;

public interface GenericDataProvider<T, ID extends Serializable> {
	public void save(T entity);
	public void merge(T entity);
	public void delete(T entity);
	public T findById(Class clazz, Integer id);
	public List<T> findAll(Class clazz);
}
