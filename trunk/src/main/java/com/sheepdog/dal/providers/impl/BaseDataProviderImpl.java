package com.sheepdog.dal.providers.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.sheepdog.infrastructure.services.MappingService;
import com.sheepdog.utils.CollectionUtils;
import com.sheepdog.dal.providers.pagination.FilterOption;
import com.sheepdog.dal.providers.pagination.SortOption;
import com.sheepdog.dal.providers.base.GenericDataProvider;
import com.sheepdog.dal.providers.base.PageableDataProvider;
import com.sheepdog.dal.providers.pagination.LoadOptions;
import com.sheepdog.dal.providers.pagination.PagedList;

public abstract class BaseDataProviderImpl<K, T, ID extends Serializable> implements GenericDataProvider<T,ID>, PageableDataProvider<K>{

	private static final Logger LOG = LoggerFactory.getLogger(BaseDataProviderImpl.class);

	@Autowired
	protected SessionFactory sessionFactory;
	
	@Autowired
	private MappingService mappingService;
	@Override
	public PagedList<K> getObjects(LoadOptions loadOptions, Class dalEntityClass, Class domainEntityClass) {
        Session session = sessionFactory.getCurrentSession();

        PagedList<K> result = new PagedList<>();

        try {
                Criteria countCriteria = session.createCriteria(dalEntityClass);
                fillCriteria(countCriteria, loadOptions, true);

                long rowsCount = (long) countCriteria.uniqueResult();

                if (rowsCount > 0) {
                        Criteria criteria = session.createCriteria(dalEntityClass);
                        fillCriteria(criteria, loadOptions, false);

                        List<T> dataEntities = criteria.list();
                        for (T de : dataEntities) {
                                K domainEntity = (K) mappingService.map(de, domainEntityClass);
                                result.add(domainEntity);
                        }
                }

                result.setTotalSize(rowsCount);
        } catch (Exception ex) {
                LOG.error("Error occured in BaseDataProviderImpl", ex);
        }

        return result;
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
