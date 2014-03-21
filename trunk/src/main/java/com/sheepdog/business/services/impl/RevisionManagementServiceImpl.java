package com.sheepdog.business.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sheepdog.business.domain.entities.Revision;
import com.sheepdog.business.services.RevisionManagementService;
import com.sheepdog.dal.entities.RevisionEntity;
import com.sheepdog.dal.providers.RevisionDataProvider;

@Service
public class RevisionManagementServiceImpl implements RevisionManagementService {

	private static final Logger LOG = LoggerFactory.getLogger(RevisionManagementServiceImpl.class);

	@Autowired
	private RevisionDataProvider revisionDataProvider;

	@Override
	public Revision getCurrentRevision() {
		Revision revision = null;
		try {
			revision = revisionDataProvider.getLatestRevision();
		} catch (Exception ex) {
			LOG.error("Error loading revision", ex.getMessage());
		}
		if (revision == null) {
			revision = new Revision();
			revision.setRevisionNo(0);
		}
		return revision;
	}

	@Override
	public void saveRevisions(Set<Revision> r) {
		for (Revision revision : r) {
			if (r != null) {
				revisionDataProvider.save(revision, RevisionEntity.class);
			}
		}
	}

	@Override
	public List<Revision> getAllRevisions() {
		List<Revision> dbRevision = new ArrayList<>(0);

		dbRevision.addAll(revisionDataProvider.findAll(RevisionEntity.class, Revision.class));

		return dbRevision;
	}

}
