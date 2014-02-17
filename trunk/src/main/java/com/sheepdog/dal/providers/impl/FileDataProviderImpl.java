package com.sheepdog.dal.providers.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sheepdog.business.domain.entities.File;
import com.sheepdog.dal.entities.FileEntity;
import com.sheepdog.dal.providers.FileDataProvider;

@Repository
public class FileDataProviderImpl extends BaseDataProviderImpl<FileEntity, File, Integer> implements FileDataProvider{

	@Transactional
	@Override
	public File findFileByQualifiedName(String qualifiedName) {
		File file = null;
		try{
			Criteria cr = sessionFactory.getCurrentSession()
					.createCriteria(FileEntity.class)
					.add(Restrictions.eq("qualifiedName", qualifiedName));
			cr.setMaxResults(1);
			FileEntity fileEntity = (FileEntity) cr.uniqueResult();
			file = mappingService.map(fileEntity, File.class);
		} catch (Exception ex){
			LOG.error("Error loading file", ex.getMessage());
		}
		return file;
	}

}
