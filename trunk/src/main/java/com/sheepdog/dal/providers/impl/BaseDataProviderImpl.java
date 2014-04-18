package com.sheepdog.dal.providers.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sheepdog.business.domain.entities.PersistentEntity;
import com.sheepdog.dal.entities.GenericDalEntity;
import com.sheepdog.dal.exceptions.ConstraintViolationDaoException;
import com.sheepdog.dal.exceptions.DaoException;
import com.sheepdog.infrastructure.services.MappingService;
import com.sheepdog.utils.CollectionUtils;
import com.sheepdog.dal.providers.pagination.FilterOption;
import com.sheepdog.dal.providers.pagination.SortOption;
import com.sheepdog.dal.providers.base.PageableDataProvider;
import com.sheepdog.dal.providers.pagination.LoadOptions;
import com.sheepdog.dal.providers.pagination.PagedList;

@Repository
public abstract class BaseDataProviderImpl<T extends GenericDalEntity<ID>, 
											K extends PersistentEntity<ID>, 
											ID extends Serializable> 
		implements PageableDataProvider<T, K, ID>{

	protected static final Logger LOG = LoggerFactory.getLogger(BaseDataProviderImpl.class);

	@Autowired
	protected SessionFactory sessionFactory;
	
	@Autowired
	protected MappingService mappingService;
	
	@Transactional
	@Override
	public PagedList<K> getObjects(LoadOptions loadOptions, Class<T> dalEntityClass, Class<K> domainEntityClass) throws DaoException {
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
        } catch (HibernateException ex) {
        	LOG.error("Hibernate error occured while getting objects", ex.getMessage());
        	throw new DaoException(ex);
        } catch (Exception ex) {
        	LOG.error("Unknown error occured while getting objects", ex.getMessage());
        	throw new DaoException(ex);
        }

        return result;
	}

	@Transactional
	@Override
	public void save(K entity, Class<T> dalEntityClass) throws DaoException {
		Session session = sessionFactory.getCurrentSession();

		try {
			T dataEntity = mappingService.map(entity, dalEntityClass);
			session.saveOrUpdate(dataEntity);
			entity.setId(dataEntity.getId());
		} catch (ConstraintViolationException ex) {
			LOG.error("Constraint violation error occured while creating or updating data entity", ex.getMessage());
        	throw new ConstraintViolationDaoException(ex);
		} catch (HibernateException ex) {
        	LOG.error("Hibernate error occured while creating or updating data entity", ex.getMessage());	
        	throw new DaoException(ex);
		} catch (Exception ex) {
			LOG.error("Unknown error occured while creating or updating data entity", ex.getMessage());
			throw new DaoException(ex);
		}
	}
	
	@Transactional
	@Override
	public void merge(K entity, Class<T> dalEntityClass) throws DaoException {
		Session session = sessionFactory.getCurrentSession();

		try {
			T dataEntity = mappingService.map(entity, dalEntityClass);
			session.merge(dataEntity);
		} catch (HibernateException ex) {
        	LOG.error("Hibernate error occured while merging data entity", ex.getMessage());	
        	throw new DaoException(ex);
		} catch (Exception ex) {
			LOG.error("Unknown error occured while merging data entity", ex.getMessage());
			throw new DaoException(ex);
		}
	}

	@Transactional
	@Override
	public void delete(K entity, Class<T> dalEntityClass) throws DaoException {
		Session session = sessionFactory.getCurrentSession();

		try {
			T dataEntity = mappingService.map(entity, dalEntityClass);
			session.delete(dataEntity);
		} catch (HibernateException ex) {
        	LOG.error("Hibernate error occured while deleting data entity", ex.getMessage());	
        	throw new DaoException(ex);
		} catch (Exception ex) {
			LOG.error("Unknown error occured while deleting data entity", ex.getMessage());
			throw new DaoException(ex);
		}
	}

	@Transactional
	@Override
	public K findById(Class<T> dalEntityClass, Class<K> domainEntityClass, ID id) throws DaoException {
		Session session = sessionFactory.getCurrentSession();
		K businessEntity = null;
		try {
			Criteria cr = session.createCriteria(dalEntityClass).add(Restrictions.eq("id", id));
			cr.setMaxResults(1);
			@SuppressWarnings("unchecked")
			T dalEntity = (T) cr.uniqueResult();
			businessEntity = mappingService.map(dalEntity, domainEntityClass);
		} catch (HibernateException ex) {
        	LOG.error("Hibernate error occured while loading business entity", ex.getMessage());
        	throw new DaoException(ex);
		} catch (Exception ex) {
			LOG.error("Unknown error occured while loading business entity", ex.getMessage());
			throw new DaoException(ex);
		}
		return businessEntity;
	}

	@Transactional
	@Override
	public List<K> findAll(Class<T> dalEntityClass, Class<K> domainEntityClass) throws DaoException {		
		Session session = sessionFactory.getCurrentSession();

		List<K> businessEntities = new ArrayList<>();

		try {
			@SuppressWarnings("unchecked")
			List<T> dataEntities = session.createCriteria(dalEntityClass).list();

			for (T de : dataEntities) {
				K businessEntity = mappingService.map(de, domainEntityClass);
				businessEntities.add(businessEntity);
			}
		} catch (HibernateException ex) {
        	LOG.error("Hibernate error occured while loading business entities", ex.getMessage());
        	throw new DaoException(ex);
		} catch (Exception ex) {
			LOG.error("Unknown error occured while loading business entities", ex.getMessage());
			throw new DaoException(ex);
		}
		
		return businessEntities;
	}
	
	private void fillCriteria(Criteria source, LoadOptions loadOptions, boolean countOnly) {
		List<FilterOption> filters = loadOptions.getFilters();
		if (CollectionUtils.hasItems(filters)) {
			for (FilterOption f : filters) {
				source.add(Restrictions.like(f.getField(), "%"+f.getValue()+"%"));
			}
		}

		if (countOnly) {
			source.setProjection(Projections.rowCount());
		} else {
			
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
			
			source.setFirstResult(loadOptions.getSkipItems());
			source.setMaxResults(loadOptions.getTakeItems());
		}
	}
}
