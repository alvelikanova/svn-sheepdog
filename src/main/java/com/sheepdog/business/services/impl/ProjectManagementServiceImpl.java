package com.sheepdog.business.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sheepdog.business.domain.entities.Project;
import com.sheepdog.business.services.ProjectManagementService;
import com.sheepdog.dal.entities.ProjectEntity;
import com.sheepdog.dal.providers.ProjectDataProvider;

@Service
public class ProjectManagementServiceImpl implements ProjectManagementService {

	private static final Logger LOG = LoggerFactory.getLogger(ProjectManagementServiceImpl.class);

	@Autowired
	ProjectDataProvider projectDataProvider;
	
	@Override
	public Project getCurrentProject() {
		return projectDataProvider.getCurrentProject();
	}

	@Override
	public void deleteCurrentProject(){
		Project project = projectDataProvider.getCurrentProject();
		projectDataProvider.delete(project, ProjectEntity.class);
	}
	
	@Override
	public void saveProject(Project project){
		projectDataProvider.save(project, ProjectEntity.class);
	}
}
