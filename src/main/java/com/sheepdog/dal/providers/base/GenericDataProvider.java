package com.sheepdog.dal.providers.base;

import java.io.Serializable;
import java.util.List;

public interface GenericDataProvider<T, K, ID extends Serializable> {
	public void save(K entity, Class<T> dalEntityClass);
	public void merge(K entity, Class<T> dalEntityClass);
	public void delete(K entity, Class<T> dalEntityClass); 
	public K findById(Class<T> dalEntityClass, Class<K> domainEntityClass, Integer id);
	public List<K> findAll(Class<T> dalEntityClass, Class<K> domainEntityClass);
}
