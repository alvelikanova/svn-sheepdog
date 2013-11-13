package com.sample.dal.providers.impl;

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
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sample.business.domain.entities.Student;
import com.sample.dal.entities.StudentEntity;
import com.sample.dal.entities.UserEntity;
import com.sample.dal.exceptions.DaoException;
import com.sample.dal.providers.StudentsDataProvider;
import com.sample.dal.providers.pagination.FilterOption;
import com.sample.dal.providers.pagination.LoadOptions;
import com.sample.dal.providers.pagination.PagedList;
import com.sample.dal.providers.pagination.SortOption;
import com.sample.infrastructure.services.MappingService;
import com.sample.utils.CollectionUtils;
import com.sample.utils.annotations.DefaultProfile;

@Repository
@DefaultProfile
public class StudentsDataProviderImpl implements StudentsDataProvider {

	private static final Logger LOG = LoggerFactory.getLogger(StudentsDataProviderImpl.class);

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private MappingService mappingService;

	@Transactional
	@Override
	public PagedList<Student> getObjects(LoadOptions loadOptions) {
		Session session = sessionFactory.getCurrentSession();

		PagedList<Student> result = new PagedList<>();

		try {
			Criteria countCriteria = session.createCriteria(StudentEntity.class);
			fillCriteria(countCriteria, loadOptions, true);

			long rowsCount = (long) countCriteria.uniqueResult();

			if (rowsCount > 0) {
				Criteria criteria = session.createCriteria(StudentEntity.class);
				fillCriteria(criteria, loadOptions, false);

				List<StudentEntity> dataEntities = criteria.list();
				for (StudentEntity de : dataEntities) {
					Student student = mappingService.map(de, Student.class);
					result.add(student);
				}
			}

			result.setTotalSize(rowsCount);
		} catch (Exception ex) {
			LOG.error("Error loading students", ex);
		}

		return result;
	}

	@Transactional
	@Override
	public void create(Student object) throws DaoException {
		Session session = sessionFactory.getCurrentSession();

		try {
			StudentEntity dataEntity = mappingService.map(object, StudentEntity.class);
			UserEntity userDataEntity = dataEntity.getUserEntity();
			session.save(userDataEntity);
			session.save(dataEntity);
		} catch (Exception ex) {
			LOG.error("Error saving new student", ex);
			throw new DaoException(ex);
		}
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
