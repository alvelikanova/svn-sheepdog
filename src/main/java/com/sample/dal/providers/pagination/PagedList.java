package com.sample.dal.providers.pagination;

import java.util.ArrayList;

import com.sample.dal.providers.base.PageableDataProvider;

/**
 * Used by {@link PageableDataProvider} contract for loading entries. Actual
 * count of entries in this list depends on {@link LoadOptions#getTakeItems()}
 * 
 * @author tillias
 */
public class PagedList<T> extends ArrayList<T> {

	private static final long serialVersionUID = 3540920657092240706L;

	private long totalSize;

	/**
	 * Gets total entries count for the whole selection.
	 */
	public long getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(long totalSize) {
		this.totalSize = totalSize;
	}
}
