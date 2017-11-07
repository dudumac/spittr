package spittr.web;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class HomeControllerTest {

	@Test
	public void testHomePage() {
		HomeController controller = new HomeController();
		assertEquals("home", controller.home());
	}
}
