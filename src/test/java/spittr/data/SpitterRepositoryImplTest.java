package spittr.data;

import static org.junit.Assert.assertTrue;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import spittr.Spitter;
import spittr.config.DataSourceConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { DataSourceConfig.class})
@SpringBootTest(classes = { DataSourceConfig.class})
@ActiveProfiles("development")
public class SpitterRepositoryImplTest {
	@Autowired
	private DataSource dataSource;

	@Autowired
	private SpitterRepository repository;
		
	private JdbcOperations jdbcOperations;
	
	@Before
	public void setUp() {
		jdbcOperations = new JdbcTemplate(dataSource); 
	}
	
	@Test
	public void testAddSpitter() throws Exception {
		Spitter unsaved = new Spitter(2L, "username", "password", "firstName", "lastName");
		
		repository.save(unsaved);
		String sql = "select id, username, password, firstname, lastname "
				+ "from spitter where id = ?";

		Spitter saved = jdbcOperations.queryForObject(sql, (rs, rowNum) -> {
			return new Spitter(rs.getLong("id"), rs.getString("username"), rs.getString("password"),
					rs.getString("firstname"), rs.getString("lastname"));
		}, 2);
		assertTrue(unsaved.equals(saved));
		
	}


}
