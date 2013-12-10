package com.sheepdog.dal.providers;

import java.util.List;

import com.sheepdog.business.domain.entities.File;
import com.sheepdog.dal.entities.FileEntity;
import com.sheepdog.dal.providers.pagination.LoadOptions;
import com.sheepdog.dal.providers.pagination.PagedList;

public interface FileDataProvider {
	FileEntity findFileById(Integer id);
	FileEntity findFileByQualifiedName(String qualifiedName);
	void createFile(FileEntity fileEntity);
	void deleteFileById(Integer fileId);
	List<FileEntity> findAllFiles();
	PagedList<File> getFileBusinessObjects(LoadOptions loadOptions);
}
