package spittr.web;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import spittr.Spitter;
import spittr.config.TestConfig;
import spittr.config.WebConfig;
import spittr.data.SpitterRepository;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { WebConfig.class, TestConfig.class })
public class SpitterControllerTest {

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	SpitterRepository mockSpitterRpositry;

	private MockMvc mockMvc;

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
				.build();
	}

	@Test
	public void testGetRegisterForm() throws Exception {
		SpitterRepository mockRepository = mock(SpitterRepository.class);
		SpitterController controller = new SpitterController(mockRepository);

		MockMvc mockMvc = standaloneSetup(controller).build();

		mockMvc.perform(get("/spitter/register"))
				.andExpect(view().name("registerForm"));

	}

	@Test
	public void shouldProcessRegistration() throws Exception {
		Spitter unsaved = new Spitter("jbauer", "24hours", "Jack", "Bauer");
		Spitter saved = new Spitter(24L, "jbauer", "24hours", "Jack", "Bauer");
		when(mockSpitterRpositry.save(unsaved)).thenReturn(saved);

		MockMultipartFile jpgUploadMock = new MockMultipartFile("profilePicture", "mytestfile.txt", "txt/plain",
				"".getBytes());
		mockMvc.perform(MockMvcRequestBuilders.fileUpload("/spitter/register")
				.file(jpgUploadMock)
				.param("username", "jbauer")
				.param("password", "24hours")
				.param("firstName", "Jack")
				.param("lastName", "Bauer"))
				.andExpect(redirectedUrl("/spitter/jbauer"));

		verify(mockSpitterRpositry, atLeastOnce()).save(unsaved);
	}

	@Test
	public void shouldReturnRegistrationFormWhenInvalidInputReceived() throws Exception {
		MockMultipartFile jpgUploadMock = new MockMultipartFile("profilePicture", "mytestfile.txt", "txt/plain",
				"".getBytes());
		mockMvc.perform(MockMvcRequestBuilders.fileUpload("/spitter/register")
				.file(jpgUploadMock)
				.param("username", "j")
				.param("password", "24hours")
				.param("firstName", "J")
				.param("lastName", "Bauer"))
				.andExpect(view().name("registerForm"));
		verify(mockSpitterRpositry, never()).save(any());
	}

	@Test
	public void shouldShowProfile() throws Exception {
		Spitter expectedSpitter = new Spitter(24L, "jbauer", "24hours", "Jack", "Bauer");

		SpitterRepository mockRepository = mock(SpitterRepository.class);
		when(mockRepository.findByUsername("jbauer")).thenReturn(expectedSpitter);

		SpitterController controller = new SpitterController(mockRepository);
		MockMvc mockMvc = standaloneSetup(controller).build();
		mockMvc.perform(post("/spitter/jbauer"))
				.andExpect(view().name("profile"))
				.andExpect(model().attributeExists("spitter"))
				.andExpect(model().attribute("spitter", expectedSpitter));
	}
}
