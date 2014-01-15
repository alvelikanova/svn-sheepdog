package com.sheepdog.business.services;

import com.sheepdog.business.domain.entities.File;

public interface FileManagementService {
	void saveFile(File file);
	void updateFilesRevision(File file);
}
