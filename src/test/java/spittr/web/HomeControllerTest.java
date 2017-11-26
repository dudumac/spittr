package spittr.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import javax.servlet.Filter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.view.InternalResourceView;

import spittr.config.SecurityConfig;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { SecurityConfig.class})
public class HomeControllerTest {

	@Autowired
	private Filter springSecurityFilterChain;

	HomeController controller;
	MockMvc mockMvc;
		
	@Before
	public void setUp() {
		controller = new HomeController();
		
		mockMvc = standaloneSetup(controller)
				.addFilters(springSecurityFilterChain)
				.setSingleView(new InternalResourceView("/WEB-INF/views/home.jsp"))
				.build();		
	}
	
	@Test
	public void testDefaultRoute() throws Exception {		
		mockMvc.perform(get("/"))
			.andExpect(view().name("home"));
	}

	@Test
	public void testHomePage() throws Exception {
		mockMvc.perform(get("/homepage"))
			.andExpect(view().name("home"));
	}
}
