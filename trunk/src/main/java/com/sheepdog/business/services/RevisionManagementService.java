package com.sheepdog.business.services;

import java.util.List;
import java.util.Set;

import com.sheepdog.business.domain.entities.Revision;

public interface RevisionManagementService {
	Revision getCurrentRevision();

	void saveRevisions(Set<Revision> r);

	public List <Revision> getAllRevisions();
}
