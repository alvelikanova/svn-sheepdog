package com.sheepdog.business.services;

import com.sheepdog.business.domain.entities.Project;

public interface ProjectManagementService {
	Project getCurrentProject();

	void deleteCurrentProject();

	void saveProject(Project project);
}
