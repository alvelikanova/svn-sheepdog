package com.sheepdog.dal.providers.impl;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sheepdog.business.domain.entities.Project;
import com.sheepdog.dal.entities.ProjectEntity;
import com.sheepdog.dal.providers.ProjectDataProvider;

@Repository
public class ProjectDataProviderImpl extends BaseDataProviderImpl<ProjectEntity, Project, Integer> implements ProjectDataProvider{

	@Transactional
	@Override
	public Project findProjectByUrl(String url) {
		Project project = null;
		try{
			Criteria cr = sessionFactory.getCurrentSession()
					.createCriteria(ProjectEntity.class)
					.add(Restrictions.eq("url", url));
			cr.setMaxResults(1);
			ProjectEntity projectEntity = (ProjectEntity) cr.uniqueResult();
			project = mappingService.map(projectEntity, Project.class);	
		} catch (HibernateException ex) {
        	LOG.error("Hibernate error occured while loading project by url", ex.getMessage());
        } catch (Exception ex) {
			LOG.error("Unknown error occured while loading project by url", ex.getMessage());
		}
		return project;
	}

	@Transactional
	@Override
	public Project findProjectByName(String projectName) {
		Project project = null;
		try{
			Criteria cr = sessionFactory.getCurrentSession()
					.createCriteria(ProjectEntity.class)
					.add(Restrictions.eq("name", projectName));
			cr.setMaxResults(1);
			ProjectEntity projectEntity = (ProjectEntity) cr.uniqueResult();
			project = mappingService.map(projectEntity, Project.class);	
		} catch (HibernateException ex) {
        	LOG.error("Hibernate error occured while loading project by name", ex.getMessage());
        } catch (Exception ex) {
			LOG.error("Unknown error occured while loading project by name", ex.getMessage());
		}
		return project;
	}

	@Transactional
	@Override
	public Project getCurrentProject() {
		Project project = null;
		try {
			Criteria cr = sessionFactory.getCurrentSession()
					.createCriteria(ProjectEntity.class);
			cr.setMaxResults(1);
			ProjectEntity pe = (ProjectEntity)cr.uniqueResult();
			project = mappingService.map(pe, Project.class);
		} catch (HibernateException ex) {
        	LOG.error("Hibernate error occured while loading current project", ex.getMessage());
        } catch (Exception ex) {
			LOG.error("Unknown error occured while loading current project", ex.getMessage());
		}
		return project;
	}

}
