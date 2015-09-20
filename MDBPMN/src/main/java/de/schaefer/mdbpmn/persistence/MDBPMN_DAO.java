package de.schaefer.mdbpmn.persistence;

import java.util.List;

public interface MDBPMN_DAO {

	List<Object> save (List<Object> objects);
	Object read(Class<?> clazz, int id);
}
