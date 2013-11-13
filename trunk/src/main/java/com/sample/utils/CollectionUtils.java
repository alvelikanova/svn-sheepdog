package com.sample.utils;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

import com.sample.business.domain.entities.PersistentEntity;

/**
 * @author Ilya N. Ternovikh
 * 
 */
public class CollectionUtils {

	public static final String ID_FIELD = "id";

	public static <T> T firstOrDefault(Collection<T> source) {
		if (source == null)
			return null;

		Iterator<T> iterator = source.iterator();
		if (iterator.hasNext()) {
			return iterator.next();
		}

		return null;
	}

	public static <T extends PersistentEntity<?>> T findByID(Collection<T> source, Serializable id) {

		if (source == null || id == null)
			return null;

		Iterator<T> iterator = source.iterator();
		while (iterator.hasNext()) {
			T user = iterator.next();
			if (id.equals(user.getId())) {
				return user;
			}
		}

		return null;
	}

	public static <T> boolean hasItems(Collection<T> source) {
		if (source == null)
			return false;

		return source.size() > 0;
	}
}
