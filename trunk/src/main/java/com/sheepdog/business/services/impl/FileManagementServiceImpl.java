package com.sheepdog.business.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sheepdog.business.domain.entities.File;
import com.sheepdog.business.services.FileManagementService;
import com.sheepdog.dal.entities.FileEntity;
import com.sheepdog.dal.providers.FileDataProvider;

@Service
public class FileManagementServiceImpl implements FileManagementService {

	private static final Logger LOG = LoggerFactory.getLogger(FileManagementServiceImpl.class);

	@Autowired
	private FileDataProvider fileDataProvider;

	@Override
	public void saveFile(File file) {
		fileDataProvider.save(file, FileEntity.class);
	}

	@Override
	public void updateFilesRevision(File file) {
		String qualifiedName = file.getQualifiedName();
		File dbfile = fileDataProvider.findFileByQualifiedName(qualifiedName);
		dbfile.setRevision(file.getRevision());
		fileDataProvider.save(dbfile, FileEntity.class);
	}

	@Override
	public File getFileByQualifiedName(String name) {
		File file = fileDataProvider.findFileByQualifiedName(name);
		return file;
	}

}
