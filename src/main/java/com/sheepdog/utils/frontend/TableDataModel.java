package com.sheepdog.utils.frontend;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;

import com.sheepdog.dal.providers.pagination.FilterOption;
import com.sheepdog.dal.providers.pagination.LoadOptions;
import com.sheepdog.dal.providers.pagination.PagedList;
import com.sheepdog.dal.providers.pagination.SortDirection;
import com.sheepdog.dal.providers.pagination.SortOption;

public abstract class TableDataModel<T> extends LazyDataModel<T> {

	private static final long serialVersionUID = -6074599821435569629L;

	private int rowCount;

	@Override
	public int getRowCount() {
		return rowCount;
	}

	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}

	@Override
	public List<T> load(int first, int pageSize, List<SortMeta> multiSortMeta, Map<String, String> filters) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<T> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
		LoadOptions loadOptions = convert(first, pageSize, sortField, sortOrder, filters);
		PagedList<T> data = getData(loadOptions);

		Long longSize = Long.valueOf(data.getTotalSize());

		setRowCount(longSize.intValue());

		return data;
	}

	protected abstract PagedList<T> getData(LoadOptions loadOptions);

	private LoadOptions convert(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, String> filters) {
		List<SortMeta> sorts = new ArrayList<>();
		if (!StringUtils.isBlank(sortField)) {
			SortMeta sort = new SortMeta();
			sort.setSortField(sortField);
			sort.setSortOrder(sortOrder);
			sorts.add(sort);
		}

		return convert(first, pageSize, sorts, filters);
	}

	private LoadOptions convert(int first, int pageSize, List<SortMeta> multiSortMeta, Map<String, String> filters) {
		LoadOptions loadOptions = new LoadOptions();
		loadOptions.setSkipItems(first);
		loadOptions.setTakeItems(pageSize);

		List<SortOption> sorts = new ArrayList<>();
		for (int i = 0; i < multiSortMeta.size(); ++i) {
			SortMeta sortMeta = multiSortMeta.get(i);
			if (sortMeta != null) {
				String sortField = sortMeta.getSortField();
				if (!StringUtils.isBlank(sortField)) {
					SortOption sort = new SortOption();
					sort.setSortField(sortField);
					sort.setSortDirection(convert(sortMeta.getSortOrder()));
					sort.setRank(i);
					sorts.add(sort);
				}
			}
		}
		loadOptions.setSorts(sorts);

		List<String> filterFields = new ArrayList<>(filters.keySet());
		List<FilterOption> filterOptions = new ArrayList<>();
		for (String filterField : filterFields) {
			if (!StringUtils.isBlank(filterField)) {
				String filterValue = filters.get(filterField);
				FilterOption filterOption = new FilterOption();
				filterOption.setField(filterField);
				filterOption.setValue(filterValue);
				filterOptions.add(filterOption);
			}
		}
		loadOptions.setFilters(filterOptions);

		return loadOptions;
	}

	private SortDirection convert(SortOrder sortOrder) {
		switch (sortOrder) {
		case ASCENDING:
			return SortDirection.ASCENDING;
		case DESCENDING:
			return SortDirection.DESCENDING;
		case UNSORTED:
			return SortDirection.UNSORTED;
		default:
			throw new IllegalArgumentException("Unknown sort order");
		}
	}
}
