package com.sheepdog.dal.providers;

import com.sheepdog.business.domain.entities.Project;
import com.sheepdog.dal.entities.ProjectEntity;
import com.sheepdog.dal.providers.base.PageableDataProvider;

public interface ProjectDataProvider extends PageableDataProvider<ProjectEntity, Project, Integer> {
	Project findProjectByUrl(String url);
	Project findProjectByName(String projectName);
	Project getCurrentProject();
}
