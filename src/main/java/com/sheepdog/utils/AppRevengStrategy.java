package com.sheepdog.utils;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.cfg.reveng.DelegatingReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.TableIdentifier;

/**
 * Application reveng strategy to apply *Entity suffix for every hibernate POJO
 * mapping during generation
 */
public class AppRevengStrategy extends DelegatingReverseEngineeringStrategy {

	private static final String ENTITY_SUFFIX = "Entity";

	private static final List<String> EXCLUDE_TABLES;

	static {
		EXCLUDE_TABLES = new ArrayList<>();
		EXCLUDE_TABLES.add("DATABASECHANGELOG");
		EXCLUDE_TABLES.add("DATABASECHANGELOGLOCK");
	}

	public AppRevengStrategy(ReverseEngineeringStrategy delegate) {
		super(delegate);
	}

	@Override
	public String tableToClassName(TableIdentifier tableIdentifier) {
		String baseName = super.tableToClassName(tableIdentifier);

		return baseName + ENTITY_SUFFIX;
	}

	@Override
	public boolean excludeTable(TableIdentifier ti) {
		boolean exclude = super.excludeTable(ti);

		if (EXCLUDE_TABLES.contains(ti.getName())) {
			return true;
		}

		return exclude;
	}

}
