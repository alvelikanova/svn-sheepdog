package com.sheepdog.infrastructure.services;

public interface MappingService {
	<T> T  map(Object sourceObject, Class<T> targetClass);
}
