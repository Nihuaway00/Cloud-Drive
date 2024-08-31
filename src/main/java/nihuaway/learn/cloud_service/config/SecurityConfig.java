package nihuaway.learn.cloud_service.config;

import nihuaway.learn.cloud_service.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				.authorizeHttpRequests(authorize -> authorize
						.requestMatchers("/**").permitAll()
						.anyRequest()
						.authenticated()
				)
				.formLogin(formLogin -> formLogin
						.loginPage("/login")
						.permitAll()
				)
				.userDetailsService(userDetailsService())
				.rememberMe(Customizer.withDefaults());

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return new UserService();
	}
}
