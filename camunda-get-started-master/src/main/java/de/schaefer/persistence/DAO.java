package de.schaefer.persistence;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import de.schaefer.mdbpmn.persistence.MDBPMN_DAO;

public class DAO implements MDBPMN_DAO {
	
	private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa-example");

	@Override
	public Object read(Class<?> clazz, int id) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction();
		em.getTransaction().begin();
		return em.find(clazz, id);
	}
	
	@Override
	public List<Object> save(List<Object> objects) {
	    EntityManager em = emf.createEntityManager();
	    em.getTransaction().begin();
	    for (int i=0; i < objects.size(); i++) {
	    	objects.set(i, em.merge(objects.get(i)));
	    }
	    em.getTransaction().commit();
	    em.close();
	    return objects;
	}
}
