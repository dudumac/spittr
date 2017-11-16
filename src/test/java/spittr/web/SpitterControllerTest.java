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

import java.io.IOException;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import spittr.Spitter;
import spittr.data.SpitterRepository;

@Configuration
public class SpitterControllerTest {

	@Test
	public void testGetRegisterForm() throws Exception {
		SpitterRepository mockRepository = mock(SpitterRepository.class);
		SpitterController controller = new SpitterController(mockRepository);

		MockMvc mockMvc = standaloneSetup(controller).build();

		mockMvc.perform(get("/spitter/register")).andExpect(view().name("registerForm"));

	}

	@Test
	public void shouldProcessRegistration() throws Exception {
		SpitterRepository mockRepository = mock(SpitterRepository.class);
		Spitter unsaved = new Spitter("jbauer", "24hours", "Jack", "Bauer");
		Spitter saved = new Spitter(24L, "jbauer", "24hours", "Jack", "Bauer");

		when(mockRepository.save(unsaved)).thenReturn(saved);

		SpitterController controller = new SpitterController(mockRepository);
		MockMvc mockMvc = standaloneSetup(controller).build();
		// mockMvc.perform(post("/spitter/register")
		// .param("username", "jbauer")
		// .param("password", "24hours")
		// .param("firstName", "Jack")
		// .param("lastName", "Bauer"))
		// .andExpect(redirectedUrl("/spitter/jbauer"));

		MockMultipartFile jpgUploadMock = new MockMultipartFile("name", "".getBytes());
		mockMvc.perform(MockMvcRequestBuilders.fileUpload("")
				.file(jpgUploadMock)
				.param("username", "jbauer")
				.param("password", "24hours")
				.param("firstName", "Jack")
				.param("lastName", "Bauer"))
				.andExpect(redirectedUrl("/spitter/jbauer"));
		
		verify(mockRepository, atLeastOnce()).save(unsaved);
	}

	@Test
	public void shouldReturnRegistrationFormWhenInvalidInputReceived() throws Exception {
		SpitterRepository mockRepository = mock(SpitterRepository.class);
		SpitterController controller = new SpitterController(mockRepository);
		MockMvc mockMvc = standaloneSetup(controller).build();
		mockMvc.perform(post("/spitter/register").param("username", "j").param("password", "24hours")
				.param("firstName", "J").param("lastName", "Bauer")).andExpect(view().name("registerForm"));
		verify(mockRepository, never()).save(any());
	}

	@Test
	public void shouldShowProfile() throws Exception {
		Spitter expectedSpitter = new Spitter(24L, "jbauer", "24hours", "Jack", "Bauer");

		SpitterRepository mockRepository = mock(SpitterRepository.class);
		when(mockRepository.findByUsername("jbauer")).thenReturn(expectedSpitter);

		SpitterController controller = new SpitterController(mockRepository);
		MockMvc mockMvc = standaloneSetup(controller).build();
		mockMvc.perform(post("/spitter/jbauer")).andExpect(view().name("profile"))
				.andExpect(model().attributeExists("spitter")).andExpect(model().attribute("spitter", expectedSpitter));
	}
	
	@Bean(name="multipartResolver")
	public MultipartResolver multipartResolver() throws IOException {
		return new StandardServletMultipartResolver();
	}
}
