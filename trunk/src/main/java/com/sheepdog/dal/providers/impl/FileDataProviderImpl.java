package com.sheepdog.dal.providers.impl;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sheepdog.business.domain.entities.File;
import com.sheepdog.dal.entities.FileEntity;
import com.sheepdog.dal.exceptions.DaoException;
import com.sheepdog.dal.providers.FileDataProvider;

@Repository
public class FileDataProviderImpl extends BaseDataProviderImpl<FileEntity, File, Integer> implements FileDataProvider{

	@Transactional
	@Override
	public File findFileByQualifiedName(String qualifiedName) throws DaoException {
		File file = null;
		try{
			Criteria cr = sessionFactory.getCurrentSession()
					.createCriteria(FileEntity.class)
					.add(Restrictions.eq("qualifiedName", qualifiedName));
			cr.setMaxResults(1);
			FileEntity fileEntity = (FileEntity) cr.uniqueResult();
			file = mappingService.map(fileEntity, File.class);
		} catch (HibernateException ex) {
        	LOG.error("Hibernate error occured while loading file by qualified name", ex.getMessage());
        	throw new DaoException(ex);
        } catch (Exception ex) {
			LOG.error("Unknown error occured while loading file by qualified name", ex.getMessage());
			throw new DaoException(ex);
		}
		return file;
	}

}
