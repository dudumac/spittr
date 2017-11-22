package spittr.config;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

public class SecurityWebInitializer extends AbstractSecurityWebApplicationInitializer{

	    public SecurityWebInitializer() {
	        super(SecurityConfig.class);
	}
}