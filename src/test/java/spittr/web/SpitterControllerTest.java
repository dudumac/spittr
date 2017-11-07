package spittr.web;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.util.Date;

import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;

import spittr.Spitter;
import spittr.Spittle;
import spittr.data.SpitterRepository;
import spittr.data.SpittleRepository;

public class SpitterControllerTest {

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
		SpitterRepository mockRepository = mock(SpitterRepository.class);
		Spitter unsaved = new Spitter("jbauer", "24hours", "Jack", "Bauer");
		Spitter saved = new Spitter(24L, "jbauer", "24hours", "Jack", "Bauer");
		
		when(mockRepository.save(unsaved)).thenReturn(saved);
		
		SpitterController controller = new SpitterController(mockRepository);
		MockMvc mockMvc = standaloneSetup(controller).build();
		mockMvc.perform(post("/spitter/register")
				.param("username", "jbauer")
				.param("password", "24hours")
				.param("firstName", "Jack")
				.param("lastName", "Bauer"))
				.andExpect(redirectedUrl("/spitter/jbauer"));
		verify(mockRepository, atLeastOnce()).save(unsaved);
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
