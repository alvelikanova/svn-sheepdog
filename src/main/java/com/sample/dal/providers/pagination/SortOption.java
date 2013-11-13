package com.sample.dal.providers.pagination;

import java.io.Serializable;

public class SortOption implements Comparable<SortOption>, Serializable {

	private static final long serialVersionUID = 8968429521664004650L;

	private String sortField;
	private SortDirection sortDirection;
	private int rank;

	public String getSortField() {
		return sortField;
	}

	public void setSortField(String sortField) {
		this.sortField = sortField;
	}

	public SortDirection getSortDirection() {
		return sortDirection;
	}

	public void setSortDirection(SortDirection sortDirection) {
		this.sortDirection = sortDirection;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	@Override
	public int compareTo(SortOption rhs) {
		if (rhs == null)
			return 1;

		if (rank == rhs.getRank())
			return 0;

		if (rank < rhs.getRank())
			return -1;

		return 1;
	}
}
