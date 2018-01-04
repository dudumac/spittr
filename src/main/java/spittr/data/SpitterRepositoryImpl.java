package spittr.data;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import spittr.Spitter;

@Repository
@Transactional
public class SpitterRepositoryImpl implements SpitterRepository {
	private SessionFactory sessionFactory;

	@Autowired
	public SpitterRepositoryImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	private Session currentSession() {
		return sessionFactory.getCurrentSession();
	}

	public long count() {
		return findAll().size();
	}

	public Spitter save(Spitter spitter) {
		Serializable id = currentSession().save(spitter);
		return new Spitter((Long) id, spitter.getUsername(), spitter.getPassword(), spitter.getFirstName(),
				spitter.getLastName());
	}

	public Spitter findOne(long id) {
		return (Spitter) currentSession().get(Spitter.class, id);
	}

	public Spitter findByUsername(String username) {
		return (Spitter) currentSession().createCriteria(Spitter.class).add(Restrictions.eq("username", username))
				.list().get(0);
	}

	public List<Spitter> findAll() {
		return (List<Spitter>) currentSession().createCriteria(Spitter.class).list();
	}
}