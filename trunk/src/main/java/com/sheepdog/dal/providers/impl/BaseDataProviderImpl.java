package com.sheepdog.dal.providers.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.sheepdog.dal.providers.base.GenericDataProvider;
import com.sheepdog.dal.providers.base.PageableDataProvider;
import com.sheepdog.dal.providers.pagination.LoadOptions;
import com.sheepdog.dal.providers.pagination.PagedList;

public abstract class BaseDataProviderImpl<K, T, ID extends Serializable> implements GenericDataProvider<T,ID>, PageableDataProvider<K>{
//WTF?
	@Autowired
	protected SessionFactory sessionFactory;

	@Override
	public PagedList<K> getObjects(LoadOptions loadOptions, Class dalEntityClass, Class domainEntityClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void save(T entity) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(entity);
	}

	@Override
	public void merge(T entity) {
		Session session = sessionFactory.getCurrentSession();
		session.merge(entity);
	}

	@Override
	public void delete(T entity) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(entity);
	}

	@Override
	public T findById(Class clazz, Integer id) {
		Session session = sessionFactory.getCurrentSession();
		T t = (T)session.get(clazz, id);
		return t;
	}

	@Override
	public List<T> findAll(Class clazz) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("from "+clazz.getSimpleName());
		return (ArrayList<T>) query.list();
	}
}
