package com.sheepdog.dal.providers.impl;

import org.hibernate.Criteria;
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
					.add(Restrictions.eq("URL", url));
			cr.setMaxResults(1);
			ProjectEntity projectEntity = (ProjectEntity) cr.uniqueResult();
			project = mappingService.map(projectEntity, Project.class);	
		} catch (Exception ex) {
			LOG.error("Error loading project", ex.getMessage());
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
					.add(Restrictions.eq("NAME", projectName));
			cr.setMaxResults(1);
			ProjectEntity projectEntity = (ProjectEntity) cr.uniqueResult();
			project = mappingService.map(projectEntity, Project.class);	
		} catch (Exception ex) {
			LOG.error("Error loading project", ex.getMessage());
		}
		return project;
	}

	@Transactional
	@Override
	public Project getCurrentProject() {
		Project project = null;
		Criteria cr = sessionFactory.getCurrentSession()
				.createCriteria(ProjectEntity.class);
		cr.setMaxResults(1);
		ProjectEntity pe = (ProjectEntity)cr.uniqueResult();
		project = mappingService.map(pe, Project.class);
		return project;
	}

}
