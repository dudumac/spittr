package spittr.data;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import spittr.Spitter;

@Repository
public class SpitterRepositoryImpl implements SpitterRepository {
	private NamedParameterJdbcOperations jdbcOperations;

	private static final String INSERT_SPITTER = "insert into spitter (id, username, password, firstname, lastname) "
			+ "values (:id, :username, :password, :firstName, :lastName)";

	private static final String SELECT_SPITTER_BY_ID = "select id, username, password, firstname, lastname "
			+ "from spitter where id = :id";

	private static final String SELECT_SPITTER_BY_USERNAME = "select id, username, password, firstname, lastname "
			+ "from spitter where username like :username";

	@Autowired
	public SpitterRepositoryImpl(NamedParameterJdbcOperations jdbcOperations) {
		this.jdbcOperations = jdbcOperations;
	}

	@Override
	public Spitter save(Spitter spitter) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("id", spitter.getId());
		parameters.put("username", spitter.getUsername());
		parameters.put("password", spitter.getPassword());
		parameters.put("firstName", spitter.getFirstName());
		parameters.put("lastName", spitter.getLastName());

		jdbcOperations.update(INSERT_SPITTER, parameters);
		return spitter;
	}

	@Override
	public Spitter findByUsername(String username) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("username", username);

		return jdbcOperations.queryForObject(SELECT_SPITTER_BY_USERNAME, parameters, (rs, rowNum) -> {
			return new Spitter(rs.getLong("id"), rs.getString("username"), rs.getString("password"),
					rs.getString("firstname"), rs.getString("lastname"));
		});
	}

	public Spitter findOne(long id) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("id", id);

		return jdbcOperations.queryForObject(SELECT_SPITTER_BY_ID, parameters, (rs, rowNum) -> {
			return new Spitter(rs.getLong("id"), rs.getString("username"), rs.getString("password"),
					rs.getString("firstname"), rs.getString("lastname"));
		});
	}
}