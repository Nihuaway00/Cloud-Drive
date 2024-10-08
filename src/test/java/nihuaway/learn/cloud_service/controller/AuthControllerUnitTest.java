package nihuaway.learn.cloud_service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Jwk;
import io.jsonwebtoken.security.SignatureAlgorithm;
import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import nihuaway.learn.cloud_service.config.SecurityConfig;
import nihuaway.learn.cloud_service.dto.JwtResponse;
import nihuaway.learn.cloud_service.dto.UserLoginDto;
import nihuaway.learn.cloud_service.dto.UserRegisterDto;
import nihuaway.learn.cloud_service.entity.User;
import nihuaway.learn.cloud_service.repository.UserRepository;
import nihuaway.learn.cloud_service.util.JwtUtil;
import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.security.Key;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.data.redis.connection.ReactiveStreamCommands.AddStreamRecord.body;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerUnitTest {
	@MockBean
	private UserRepository userRepository;

	@Autowired
	private MockMvc mockMvc;

	@Test
	void successRegister() throws Exception {
		UserRegisterDto dto = UserRegisterDto.builder()
				.username("username@gmail.com")
				.password("A1b2c3")
				.build();

		ObjectMapper mapper = new ObjectMapper();
		mockMvc.perform(post("/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(dto)))
				.andExpect(status().isOk());

		verify(userRepository).save(any(User.class));
	}

	@Test
	void userAlreadyRegistered() throws Exception {
		UserRegisterDto dto = UserRegisterDto.builder()
				.username("username@gmail.com")
				.password("A1b2c3")
				.build();

		User duplicate = new User(1L, dto.getUsername(), dto.getPassword());
		when(userRepository.findByUsername(dto.getUsername())).thenReturn(Optional.of(duplicate));

		ObjectMapper mapper = new ObjectMapper();
		mockMvc.perform(post("/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(dto)))
				.andExpect(status().isBadRequest());

		verify(userRepository).findByUsername(anyString());

	}


	@Test
	void BadUsernameRegister() throws Exception {
		UserRegisterDto dto = UserRegisterDto.builder()
				.username("usernameNotEMail")
				.password("Password123")
				.build();

		ObjectMapper mapper = new ObjectMapper();
		mockMvc.perform(post("/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(dto)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void BadPasswordRegister() throws Exception {
		UserRegisterDto dto = UserRegisterDto.builder()
				.username("username@gmail.com")
				.password(".,.")
				.build();

		ObjectMapper mapper = new ObjectMapper();
		mockMvc.perform(post("/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(dto)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void successLogin() throws Exception {
		UserLoginDto dto = UserLoginDto.builder()
				.username("username@gmail.com")
				.password("Password123")
				.build();

		User root = new User(1L, dto.getUsername(), dto.getPassword());
		when(userRepository.findByUsername(dto.getUsername())).thenReturn(Optional.of(root));

		ObjectMapper mapper = new ObjectMapper();
		MvcResult result = mockMvc.perform(post("/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(dto)))
				.andExpect(status().isOk())
				.andReturn();

		String content = result.getResponse().getContentAsString();

		assertNotNull(content);
		assertNotEquals("", content);
		JwtResponse jwtResponse = mapper.readValue(content, JwtResponse.class);
		assertNotNull(jwtResponse);

		assertDoesNotThrow(() -> Jwts.parser()
				.verifyWith(JwtUtil.getSecretKey())
				.build()
				.parse(jwtResponse.getAccessToken()));

		assertDoesNotThrow(() -> Jwts.parser()
				.verifyWith(JwtUtil.getSecretKey())
				.build()
				.parse(jwtResponse.getRefreshToken()));

		verify(userRepository).findByUsername(anyString());
	}

	@Test
	void badPasswordLogin() throws Exception {
		UserLoginDto dto = UserLoginDto.builder()
				.username("username@gmail.com")
				.password("fakePass")
				.build();


		User root = new User(1L, dto.getUsername(), "truePass123");
		when(userRepository.findByUsername(dto.getUsername())).thenReturn(Optional.of(root));

		ObjectMapper mapper = new ObjectMapper();
		mockMvc.perform(post("/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(dto)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void userNotRegistered() throws Exception {
		UserLoginDto dto = UserLoginDto.builder()
				.username("notexists@gmail.com")
				.password("Password123")
				.build();

		when(userRepository.findByUsername(dto.getUsername())).thenReturn(Optional.empty());

		ObjectMapper mapper = new ObjectMapper();
		mockMvc.perform(post("/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(dto)))
				.andExpect(status().isBadRequest());
	}
}