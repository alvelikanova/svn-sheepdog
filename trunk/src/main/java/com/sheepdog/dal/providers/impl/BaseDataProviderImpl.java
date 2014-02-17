package com.sheepdog.dal.providers.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.sheepdog.dal.exceptions.DaoException;
import com.sheepdog.infrastructure.services.MappingService;
import com.sheepdog.utils.CollectionUtils;
import com.sheepdog.dal.providers.pagination.FilterOption;
import com.sheepdog.dal.providers.pagination.SortOption;
import com.sheepdog.dal.providers.base.GenericDataProvider;
import com.sheepdog.dal.providers.base.PageableDataProvider;
import com.sheepdog.dal.providers.pagination.LoadOptions;
import com.sheepdog.dal.providers.pagination.PagedList;

public abstract class BaseDataProviderImpl<T, K, ID extends Serializable> implements GenericDataProvider<T, K, ID>, PageableDataProvider<T, K>{

	protected static final Logger LOG = LoggerFactory.getLogger(BaseDataProviderImpl.class);

	@Autowired
	protected SessionFactory sessionFactory;
	
	@Autowired
	protected MappingService mappingService;
	@Override
	public PagedList<K> getObjects(LoadOptions loadOptions, Class<T> dalEntityClass, Class<K> domainEntityClass) {
        Session session = sessionFactory.getCurrentSession();

        PagedList<K> result = new PagedList<>();

        try {
                Criteria countCriteria = session.createCriteria(dalEntityClass);
                fillCriteria(countCriteria, loadOptions, true);

                long rowsCount = (long) countCriteria.uniqueResult();

                if (rowsCount > 0) {
                        Criteria criteria = session.createCriteria(dalEntityClass);
                        fillCriteria(criteria, loadOptions, false);

                        @SuppressWarnings("unchecked")
						List<T> dataEntities = criteria.list();
                        for (T de : dataEntities) {
                                K domainEntity = mappingService.map(de, domainEntityClass);
                                result.add(domainEntity);
                        }
                }

                result.setTotalSize(rowsCount);
        } catch (Exception ex) {
                LOG.error("Error occured in BaseDataProviderImpl", ex.getMessage());
        }

        return result;
	}

	@Override
	public void save(K entity, Class<T> dalEntityClass) throws DaoException {
		Session session = sessionFactory.getCurrentSession();

		try {
			T dataEntity = mappingService.map(entity, dalEntityClass);
			session.saveOrUpdate(dataEntity);
			
		} catch (Exception ex) {
			LOG.error("Error creating or updating data entity", ex.getMessage());
			throw new DaoException(ex);
		}
	}
	
	
	@Override
	public void merge(K entity, Class<T> dalEntityClass) throws DaoException{
		Session session = sessionFactory.getCurrentSession();

		try {
			T dataEntity = mappingService.map(entity, dalEntityClass);
			session.merge(dataEntity);
			
		} catch (Exception ex) {
			LOG.error("Error creating or updating data entity", ex.getMessage());
			throw new DaoException(ex);
		}
	}
	
//	public void merge(K entity, T dalEntity, Class<T> dalEntityClass)
//	{
//		Session session = sessionFactory.getCurrentSession();
//
//		try {
//			dalEntity = mappingService.map(entity, dalEntityClass);
//			session.merge(dalEntity);
//			
//		} catch (Exception ex) {
//			LOG.error("Error creating or updating data entity", ex.getMessage());
//			throw new DaoException(ex);
//		}
//	}

	@Override
	public void delete(K entity, Class<T> dalEntityClass) throws DaoException{
		Session session = sessionFactory.getCurrentSession();

		try {
			T dataEntity = mappingService.map(entity, dalEntityClass);
			session.delete(dataEntity);
			
		} catch (Exception ex) {
			LOG.error("Error deleting data entity", ex.getMessage());
			throw new DaoException(ex);
		}
	}

	@Override
	public K findById(Class<T> dalEntityClass, Class<K> domainEntityClass, Integer id) {
		Session session = sessionFactory.getCurrentSession();
		K businessEntity = null;
		try {
			Criteria cr = session.createCriteria(dalEntityClass).add(Restrictions.eq("id", id));
			cr.setMaxResults(1);
			@SuppressWarnings("unchecked")
			T dalEntity = (T) cr.uniqueResult();
			businessEntity = mappingService.map(dalEntity, domainEntityClass);
		}
		catch (Exception ex) {
			LOG.error("Error loading business entity", ex.getMessage());
		}
		return businessEntity;
	}

	@Override
	public List<K> findAll(Class<T> dalEntityClass, Class<K> domainEntityClass) {		
		Session session = sessionFactory.getCurrentSession();

		List<K> businessEntities = new ArrayList<>();

		try {
			@SuppressWarnings("unchecked")
			List<T> dataEntities = session.createCriteria(dalEntityClass).list();

			for (T de : dataEntities) {
				K businessEntity = mappingService.map(de, domainEntityClass);
				businessEntities.add(businessEntity);
			}
		} catch (Exception ex) {
			LOG.error("Error loading business entities", ex.getMessage());
		}
		
		return businessEntities;
	}
	
	private void fillCriteria(Criteria source, LoadOptions loadOptions, boolean countOnly) {
		List<FilterOption> filters = loadOptions.getFilters();
		if (CollectionUtils.hasItems(filters)) {
			for (FilterOption f : filters) {
				source.add(Restrictions.eq(f.getField(), f.getValue()));
			}
		}

		List<SortOption> sorts = loadOptions.getSorts();
		Collections.sort(sorts);

		if (CollectionUtils.hasItems(sorts)) {
			for (SortOption s : sorts) {
				switch (s.getSortDirection()) {
				case ASCENDING:
					source.addOrder(Order.asc(s.getSortField()));
					break;
				case DESCENDING:
					source.addOrder(Order.desc(s.getSortField()));
					break;
				default:
					break;
				}
			}
		}

		if (countOnly) {
			source.setProjection(Projections.rowCount());
		} else {
			source.setFirstResult(loadOptions.getSkipItems());
			source.setMaxResults(loadOptions.getTakeItems());
		}
	}
}
