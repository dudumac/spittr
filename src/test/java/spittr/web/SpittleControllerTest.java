package spittr.web;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.Filter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.MethodMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.view.InternalResourceView;

import spittr.Spittle;
import spittr.config.SecurityConfig;
import spittr.config.TestConfig;
import spittr.config.WebConfig;
import spittr.data.SpittleRepository;
import spittr.exception.DuplicateSpittleException;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { WebConfig.class, SecurityConfig.class, TestConfig.class })
@DirtiesContext(methodMode=MethodMode.AFTER_METHOD)
public class SpittleControllerTest {

	@Autowired
	private Filter springSecurityFilterChain;

	@Autowired
	SpittrWebAppExceptionHandler spittrWebAppExceptionHandler;

	SpittleRepository mockRepository;
	
	MockMvc mockMvc;
	
	@Before
	public void setUp() {
		mockRepository = mock(SpittleRepository.class);
		SpittleController spittleController = new SpittleController(mockRepository);
		
		mockMvc = standaloneSetup(spittleController)
				.setSingleView(new InternalResourceView("/WEB-INF/views/spittles.jsp"))
				.addFilters(springSecurityFilterChain)
				.setControllerAdvice(spittrWebAppExceptionHandler)
				.build();
	}
	
	@Test
	public void testGetSpittles() throws Exception {
		List<Spittle> expectedSpittles = createSpittleList(20);

		when(mockRepository.findSpittles(Long.MAX_VALUE, 20)).thenReturn(expectedSpittles);
		mockMvc.perform(get("/spittles"))
				.andExpect(view().name("spittles"))
				.andExpect(model().attributeExists("spittleList"))
				.andExpect(model().attribute("spittleList", hasItems(expectedSpittles.toArray())));

	}

	@Test
	public void shouldShowPagedSpittles() throws Exception {
		List<Spittle> expectedSpittles = createSpittleList(50);
		when(mockRepository.findSpittles(238900, 50)).thenReturn(expectedSpittles);

		mockMvc.perform(get("/spittles?max=238900&count=50"))
				.andExpect(view().name("spittles"))
				.andExpect(model().attributeExists("spittleList"))
				.andExpect(model().attribute("spittleList", hasItems(expectedSpittles.toArray())));
	}

	/*
	 * As a general rule, query parameters should not be used to identify a
	 * resource. A GET request for /spittles/12345 is better than one for
	 * /spittles/show?spittle_id=12345.
	 */
	@Test
	public void testSpittle() throws Exception {
		Spittle expectedSpittle = new Spittle("Hello", new Date());
		when(mockRepository.findOne(12345)).thenReturn(expectedSpittle);
		
		mockMvc.perform(get("/spittles/12345"))
				.andExpect(view().name("spittle"))
				.andExpect(model().attributeExists("spittle"))
				.andExpect(model().attribute("spittle", expectedSpittle));
	}

	@Test
	public void testSpittleNotFoundErrorStatusCodeReturned() throws Exception {
		when(mockRepository.findOne(12345)).thenReturn(null);

		mockMvc.perform(get("/spittles/12345"))
				.andExpect(status().isNotFound());
				
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testPostSpittleReturnsSpittleView() throws Exception {
		mockMvc.perform(post("/spittles/1")
				.with(user("admin").password("password")))
				.andExpect(status().is2xxSuccessful())
				.andExpect(view().name("redirect:/spittles"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testPostWithInvalidCredentialsFails() throws Exception {
		mockMvc.perform(post("/spittles/1").with(httpBasic("baduser","password")))
				.andExpect(status().is4xxClientError());
//				.andExpect(view().name("redirect:/spittles"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testPostDuplicateSpittleReturnsDuplicateErrorView() throws Exception {
		when(mockRepository.save(Matchers.any())).thenThrow(DuplicateSpittleException.class);
		
		mockMvc.perform(post("/spittles/1")
				.with(user("admin").password("password")))
				.andExpect(view().name("error/duplicate"));
	}

	private List<Spittle> createSpittleList(int count) {
		List<Spittle> spittles = new ArrayList<Spittle>();
		for (int i = 0; i < count; i++) {
			spittles.add(new Spittle("Spittle" + i, new Date()));
		}
		return spittles;
	}



}
