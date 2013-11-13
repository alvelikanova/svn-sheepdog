package com.sample.frontend.beans.students;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sample.business.domain.entities.Student;
import com.sample.dal.providers.StudentsDataProvider;
import com.sample.dal.providers.pagination.LoadOptions;
import com.sample.dal.providers.pagination.PagedList;
import com.sample.utils.annotations.SessionScope;
import com.sample.utils.frontend.TableDataModel;

@Service
@SessionScope
public class LazyStudentsDataModel extends TableDataModel<Student> {

	private static final long serialVersionUID = -544764175489727755L;

	@Autowired
	private StudentsDataProvider dataProvider;

	@Override
	public Object getRowKey(Student object) {
		return object.getId();
	}

	@Override
	protected PagedList<Student> getData(LoadOptions loadOptions) {
		return dataProvider.getObjects(loadOptions);
	}

}
