package com.sheepdog.dal.providers;

import com.sheepdog.business.domain.entities.Project;
import com.sheepdog.dal.entities.ProjectEntity;
import com.sheepdog.dal.providers.base.GenericDataProvider;

public interface ProjectDataProvider extends GenericDataProvider<ProjectEntity, Project, Integer> {
	Project findProjectByUrl(String url);
	Project findProjectByName(String projectName);
}
