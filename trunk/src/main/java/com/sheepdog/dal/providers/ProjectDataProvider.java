package com.sheepdog.dal.providers;

import java.util.List;

import com.sheepdog.business.domain.entities.Project;
import com.sheepdog.dal.entities.ProjectEntity;
import com.sheepdog.dal.providers.pagination.LoadOptions;
import com.sheepdog.dal.providers.pagination.PagedList;

public interface ProjectDataProvider {
	ProjectEntity findProjectById(Integer id);
	ProjectEntity findProjectByUrl(String url);
	void createProject(ProjectEntity projectEntity);
	void deleteProjectById(Integer projectId);
	List<ProjectEntity> findAllProjects();
	PagedList<Project> getProjectBusinessObjects(LoadOptions loadOptions);
}
