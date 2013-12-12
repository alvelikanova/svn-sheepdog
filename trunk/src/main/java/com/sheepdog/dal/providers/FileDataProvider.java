package com.sheepdog.dal.providers;

import com.sheepdog.business.domain.entities.File;
import com.sheepdog.dal.entities.FileEntity;
import com.sheepdog.dal.providers.base.GenericDataProvider;

public interface FileDataProvider extends GenericDataProvider<FileEntity, File, Integer> {
	File findFileByQualifiedName(String qualifiedName);
}
